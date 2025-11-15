package com.ticket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.ticket.exception.BusinessException;
import com.ticket.exception.ErrorCode;
import com.ticket.mapper.UserAddressMapper;
import com.ticket.model.dto.request.AddressRequestDTO;
import com.ticket.model.dto.response.AddressResponseDTO;
import com.ticket.model.entity.UserAddress;
import com.ticket.service.UserAddressService;
import com.ticket.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserAddressServiceImpl implements UserAddressService {
    @Resource
    private UserAddressMapper addressMapper;

    @Override
    public List<AddressResponseDTO> getAddressList() {
        Long userId = UserContext.getCurrentUserId();
        List<UserAddress> addressList = addressMapper.selectList(new QueryWrapper<UserAddress>().eq("user_id",userId));
        if (CollectionUtils.isEmpty(addressList)) {
            throw new BusinessException(ErrorCode.ADDRESS_OPERATION_FAIL);
        }

        return addressList.stream().map(address -> {
            AddressResponseDTO dto = new AddressResponseDTO();
            BeanUtils.copyProperties(address, dto);
            return dto;
        }).collect(Collectors.toList());

    }

    @Override
    @Transactional
    public void addAddress(AddressRequestDTO request) {
        Long userId = UserContext.getCurrentUserId();
        UserAddress address = new UserAddress();
        BeanUtils.copyProperties(request, address);
        address.setUserId(userId);

        try {
            // 如果设置为默认地址，先取消其他地址的默认状态
            if (request.getIsDefault() == 1) {
                UserAddress update = new UserAddress();
                update.setIsDefault(0);
                addressMapper.update(update, new LambdaQueryWrapper<UserAddress>()
                        .eq(UserAddress::getUserId, userId)
                        .eq(UserAddress::getIsDefault, 1));
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.ADDRESS_OPERATION_FAIL);
        }

        try {
            addressMapper.insert(address);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.ADDRESS_OPERATION_FAIL);
        }
    }

    @Override
    @Transactional
    public void updateAddress(Long addressId, AddressRequestDTO request) {
        Long userId = UserContext.getCurrentUserId();
        UserAddress address = addressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.ADDRESS_NOT_FOUND);
        }

        BeanUtils.copyProperties(request, address);

        try {
            // 如果设置为默认地址，先取消其他地址的默认状态
            if (request.getIsDefault() == 1) {
                UserAddress update = new UserAddress();
                update.setIsDefault(0);
                addressMapper.update(update, new LambdaQueryWrapper<UserAddress>()
                        .eq(UserAddress::getUserId, userId)
                        .eq(UserAddress::getIsDefault, 1)
                        .ne(UserAddress::getId, addressId)); // 排除当前地址
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.ADDRESS_OPERATION_FAIL);
        }


        try {
            addressMapper.updateById(address);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.ADDRESS_OPERATION_FAIL);
        }
    }

    @Override
    public void deleteAddress(Long addressId) {
        Long userId = UserContext.getCurrentUserId();
        UserAddress address = addressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.ADDRESS_NOT_FOUND);
        }

        try {
            addressMapper.deleteById(addressId);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.ADDRESS_OPERATION_FAIL);
        }
    }
}

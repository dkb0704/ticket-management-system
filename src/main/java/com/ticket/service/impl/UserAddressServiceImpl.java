package com.ticket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
        List<UserAddress> addressList = addressMapper.selectByUserId(userId);
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

        // 如果设置为默认地址，先取消其他地址的默认状态
        if (request.getIsDefault() == 1) {
            UserAddress update = new UserAddress();
            update.setIsDefault(0);
            addressMapper.update(update, new LambdaQueryWrapper<UserAddress>()
                    .eq(UserAddress::getUserId, userId)
                    .eq(UserAddress::getIsDefault, 1));
        }
        addressMapper.insert(address);
    }

    @Override
    @Transactional
    public void updateAddress(Long addressId, AddressRequestDTO request) {
        Long userId = UserContext.getCurrentUserId();
        UserAddress address = addressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            log.info("UserAddressServiceImpl的updateAddress: 地址不存在或用户id和地址id不匹配");
        }

        BeanUtils.copyProperties(request, address);

        // 如果设置为默认地址，先取消其他地址的默认状态
        if (request.getIsDefault() == 1) {
            UserAddress update = new UserAddress();
            update.setIsDefault(0);
            addressMapper.update(update, new LambdaQueryWrapper<UserAddress>()
                    .eq(UserAddress::getUserId, userId)
                    .eq(UserAddress::getIsDefault, 1)
                    .ne(UserAddress::getId, addressId)); // 排除当前地址
        }


        addressMapper.updateById(address);
    }

    @Override
    public void deleteAddress(Long addressId) {
        Long userId = UserContext.getCurrentUserId();
        UserAddress address = addressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            log.info("UserAddressServiceImpl的deleteAddress: 地址不存在或用户id和地址id不匹配");

        }

        addressMapper.deleteById(addressId);
    }
}

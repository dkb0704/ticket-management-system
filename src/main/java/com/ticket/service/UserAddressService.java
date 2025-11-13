package com.ticket.service;

import com.ticket.model.dto.request.AddressRequestDTO;
import com.ticket.model.dto.response.AddressResponseDTO;

import java.util.List;

public interface UserAddressService {
    // 获取当前用户的所有收货地址
    List<AddressResponseDTO> getAddressList();

    // 添加新地址
    void addAddress(AddressRequestDTO request);

    // 修改地址（根据地址ID）
    void updateAddress(Long addressId, AddressRequestDTO request);

    // 删除地址（根据地址ID）
    void deleteAddress(Long addressId);
}

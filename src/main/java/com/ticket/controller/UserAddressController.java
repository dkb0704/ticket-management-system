package com.ticket.controller;

import com.ticket.model.dto.request.AddressRequestDTO;
import com.ticket.model.dto.response.AddressResponseDTO;
import com.ticket.model.entity.Result;
import com.ticket.service.UserAddressService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/user/addresses")
public class UserAddressController {

    @Resource
    private UserAddressService addressService;

    /**
     * 获取当前用户的所有收货地址
     */
    @GetMapping
    public Result<List<AddressResponseDTO>> getAddressList() {
        return Result.success(addressService.getAddressList());
    }

    /**
     * 添加新地址
     */
    @PostMapping
    public Result<Void> addAddress(@RequestBody AddressRequestDTO request) {
        addressService.addAddress(request);
        return Result.success();
    }

    /**
     * 修改地址
     */
    @PutMapping("/{addressId}")
    public Result<Void> updateAddress(
            @PathVariable Long addressId,
            @RequestBody AddressRequestDTO request
    ) {
        addressService.updateAddress(addressId, request);
        return Result.success();
    }

    /**
     * 删除地址
     */
    @DeleteMapping("/{addressId}")
    public Result<Void> deleteAddress(@PathVariable Long addressId) {
        addressService.deleteAddress(addressId);
        return Result.success();
    }
}

package com.ticket.model.dto.request;

import lombok.Data;

@Data
public class AddressRequestDTO {
    private String receiver;

    private String phone;

    private String province;

    private String city;

    private String district;

    private String detailAddress;

    private Integer isDefault = 0;
}
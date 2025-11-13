package com.ticket.model.dto.response;

import lombok.Data;

@Data
public class AddressResponseDTO {
    private Long id;
    private String recipient;
    private String phone;
    private String province;
    private String city;
    private String district;
    private String detailAddress;
    private Integer isDefault;
}
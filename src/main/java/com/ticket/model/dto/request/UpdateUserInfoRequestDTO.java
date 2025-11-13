package com.ticket.model.dto.request;

import lombok.Data;

@Data
public class UpdateUserInfoRequestDTO {
    private String username;
    private String email;
    private String phone;
    private String avatar;
}

package com.ticket.model.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserInfoResponseDTO {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String avatar;
}

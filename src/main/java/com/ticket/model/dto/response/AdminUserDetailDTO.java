package com.ticket.model.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdminUserDetailDTO {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String avatar;
    private Integer status;
    private Integer role;
    private LocalDateTime lastLoginTime;
    private LocalDateTime createTime;
}

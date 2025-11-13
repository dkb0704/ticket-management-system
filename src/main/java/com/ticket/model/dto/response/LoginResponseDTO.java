package com.ticket.model.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author dkb
 */
@Data
public class LoginResponseDTO {
    // JWT令牌
    private String token;
    // 用户ID
    private Long userId;
    // 用户名
    private String username;
    // 邮箱
    private String email;
    // 头像
    private String avatar;
    //权限
    private Integer role;
    // 令牌过期时间
    private LocalDateTime expireTime;
}
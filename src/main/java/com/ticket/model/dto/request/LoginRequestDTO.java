package com.ticket.model.dto.request;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author dkb
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDTO {
    // 1-密码登录，2-邮箱登录
    private Integer loginType;
    // 用户名（loginType=1）或邮箱（loginType=2
    private String account;

    private String password;
}


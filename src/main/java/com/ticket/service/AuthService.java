package com.ticket.service;

import com.ticket.model.dto.request.LoginRequestDTO;
import com.ticket.model.dto.response.LoginResponseDTO;
import com.ticket.model.entity.User;

/**
 * @author dkb
 */
public interface AuthService {
    // 密码/邮箱登录
    public LoginResponseDTO login(LoginRequestDTO request);
    // 注册新用户
    public LoginResponseDTO registerUser(LoginRequestDTO request);
    //登出
    void logout();
    //  构建登录响应
    public LoginResponseDTO generateLoginResponse(User user);
}
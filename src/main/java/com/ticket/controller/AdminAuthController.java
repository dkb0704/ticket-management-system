package com.ticket.controller;

import com.ticket.exception.BusinessException;
import com.ticket.exception.ErrorCode;
import com.ticket.model.dto.request.LoginRequestDTO;
import com.ticket.model.dto.response.LoginResponseDTO;
import com.ticket.model.entity.Result;
import com.ticket.model.entity.User;
import com.ticket.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Objects;

@RestController
@RequestMapping("/admin/auth")
public class AdminAuthController {
    @Resource
    private AuthService authService;

    @PostMapping("/login")
    public Result<LoginResponseDTO> adminLogin(@RequestBody LoginRequestDTO request) {
        // 调用登录方法
        LoginResponseDTO response = authService.login(request);
        // 校验是否为管理员角色
        if (!Objects.equals(response.getRole(), User.ROLE_ADMIN)) {
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }
        return Result.success(response);
    }

    // 管理员登出
    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.success();
    }
}
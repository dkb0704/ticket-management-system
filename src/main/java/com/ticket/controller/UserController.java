package com.ticket.controller;

import com.ticket.model.dto.request.LoginRequestDTO;
import com.ticket.model.dto.response.LoginResponseDTO;
import com.ticket.model.entity.Result;
import com.ticket.service.UserService;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author dkb
 */
@RestController
@RequestMapping("/api/auth")
public class UserController {
    @Resource
    private UserService userService;

    //密码/邮箱登录（合并注册）
    @PostMapping("/login")
    public Result<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        LoginResponseDTO response = userService.login(request);
        return Result.success(response);
    }
    //登出
    @PostMapping("/logout")
    public Result<LoginResponseDTO> logout() {
        userService.logout();
        return Result.success();
    }
}

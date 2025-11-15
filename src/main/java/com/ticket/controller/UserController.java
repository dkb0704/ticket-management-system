package com.ticket.controller;

import com.ticket.model.dto.request.UpdateUserInfoRequestDTO;
import com.ticket.model.dto.response.UserInfoResponseDTO;
import com.ticket.model.entity.Result;
import com.ticket.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result<UserInfoResponseDTO> getCurrentUserInfo() {
        return Result.success(userService.getCurrentUserInfo());
    }

    /**
     * 修改个人信息
     */
    @PutMapping("/info")
    public Result<Void> updateUserInfo(@RequestBody UpdateUserInfoRequestDTO request) {
        userService.updateUserInfo(request);
        return Result.success();
    }
}
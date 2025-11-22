package com.ticket.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ticket.model.dto.request.UpdateUserInfoRequestDTO;
import com.ticket.model.dto.request.UserQueryRequestDTO;
import com.ticket.model.dto.response.AdminUserDetailDTO;
import com.ticket.model.dto.response.UserInfoResponseDTO;
import com.ticket.model.entity.Result;
import com.ticket.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 获取当前用户信息
     */
    @GetMapping("/api/user/info")
    public Result<UserInfoResponseDTO> getCurrentUserInfo() {
        return Result.success(userService.getCurrentUserInfo());
    }

    /**
     * 修改个人信息
     */
    @PutMapping("/api/user/info")
    public Result<Void> updateUserInfo(@RequestBody UpdateUserInfoRequestDTO request) {
        userService.updateUserInfo(request);
        return Result.success();
    }
    /**
     * 管理员获取用户列表
     */
    @GetMapping("/admin/list")
    public Result<IPage<AdminUserDetailDTO>> pageQueryUser(UserQueryRequestDTO request) {
        return Result.success(userService.pageQuery(request));
    }
    /**
     * 管理员获取用户信息
     */
    @GetMapping("/admin/{userId}")
    public Result<AdminUserDetailDTO> getAdminUserDetail(@PathVariable Long userId) {
        return Result.success(userService.getAdminUserDetail(userId));
    }
    /**
     * 管理员修改用户信息
     */
    @PutMapping("/admin/{userId}")
    public Result<Void> updateAdminUserInfo(
            @PathVariable Long userId,
            @RequestBody UpdateUserInfoRequestDTO request
    ) {
        userService.updateAdminUserInfo(userId, request);
        return Result.success();
    }
    /**
     * 管理员修改用户状态
     */
    @PutMapping("/admin/{userId}/status")
    public Result<Void> updateUserStatus(
            @PathVariable Long userId,
            @RequestParam Integer status
    ) {
        userService.updateUserStatus(userId, status);
        return Result.success();
    }
}
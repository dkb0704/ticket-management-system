package com.ticket.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ticket.model.dto.request.UpdateUserInfoRequestDTO;
import com.ticket.model.dto.request.UserQueryRequestDTO;
import com.ticket.model.dto.response.AdminUserDetailDTO;
import com.ticket.model.dto.response.UserInfoResponseDTO;
import com.ticket.model.entity.User;

public interface UserService {
    // 获取当前用户信息
    UserInfoResponseDTO getCurrentUserInfo();

    // 修改个人信息
    void updateUserInfo(UpdateUserInfoRequestDTO request);
    // 分页查询用户信息
    IPage<AdminUserDetailDTO> pageQuery(UserQueryRequestDTO request);
    // 查询用户详情
    AdminUserDetailDTO getAdminUserDetail(Long userId);
    // 修改用户信息
    void updateAdminUserInfo(Long userId, UpdateUserInfoRequestDTO request);
    // 启用/禁用用户
    void updateUserStatus(Long userId, Integer status);
}

package com.ticket.service;

import com.ticket.model.dto.request.UpdateUserInfoRequestDTO;
import com.ticket.model.dto.response.UserInfoResponseDTO;
import com.ticket.model.entity.User;

public interface UserService {
    // 获取当前用户信息
    UserInfoResponseDTO getCurrentUserInfo();

    // 修改个人信息
    void updateUserInfo(UpdateUserInfoRequestDTO request);
}

package com.ticket.service.impl;

import com.ticket.exception.BusinessException;
import com.ticket.exception.ErrorCode;
import com.ticket.mapper.UserMapper;
import com.ticket.model.dto.request.UpdateUserInfoRequestDTO;
import com.ticket.model.dto.response.UserInfoResponseDTO;
import com.ticket.model.entity.User;
import com.ticket.service.UserService;
import com.ticket.util.JwtUtils;
import com.ticket.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private JwtUtils jwtUtil;

    @Override
    public UserInfoResponseDTO getCurrentUserInfo() {
        // 从上下文获取当前登录用户ID
        Long userId = UserContext.getCurrentUserId();
        // 查询用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        // 转换为响应DTO
        UserInfoResponseDTO response = new UserInfoResponseDTO();
        BeanUtils.copyProperties(user, response);
        return response;
    }

    @Override
    public void updateUserInfo(UpdateUserInfoRequestDTO request) {
        Long userId = UserContext.getCurrentUserId();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw  new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        user.setUpdateTime(LocalDateTime.now());
        try {
            userMapper.updateById(user);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.USER_OPERATION_FAIL);
        }
    }
}

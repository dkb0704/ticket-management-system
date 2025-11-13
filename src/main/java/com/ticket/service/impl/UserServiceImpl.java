package com.ticket.service.impl;

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
            //todo
            log.info("UserServiceImpl的getCurrentUserInfo(): 用户不存在 ");
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
            //todo
            log.info("UserServiceImpl的updateUserInfo(): 用户不存在 ");
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

        userMapper.updateById(user);
    }
}

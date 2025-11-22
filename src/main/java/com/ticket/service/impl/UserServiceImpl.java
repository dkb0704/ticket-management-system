package com.ticket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ticket.exception.BusinessException;
import com.ticket.exception.ErrorCode;
import com.ticket.mapper.UserMapper;
import com.ticket.model.dto.request.UpdateUserInfoRequestDTO;
import com.ticket.model.dto.request.UserQueryRequestDTO;
import com.ticket.model.dto.response.AdminUserDetailDTO;
import com.ticket.model.dto.response.UserInfoResponseDTO;
import com.ticket.model.entity.User;
import com.ticket.service.UserService;
import com.ticket.util.JwtUtils;
import com.ticket.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    @Override
    public IPage<AdminUserDetailDTO> pageQuery(UserQueryRequestDTO request) {
        Page<User> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();

        // 构建查询条件
        queryWrapper.ne(User::getRole,User.ROLE_ADMIN);
        if (StringUtils.hasText(request.getUsername())) {
            queryWrapper.like(User::getUsername, request.getUsername());
        }
        if (StringUtils.hasText(request.getEmail())) {
            queryWrapper.eq(User::getEmail, request.getEmail());
        }
        if (request.getStatus() != null) {
            queryWrapper.eq(User::getStatus, request.getStatus());
        }

        // 按创建时间倒序
        queryWrapper.orderByDesc(User::getCreateTime);

        // 执行分页查询并转换DTO
        IPage<User> userPage = userMapper.selectPage(page, queryWrapper);
        return userPage.convert(user -> {
            AdminUserDetailDTO dto = new AdminUserDetailDTO();
            BeanUtils.copyProperties(user, dto);
            return dto;
        });
    }

    @Override
    public AdminUserDetailDTO getAdminUserDetail(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        AdminUserDetailDTO dto = new AdminUserDetailDTO();
        BeanUtils.copyProperties(user, dto);
        return dto;
    }

    @Override
    public void updateAdminUserInfo(Long userId, UpdateUserInfoRequestDTO request) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
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

        try {
            userMapper.updateById(user);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.USER_OPERATION_FAIL);
        }
    }


    @Override
    public void updateUserStatus(Long userId, Integer status) {
        if (status != 0 && status != 1) {
            throw new BusinessException(ErrorCode.STATUS_OPERATION_FAIL);
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        user.setStatus(status);
        try {
            userMapper.updateById(user);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.USER_OPERATION_FAIL);
        }
    }
}

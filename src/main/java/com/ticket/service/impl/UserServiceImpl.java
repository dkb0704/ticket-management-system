package com.ticket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ticket.mapper.UserMapper;
import com.ticket.model.dto.request.LoginRequestDTO;
import com.ticket.model.dto.response.LoginResponseDTO;
import com.ticket.model.entity.User;
import com.ticket.service.UserService;
import com.ticket.util.JwtUtils;
import com.ticket.util.PasswordUtils;
import com.ticket.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * @author dkb
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private JwtUtils jwtUtil;
    @Resource
    private PasswordUtils passwordUtil;
    @Resource
    private RedisUtils redisUtil;
    
    // 密码/邮箱登录
    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        Integer loginType = request.getLoginType();
        String account = request.getAccount();
        String password = request.getPassword();

        User user = null;
        // 1. 根据登录类型查询用户
        // 密码登录（账号为用户名）
        if (loginType == 1) {
            user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                    .eq(User::getUsername, account));
        }
        // 邮箱登录（账号为邮箱）
        else if (loginType == 2) {
            user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                    .eq(User::getEmail, account));
        }

        // 2. 若用户不存在，自动注册
        if (user == null) {
            return registerUser(new LoginRequestDTO(loginType, account, password));
        } else {
            // 3. 验证密码（加密比对）
            if (!passwordUtil.verify(password, user.getPassword())) {
                //todo
                log.info("AuthServiceImpl的login方法： 密码错误");
            }
        }

        // 4. 检查用户状态
        if (user.getStatus() != 1) {
            //todo
            log.info("AuthServiceImpl的login方法： 用户处于禁用状态");
        }

        // 5. 生成JWT令牌
        return generateLoginResponse(user);
    }

    // 注册新用户
    @Override
    public LoginResponseDTO registerUser(LoginRequestDTO request) {
        Integer loginType = request.getLoginType();
        String account = request.getAccount();
        String password = request.getPassword();

        User user = new User();
        // 设置用户名（邮箱登录时，默认用邮箱前缀作为用户名）
        String username = (loginType == 2) ? account.split("@")[0] : account;
        user.setUsername(username);
        // 加密密码
        user.setPassword(PasswordUtils.encrypt(password));
        // 邮箱登录时，设置邮箱字段
        if (loginType == 2) {
            user.setEmail(account);
        }
        // 保存到数据库
        int rows = userMapper.insert(user);
        if (rows <= 0) {
            //todo
            log.info("AuthServiceImpl的registerUser方法： 添加新用户失败");
        }
        return generateLoginResponse(user);
    }


    @Override
    public void logout() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization");
        if (token == null) {
            //todo
            log.info("UserServiceImpl: logout失败，token不存在");
        }
        // 计算 Token 剩余有效期（秒）
        long remainingSeconds = jwtUtil.getRemainingSeconds(token);
        // 将 Token 加入黑名单，有效期为剩余时间
        redisUtil.addToBlacklist(token, remainingSeconds);
    }

    //  构建登录响应
    @Override
    public LoginResponseDTO generateLoginResponse(User user) {
        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);

        // 生成JWT令牌
        String token = jwtUtil.generateToken(user.getId());
        LocalDateTime expireTime = LocalDateTime.now().plusHours(24);

        LoginResponseDTO response = new LoginResponseDTO();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setAvatar(user.getAvatar());
        response.setExpireTime(expireTime);
        response.setRole(user.getRole());
        return response;
    }



}
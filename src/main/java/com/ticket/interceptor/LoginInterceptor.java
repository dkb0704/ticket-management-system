package com.ticket.interceptor;

import com.ticket.exception.BusinessException;
import com.ticket.exception.ErrorCode;
import com.ticket.mapper.UserMapper;
import com.ticket.model.entity.User;
import com.ticket.util.JwtUtils;
import com.ticket.util.RedisUtils;
import com.ticket.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    @Resource
    private JwtUtils jwtUtil;
    @Resource
    private RedisUtils redisUtil;
    @Resource
    private UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头获取token
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        // 2. 检查 Token 是否在黑名单
        if (redisUtil.isInBlacklist(token)) {
            throw new BusinessException(ErrorCode.USER_LOGGED_OUT);
        }

        // 验证token并解析用户ID
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            // 3. 将用户ID存入ThreadLocal（上下文），方便后续接口获取
            UserContext.setUserId(userId);

            // 更新最后登录时间
            User user = new User();
            user.setId(userId);
            user.setLastLoginTime(LocalDateTime.now());
            userMapper.updateById(user);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清除ThreadLocal，避免内存泄漏
        UserContext.removeUserId();
    }
}


package com.ticket.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticket.exception.BusinessException;
import com.ticket.exception.ErrorCode;
import com.ticket.mapper.UserMapper;
import com.ticket.model.entity.Result;
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
        try {
            //从请求头获取token
            String token = request.getHeader("Authorization");
            if (token == null || token.isEmpty()) {
                throw new BusinessException(ErrorCode.NOT_LOGIN);
            }

            // 检查 Token 是否在黑名单
            if (redisUtil.isInBlacklist(token)) {
                throw new BusinessException(ErrorCode.USER_LOGGED_OUT);
            }

            // 验证token并解析用户ID
            Long userId = jwtUtil.getUserIdFromToken(token);
            UserContext.setUserId(userId);

            // 更新最后登录时间
            User user = new User();
            user.setId(userId);
            user.setLastLoginTime(LocalDateTime.now());
            userMapper.updateById(user);
        } catch (BusinessException e) {
            response.setContentType("application/json;charset=UTF-8");
            Result<Void> result = Result.fail(e.getCode(), e.getMessage());
            // 序列化为JSON
            String json = new ObjectMapper().writeValueAsString(result);
            response.getWriter().write(json);
            return false;
        } catch (Exception e) {
            response.setContentType("application/json;charset=UTF-8");
            Result<Void> result = Result.fail(ErrorCode.TOKEN_INVALID.getCode(), ErrorCode.TOKEN_INVALID.getMessage());
            String json = new ObjectMapper().writeValueAsString(result);
            response.getWriter().write(json);
            return false;
        }
        return true;
    }

}


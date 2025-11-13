package com.ticket.interceptor;

import com.ticket.util.JwtUtils;
import com.ticket.util.RedisUtils;
import com.ticket.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    @Resource
    private JwtUtils jwtUtil;
    @Resource
    private RedisUtils redisUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头获取token
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            //todo
            log.info("LoginInterceptor：token为空 转发到login");
        }

        // 2. 检查 Token 是否在黑名单
        if (redisUtil.isInBlacklist(token)) {
            //todo
            log.info("LoginInterceptor：用户已经登出 转发到login");
        }

        // 验证token并解析用户ID
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            // 3. 将用户ID存入ThreadLocal（上下文），方便后续接口获取
            UserContext.setUserId(userId);
        } catch (Exception e) {
            // token无效或过期
            log.info("LoginInterceptor：token已无效或过期 转发到login");
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清除ThreadLocal，避免内存泄漏
        UserContext.removeUserId();
    }
}


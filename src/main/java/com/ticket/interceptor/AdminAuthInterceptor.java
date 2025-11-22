package com.ticket.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticket.exception.BusinessException;
import com.ticket.exception.ErrorCode;
import com.ticket.mapper.UserMapper;
import com.ticket.model.entity.Result;
import com.ticket.model.entity.User;
import com.ticket.util.UserContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {
    @Resource
    private UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            Long userId = UserContext.getCurrentUserId();

            User user = userMapper.selectById(userId);
            if (user == null || !Objects.equals(user.getRole(), User.ROLE_ADMIN)) {
                throw new BusinessException(ErrorCode.NO_PERMISSION);
            }
        } catch (BusinessException e) {
            response.setContentType("application/json;charset=UTF-8");
            Result<Void> result = Result.fail(e.getCode(), e.getMessage());
            String json = new ObjectMapper().writeValueAsString(result);
            response.getWriter().write(json);
            return false;
        }   catch (Exception e) {
            response.setContentType("application/json;charset=UTF-8");
            Result<Void> result = Result.fail(ErrorCode.TOKEN_INVALID.getCode(), ErrorCode.TOKEN_INVALID.getMessage());
            String json = new ObjectMapper().writeValueAsString(result);
            response.getWriter().write(json);
            return false;
        }
        return true;
    }
}

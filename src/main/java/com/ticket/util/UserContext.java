package com.ticket.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
//存储当前请求线程中的登录用户信息
public class UserContext {
    // 用 ThreadLocal 存储当前登录用户的 ID，每个线程独立存储
    private static final ThreadLocal<Long> USER_ID_THREAD_LOCAL = new ThreadLocal<>();

    //设置当前登录用户的 ID
    public static void setUserId(Long userId) {
        USER_ID_THREAD_LOCAL.set(userId);
    }

    //获取当前登录用户的 ID
    public static Long getCurrentUserId() {
        Long userId = USER_ID_THREAD_LOCAL.get();
        if (userId == null) {
            log.info("UserContext: 当前用户未登录");
        }
        return userId;
    }

    //清除当前线程中的用户 ID（在请求处理完成后调用，避免内存泄漏）
    public static void removeUserId() {
        USER_ID_THREAD_LOCAL.remove();
    }
}
package com.ticket.exception;

import lombok.Getter;

/**
 * 异常码枚举：统一管理系统所有异常码
 */
@Getter
public enum ErrorCode {
    // ===================== 认证模块 =====================
    NOT_LOGIN(10001, "未登录，请先登录"),
    TOKEN_INVALID(10002, "登录已过期，请重新登录"),
    LOGIN_TYPE_ERROR(10003, "登录类型错误（1-密码登录，2-邮箱登录）"),
    PASSWORD_ERROR(10004, "密码错误"),
    USER_DISABLED(10005, "账号已禁用"),
    USER_REGISTER_FAIL(10006, "用户注册失败，请重试"),
    USER_LOGGED_OUT(10007, "用户已登出，请重新登录"),
    USER_OPERATION_FAIL(10008, "用户表操作失败"),



    // ===================== 用户模块 =====================
    USER_NOT_FOUND(20001, "用户不存在"),
    ADDRESS_NOT_FOUND(20002, "收货地址不存在或不属于当前用户"),
    ADDRESS_OPERATION_FAIL(20003, "地址表操作失败"),

    // ===================== 用户模块 =====================
    PERFORMANCE_IS_EMPTY(30001, "查询结果为空"),
    PERFORMANCE_OPERATION_FAIL(30002, "演出表操作失败"),


    // ===================== 工具模块 =====================
    IP_OPERATION_FAIL(90001, "ip处理失败");



    /**
     * 异常码
     */
    private final int code;

    /**
     * 异常提示消息
     */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
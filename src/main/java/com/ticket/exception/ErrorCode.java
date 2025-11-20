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
    PERFORMANCE_IS_EMPTY(30001, "演出查询结果为空"),
    PERFORMANCE_OPERATION_FAIL(30002, "演出表操作失败"),
    PERFORMANCE_SESSION_IS_EMPTY(30003, "演出场次查询结果为空"),
    PERFORMANCE_NOT_ON_SALE(30004, "演出还未开始售票"),
    SESSION_NOT_VALID(30005, "该场次和演出不匹配"),
    SESSION_NOT_ON_SALE(30006, "该场次还未开始售票"),
    TICKET_GRADE_NOT_VALID(30007, "该票和场次不匹配"),
    TICKET_COUNT_EXCEED_LIMIT(30008, "超出最大购票数量"),
    TICKET_STOCK_INSUFFICIENT(30009, "票库存不足"),
    REPEAT_GRAB_TICKET(30010, "禁止重复购票"),
    GRAB_TICKET_FAIL_RETRY(30011, "购票失败，请重试"),



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
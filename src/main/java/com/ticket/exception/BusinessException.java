package com.ticket.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    /**
     * 异常码（用于前端区分异常类型）
     */
    private final int code;

    /**
     * 构造方法：直接传入异常码和消息
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 构造方法：传入异常码枚举（推荐使用，避免硬编码）
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    /**
     * 构造方法：传入异常码枚举+自定义消息（扩展用）
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }
}

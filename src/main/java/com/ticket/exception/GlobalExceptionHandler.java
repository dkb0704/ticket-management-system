package com.ticket.exception;

import com.ticket.model.entity.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理器：拦截所有Controller层的异常，统一返回Result格式
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e, HttpServletRequest request) {
        // 打印异常日志（包含请求路径，方便排查）
        log.warn("业务异常：{}，请求路径：{}", e.getMessage(), request.getRequestURI());
        // 返回异常码和消息
        return Result.fail(e.getCode(), e.getMessage());
    }


}

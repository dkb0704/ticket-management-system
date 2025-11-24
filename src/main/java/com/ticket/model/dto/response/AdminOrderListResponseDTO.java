package com.ticket.model.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 管理员端订单列表响应DTO
 */
@Data
public class AdminOrderListResponseDTO {
    private Long id;
    private String orderNo;
    private Long userId;
    private String userName; // 用户名
    private String performanceName;
    private String sessionTime;
    private String gradeName;
    private Integer count;
    private BigDecimal totalAmount;
    private Integer status; // 订单状态码
    private String statusDesc; // 订单状态描述
    private LocalDateTime createTime;
    private LocalDateTime payTime;
    private LocalDateTime cancelTime;
    private LocalDateTime expireTime;
}


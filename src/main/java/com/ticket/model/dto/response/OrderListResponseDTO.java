package com.ticket.model.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderListResponseDTO {
    private Long id;
    private String orderNo;
    private String performanceName;
    private String sessionTime;
    private String gradeName;
    private Integer count;
    private BigDecimal totalAmount;
    // 订单状态描述（如“待支付”）
    private String statusDesc;
    private LocalDateTime createTime;
    // 仅待支付订单返回
    private LocalDateTime expireTime;
}
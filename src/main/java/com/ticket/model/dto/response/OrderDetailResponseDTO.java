package com.ticket.model.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderDetailResponseDTO {
    private Long id;
    private String orderNo;
    private String performanceName;
    // 演出场馆
    private String performanceVenue;
    private String sessionTime;
    private String gradeName;
    private BigDecimal price;
    private Integer count;
    private BigDecimal totalAmount;
    private String statusDesc;
    private LocalDateTime createTime;
    private LocalDateTime payTime;
    private LocalDateTime cancelTime;
    private LocalDateTime expireTime;
}
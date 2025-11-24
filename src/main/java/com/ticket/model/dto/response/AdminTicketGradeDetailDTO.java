package com.ticket.model.dto.response;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 管理员端票档详情DTO（包含库存数字）
 */
@Data
public class AdminTicketGradeDetailDTO {
    private Long id;
    private String gradeName;
    private BigDecimal price;
    private Integer totalStock; // 总库存
    private Integer remainingStock; // 剩余库存
    // 0-未开售，1-销售中，2-已售罄
    private Integer status;
}


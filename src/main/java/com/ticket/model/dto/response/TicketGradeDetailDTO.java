package com.ticket.model.dto.response;

import lombok.Data;

import java.math.BigDecimal;

// 票档详情DTO
@Data
public class TicketGradeDetailDTO {
    private Long id;
    private String gradeName;
    private BigDecimal price;
    private Boolean hasStock;
}
package com.ticket.model.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理员端场次详情DTO（包含库存数字）
 */
@Data
public class AdminSessionDetailDTO {
    private Long id;
    private LocalDateTime sessionTime;
    // 0-未开售，1-售票中，2-已售罄，3-已结束
    private Integer status;
    private List<AdminTicketGradeDetailDTO> ticketGrades; // 票档列表（包含库存数字）
}


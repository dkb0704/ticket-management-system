package com.ticket.model.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

// 场次详情DTO
@Data
public class SessionDetailDTO {
    private Long id;
    private LocalDateTime sessionTime;
    private Integer status; // 0-未开售，1-售票中，2-已售罄
    private List<TicketGradeDetailDTO> ticketGrades; // 票档列表
}
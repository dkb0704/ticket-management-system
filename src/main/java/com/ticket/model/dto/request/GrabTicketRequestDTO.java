package com.ticket.model.dto.request;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class GrabTicketRequestDTO {
    @NotNull(message = "演出ID不能为空")
    private Long performanceId;
    @NotNull(message = "场次ID不能为空")
    private Long sessionId;
    @NotNull(message = "票档ID不能为空")
    private Long ticketGradeId;
    @Min(value = 1, message = "购票数量不能小于1")
    // 购票数量
    private Integer count;
}

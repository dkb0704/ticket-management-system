package com.ticket.model.dto.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

// 演出详情响应DTO
@Data
public class PerformanceDetailResponseDTO {
    private Long id;
    private String name;
    private String star;
    private String categoryName;
    private String coverImage;
    private String venue;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String description;
    // 0-未开票，1-已开票，2-已结束
    private Integer status;
    private LocalDateTime ticketStartTime;
    private LocalDateTime ticketEndTime;
    private List<SessionDetailDTO> sessions;
}

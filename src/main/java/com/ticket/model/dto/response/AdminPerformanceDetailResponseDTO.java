package com.ticket.model.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理员端演出详情响应DTO（包含库存数字）
 */
@Data
public class AdminPerformanceDetailResponseDTO {
    private Long id;
    private String name;
    private String star;
    private Long categoryId;
    private String categoryName;
    private Long cityId;
    private String cityName;
    private String coverImage;
    private String venue;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String description;
    // 0-未开票，1-已开票，2-已结束
    private Integer status;
    private LocalDateTime ticketStartTime;
    private LocalDateTime ticketEndTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<AdminSessionDetailDTO> sessions;
}


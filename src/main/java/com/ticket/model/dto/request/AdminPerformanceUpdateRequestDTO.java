package com.ticket.model.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 管理员端修改演出请求DTO
 */
@Data
public class AdminPerformanceUpdateRequestDTO {
    @NotNull(message = "演出ID不能为空")
    private Long id;
    
    private String name;
    
    private Long categoryId;
    
    private Long cityId;
    
    private String venue;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endTime;
    
    private String star;
    
    private String coverImage;
    
    private String description;
    
    private Integer status; // 0-未开票，1-已开票，2-已结束
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime ticketStartTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime ticketEndTime;
}


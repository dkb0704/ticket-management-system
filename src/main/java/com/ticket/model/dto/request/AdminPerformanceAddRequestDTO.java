package com.ticket.model.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 管理员端添加演出请求DTO
 */
@Data
public class AdminPerformanceAddRequestDTO {
    @NotBlank(message = "演出名称不能为空")
    private String name;
    
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;
    
    @NotNull(message = "城市ID不能为空")
    private Long cityId;
    
    @NotBlank(message = "场馆不能为空")
    private String venue;
    
    @NotNull(message = "开始时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startTime;
    
    @NotNull(message = "结束时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endTime;
    
    private String star; // 明星/主演
    
    private String coverImage; // 封面图片
    
    private String description; // 描述
    
    private Integer status; // 0-未开票，1-已开票，2-已结束
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime ticketStartTime; // 开票开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime ticketEndTime; // 开票结束时间
}


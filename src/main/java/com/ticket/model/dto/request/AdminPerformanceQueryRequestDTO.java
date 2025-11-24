package com.ticket.model.dto.request;

import lombok.Data;

import javax.validation.constraints.Min;

/**
 * 管理员端演出查询请求DTO
 */
@Data
public class AdminPerformanceQueryRequestDTO {
    private Long cityId; // 城市ID
    private Long categoryId; // 分类ID
    private String keyword; // 关键词（演出名/明星名）
    private Integer status; // 状态：0-未开票，1-已开票，2-已结束
    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNum = 1;
    @Min(value = 1, message = "每页条数不能小于1")
    private Integer pageSize = 10;
}


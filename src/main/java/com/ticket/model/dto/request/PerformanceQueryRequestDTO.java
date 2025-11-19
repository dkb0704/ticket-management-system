package com.ticket.model.dto.request;

import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class PerformanceQueryRequestDTO {
    private Long cityId;
    private Long categoryId;
    //关键词 用于匹配城市名/明星名
    private String keyword;
    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNum = 1;
    @Min(value = 1, message = "每页条数不能小于1")
    private Integer pageSize = 10;
}
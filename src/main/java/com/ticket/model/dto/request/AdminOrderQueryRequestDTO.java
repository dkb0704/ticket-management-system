package com.ticket.model.dto.request;

import lombok.Data;

import javax.validation.constraints.Min;

/**
 * 管理员端订单查询请求DTO
 */
@Data
public class AdminOrderQueryRequestDTO {
    private String orderNo; // 订单号
    private Long userId; // 用户ID
    private Long performanceId; // 演出ID
    private Integer status; // 订单状态
    private String keyword; // 关键词（订单号/演出名）
    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNum = 1;
    @Min(value = 1, message = "每页条数不能小于1")
    private Integer pageSize = 10;
}


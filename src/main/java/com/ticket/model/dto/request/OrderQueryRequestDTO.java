package com.ticket.model.dto.request;

import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class OrderQueryRequestDTO {
    private Integer status; // 订单状态
    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNum = 1;
    @Min(value = 1, message = "每页条数不能小于1")
    private Integer pageSize = 10;
}

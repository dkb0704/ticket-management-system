package com.ticket.model.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 管理员端修改订单请求DTO
 */
@Data
public class AdminOrderUpdateRequestDTO {
    @NotNull(message = "订单ID不能为空")
    private Long id;
    
    @NotNull(message = "订单状态不能为空")
    private Integer status; // 订单状态：0-待支付，1-已支付，2-已取消，3-已完成，4-已退款
}


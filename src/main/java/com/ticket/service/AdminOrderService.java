package com.ticket.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ticket.model.dto.request.AdminOrderQueryRequestDTO;
import com.ticket.model.dto.request.AdminOrderUpdateRequestDTO;
import com.ticket.model.dto.response.AdminOrderDetailResponseDTO;
import com.ticket.model.dto.response.AdminOrderListResponseDTO;

/**
 * 管理员端订单管理Service
 */
public interface AdminOrderService {
    /**
     * 分页查询订单列表
     */
    IPage<AdminOrderListResponseDTO> queryOrderPage(AdminOrderQueryRequestDTO request);

    /**
     * 查询订单详情
     */
    AdminOrderDetailResponseDTO getOrderDetail(Long id);

    /**
     * 修改订单信息（手动更新订单状态）
     */
    void updateOrder(AdminOrderUpdateRequestDTO request);
}


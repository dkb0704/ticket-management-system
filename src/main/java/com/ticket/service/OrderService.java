package com.ticket.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ticket.model.dto.request.OrderQueryRequestDTO;
import com.ticket.model.dto.response.OrderDetailResponseDTO;
import com.ticket.model.dto.response.OrderListResponseDTO;
import com.ticket.model.entity.Order;

public interface OrderService {
    // 分页查询用户订单列表
    IPage<OrderListResponseDTO> getUserOrderPage(OrderQueryRequestDTO request);

    // 获取订单详情
    OrderDetailResponseDTO getOrderDetail(Long id);

    // 取消订单
    void cancelOrder(Long id,Long userId);
    // 设置订单状态
    void setOrderStatus(Long id,Long userId,Integer status);
    // 根据订单号查询订单
    public Order getOrderByNo(String orderNo);
}

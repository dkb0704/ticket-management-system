package com.ticket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ticket.model.dto.response.AdminOrderDetailResponseDTO;
import com.ticket.model.dto.response.AdminOrderListResponseDTO;
import com.ticket.model.dto.response.OrderDetailResponseDTO;
import com.ticket.model.dto.response.OrderListResponseDTO;
import com.ticket.model.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    // 分页查询用户订单（关联演出/场次表）
    IPage<OrderListResponseDTO> selectUserOrderPage(
            Page<OrderListResponseDTO> page,
            @Param("status") Integer status
    );

    // 查询订单详情（关联演出/场次表）
    OrderDetailResponseDTO selectOrderDetail(
            @Param("id") Long id
    );

    // 管理员端分页查询订单列表（关联演出/场次/用户表）
    IPage<AdminOrderListResponseDTO> selectAdminOrderPage(
            Page<AdminOrderListResponseDTO> page,
            @Param("orderNo") String orderNo,
            @Param("userId") Long userId,
            @Param("performanceId") Long performanceId,
            @Param("status") Integer status,
            @Param("keyword") String keyword
    );

    // 管理员端查询订单详情（关联演出/场次/用户表）
    AdminOrderDetailResponseDTO selectAdminOrderDetail(
            @Param("id") Long id
    );
}

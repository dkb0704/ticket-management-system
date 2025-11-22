package com.ticket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
            @Param("userId") Long userId
    );
}

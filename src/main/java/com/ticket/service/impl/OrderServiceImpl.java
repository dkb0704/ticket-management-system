package com.ticket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ticket.enums.OrderStatusEnum;
import com.ticket.exception.BusinessException;
import com.ticket.exception.ErrorCode;
import com.ticket.mapper.OrderMapper;
import com.ticket.mapper.TicketGradeMapper;
import com.ticket.model.dto.request.OrderQueryRequestDTO;
import com.ticket.model.dto.response.OrderDetailResponseDTO;
import com.ticket.model.dto.response.OrderListResponseDTO;
import com.ticket.model.entity.Order;
import com.ticket.model.entity.TicketGrade;
import com.ticket.service.OrderService;
import com.ticket.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class OrderServiceImpl implements OrderService {
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private TicketGradeMapper ticketGradeMapper;

    @Override
    public IPage<OrderListResponseDTO> getUserOrderPage(OrderQueryRequestDTO request) {
        Page<OrderListResponseDTO> page = new Page<>(request.getPageNum(), request.getPageSize());
        return orderMapper.selectUserOrderPage(page, request.getStatus());
    }

    @Override
    public OrderDetailResponseDTO getOrderDetail(Long id) {
        OrderDetailResponseDTO detail = orderMapper.selectOrderDetail(id);
        if (detail == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }
        return detail;
    }

    @Override
    @Transactional
    public void cancelOrder(Long id,Long userId) {
        // 查询订单（验证归属+状态）
        Order order = orderMapper.selectOne(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getId, id)
                        .eq(Order::getUserId, userId)
        );
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }

        // 校验订单状态（仅待支付订单可取消）
        if (!OrderStatusEnum.PENDING_PAY.getCode().equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_NOT_ALLOW_CANCEL);
        }

        // 校验订单是否过期（已过期无需重复取消）
        if (LocalDateTime.now().isAfter(order.getExpireTime())) {
            throw new BusinessException(ErrorCode.ORDER_HAS_EXPIRED);
        }

        //  更新订单状态+取消时间
        Order updateOrder = new Order();
        updateOrder.setId(id);
        updateOrder.setStatus(OrderStatusEnum.CANCELLED.getCode());
        updateOrder.setCancelTime(LocalDateTime.now());
        int rows = orderMapper.updateById(updateOrder);
        if (rows == 0) {
            throw new BusinessException(ErrorCode.ORDER_OPERATE_FAIL);
        }
        TicketGrade ticketGrade = ticketGradeMapper.selectById(order.getTicketGradeId());
        rows = ticketGradeMapper.incrStockWithVersion(
                order.getTicketGradeId(),
                order.getCount(),
                ticketGrade.getVersion()
        );
        if (rows == 0) {
            throw new BusinessException(ErrorCode.STOCK_OPERATE_FAIL);
        }
    }

    @Override
    public void setOrderStatus(Long id, Long userId, Integer status) {
        // 查询订单（验证归属+状态）
        Order order = orderMapper.selectOne(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getId, id)
                        .eq(Order::getUserId, userId)
        );
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }
        order.setStatus(status);
        orderMapper.updateById(order);
    }

    @Override
    public Order getOrderByNo(String orderNo) {
        return orderMapper.selectOne(new  LambdaQueryWrapper<Order>().eq(Order::getOrderNo, orderNo));
    }
}

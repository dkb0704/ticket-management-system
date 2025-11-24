package com.ticket.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ticket.enums.OrderStatusEnum;
import com.ticket.exception.BusinessException;
import com.ticket.exception.ErrorCode;
import com.ticket.mapper.OrderMapper;
import com.ticket.model.dto.request.AdminOrderQueryRequestDTO;
import com.ticket.model.dto.request.AdminOrderUpdateRequestDTO;
import com.ticket.model.dto.response.AdminOrderDetailResponseDTO;
import com.ticket.model.dto.response.AdminOrderListResponseDTO;
import com.ticket.model.entity.Order;
import com.ticket.service.AdminOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * 管理员端订单管理Service实现
 */
@Service
public class AdminOrderServiceImpl implements AdminOrderService {

    @Resource
    private OrderMapper orderMapper;

    @Override
    public IPage<AdminOrderListResponseDTO> queryOrderPage(AdminOrderQueryRequestDTO request) {
        // 构建分页参数
        Page<AdminOrderListResponseDTO> page = new Page<>(request.getPageNum(), request.getPageSize());

        // 执行查询
        IPage<AdminOrderListResponseDTO> result = orderMapper.selectAdminOrderPage(
                page,
                request.getOrderNo(),
                request.getUserId(),
                request.getPerformanceId(),
                request.getStatus(),
                request.getKeyword()
        );

        return result;
    }

    @Override
    public AdminOrderDetailResponseDTO getOrderDetail(Long id) {
        AdminOrderDetailResponseDTO detail = orderMapper.selectAdminOrderDetail(id);
        if (detail == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }
        return detail;
    }

    @Override
    @Transactional
    public void updateOrder(AdminOrderUpdateRequestDTO request) {
        // 查询订单是否存在
        Order order = orderMapper.selectById(request.getId());
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }

        // 校验订单状态是否有效
        OrderStatusEnum statusEnum = OrderStatusEnum.getByCode(request.getStatus());
        if (statusEnum == null) {
            throw new BusinessException(ErrorCode.ORDER_OPERATE_FAIL);
        }

        // 更新订单状态
        Order updateOrder = new Order();
        updateOrder.setId(request.getId());
        updateOrder.setStatus(request.getStatus());

        // 根据状态更新相应的时间字段
        if (request.getStatus().equals(OrderStatusEnum.PAID.getCode())) {
            updateOrder.setPayTime(LocalDateTime.now());
        } else if (request.getStatus().equals(OrderStatusEnum.CANCELLED.getCode())) {
            updateOrder.setCancelTime(LocalDateTime.now());
        }

        int rows = orderMapper.updateById(updateOrder);
        if (rows == 0) {
            throw new BusinessException(ErrorCode.ORDER_OPERATE_FAIL);
        }
    }
}


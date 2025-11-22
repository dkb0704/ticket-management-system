package com.ticket.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ticket.model.dto.request.OrderQueryRequestDTO;
import com.ticket.model.dto.response.OrderDetailResponseDTO;
import com.ticket.model.dto.response.OrderListResponseDTO;
import com.ticket.model.entity.Result;
import com.ticket.service.OrderService;
import com.ticket.util.UserContext;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Resource
    private OrderService orderService;

    /**
     * 分页查询用户订单列表
     */
    @GetMapping("/list")
    public Result<IPage<OrderListResponseDTO>> getUserOrderPage(
            @Validated @RequestBody OrderQueryRequestDTO request
    ) {
        IPage<OrderListResponseDTO> page = orderService.getUserOrderPage(request);
        return Result.success(page);
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{id}")
    public Result<OrderDetailResponseDTO> getOrderDetail(@PathVariable Long id) {
        OrderDetailResponseDTO detail = orderService.getOrderDetail(id);
        return Result.success(detail);
    }

    /**
     * 取消订单
     */
    @PostMapping("/{id}/cancel")
    public Result<String> cancelOrder(@PathVariable Long id) {
        Long userId = UserContext.getCurrentUserId();
        orderService.cancelOrder(id, userId);
        return Result.success("订单取消成功");
    }
}
package com.ticket.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ticket.model.dto.request.AdminOrderQueryRequestDTO;
import com.ticket.model.dto.request.AdminOrderUpdateRequestDTO;
import com.ticket.model.dto.response.AdminOrderDetailResponseDTO;
import com.ticket.model.dto.response.AdminOrderListResponseDTO;
import com.ticket.model.entity.Result;
import com.ticket.service.AdminOrderService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 管理员端订单管理Controller
 */
@RestController
@RequestMapping("/admin/order")
public class AdminOrderController {

    @Resource
    private AdminOrderService adminOrderService;

    /**
     * 分页查询订单列表
     * 支持条件：订单号、用户ID、演出ID、订单状态、关键词（订单号/演出名）
     */
    @PostMapping("/list")
    public Result<IPage<AdminOrderListResponseDTO>> queryOrderPage(
            @Validated @RequestBody AdminOrderQueryRequestDTO request) {
        IPage<AdminOrderListResponseDTO> page = adminOrderService.queryOrderPage(request);
        return Result.success(page);
    }

    /**
     * 查询订单详情
     */
    @GetMapping("/{id}")
    public Result<AdminOrderDetailResponseDTO> getOrderDetail(@PathVariable("id") Long id) {
        AdminOrderDetailResponseDTO detail = adminOrderService.getOrderDetail(id);
        return Result.success(detail);
    }

    /**
     * 修改订单信息（手动更新订单状态）
     */
    @PostMapping("/update")
    public Result<String> updateOrder(@Validated @RequestBody AdminOrderUpdateRequestDTO request) {
        adminOrderService.updateOrder(request);
        return Result.success("订单状态更新成功");
    }
}


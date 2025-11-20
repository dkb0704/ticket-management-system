package com.ticket.controller;

import com.ticket.model.dto.request.GrabTicketRequestDTO;
import com.ticket.model.dto.request.PerformanceQueryRequestDTO;
import com.ticket.model.dto.response.PerformanceDetailResponseDTO;
import com.ticket.model.dto.response.PerformancePageResponseDTO;
import com.ticket.model.entity.Result;
import com.ticket.service.PerformanceService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/performance")
public class PerformanceController {
    @Resource
    private PerformanceService performanceService;
    /**
     * 条件+分页查询演出
     * 支持按城市、分类、关键词（演出名/明星名）筛选
     */
    @GetMapping("/list")
    public Result<PerformancePageResponseDTO> queryPerformance(@RequestBody PerformanceQueryRequestDTO requestDto, HttpServletRequest request) {
        PerformancePageResponseDTO response = performanceService.queryPerformance(requestDto,request);
        return Result.success(response);
    }
    /**
     * 获取演出详情（含场次、票档、库存状态）
     * @param id 演出ID
     */
    @GetMapping("/{id}")
    public Result<PerformanceDetailResponseDTO> getPerformanceDetail(@PathVariable("id") Long id) {
        PerformanceDetailResponseDTO response = performanceService.getPerformanceDetail(id);
        return Result.success(response);
    }

    /**
     * 抢票接口（需登录，并发控制）
     */
    @PostMapping("/grab")
    public Result<String> grabTicket(@RequestBody GrabTicketRequestDTO request) {
        performanceService.grabTicket(request);
        return Result.success("抢票成功，请尽快完成订单支付");
    }
}

package com.ticket.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ticket.model.dto.request.AdminPerformanceAddRequestDTO;
import com.ticket.model.dto.request.AdminPerformanceQueryRequestDTO;
import com.ticket.model.dto.request.AdminPerformanceUpdateRequestDTO;
import com.ticket.model.dto.response.AdminPerformanceDetailResponseDTO;
import com.ticket.model.entity.Result;
import com.ticket.service.AdminPerformanceService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 管理员端演出管理Controller
 */
@RestController
@RequestMapping("/admin/performance")
public class AdminPerformanceController {

    @Resource
    private AdminPerformanceService adminPerformanceService;

    /**
     * 添加演出信息
     */
    @PostMapping("/add")
    public Result<String> addPerformance(@Validated @RequestBody AdminPerformanceAddRequestDTO request) {
        adminPerformanceService.addPerformance(request);
        return Result.success("演出添加成功");
    }

    /**
     * 修改演出信息
     */
    @PostMapping("/update")
    public Result<String> updatePerformance(@Validated @RequestBody AdminPerformanceUpdateRequestDTO request) {
        adminPerformanceService.updatePerformance(request);
        return Result.success("演出修改成功");
    }

    /**
     * 分页查询演出列表
     * 支持条件：城市ID、分类ID、状态、关键词（演出名/明星名）
     */
    @PostMapping("/list")
    public Result<IPage<AdminPerformanceDetailResponseDTO>> queryPerformancePage(
            @Validated @RequestBody AdminPerformanceQueryRequestDTO request) {
        IPage<AdminPerformanceDetailResponseDTO> page = adminPerformanceService.queryPerformancePage(request);
        return Result.success(page);
    }

    /**
     * 查询演出详情（包含库存数字）
     */
    @GetMapping("/{id}")
    public Result<AdminPerformanceDetailResponseDTO> getPerformanceDetail(@PathVariable("id") Long id) {
        AdminPerformanceDetailResponseDTO detail = adminPerformanceService.getPerformanceDetail(id);
        return Result.success(detail);
    }

    /**
     * 删除演出信息
     */
    @DeleteMapping("/{id}")
    public Result<String> deletePerformance(@PathVariable("id") Long id) {
        adminPerformanceService.deletePerformance(id);
        return Result.success("演出删除成功");
    }
}


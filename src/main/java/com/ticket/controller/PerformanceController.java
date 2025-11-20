package com.ticket.controller;

import com.ticket.model.dto.request.PerformanceQueryRequestDTO;
import com.ticket.model.dto.response.PerformancePageResponseDTO;
import com.ticket.model.entity.Result;
import com.ticket.service.PerformanceService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/performance")
public class PerformanceController {
    @Resource
    private PerformanceService performanceService;

    @GetMapping("/list")
    public Result<PerformancePageResponseDTO> queryPerformance(@RequestBody PerformanceQueryRequestDTO requestDto, HttpServletRequest request) {
        PerformancePageResponseDTO response = performanceService.queryPerformance(requestDto,request);
        return Result.success(response);
    }
}

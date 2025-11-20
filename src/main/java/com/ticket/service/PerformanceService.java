package com.ticket.service;

import com.ticket.model.dto.request.GrabTicketRequestDTO;
import com.ticket.model.dto.response.PerformanceDetailResponseDTO;
import com.ticket.model.dto.response.PerformancePageResponseDTO;
import com.ticket.model.dto.request.PerformanceQueryRequestDTO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface PerformanceService {
    // 条件+分页查询演出
    PerformancePageResponseDTO queryPerformance(PerformanceQueryRequestDTO requestDTO,HttpServletRequest request);

    // 获取演出详情（含场次、票档、库存状态）
    PerformanceDetailResponseDTO getPerformanceDetail(Long performanceId);

    // 抢票接口（核心并发接口）
    void grabTicket(GrabTicketRequestDTO request);
}

package com.ticket.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ticket.model.dto.request.AdminPerformanceAddRequestDTO;
import com.ticket.model.dto.request.AdminPerformanceQueryRequestDTO;
import com.ticket.model.dto.request.AdminPerformanceUpdateRequestDTO;
import com.ticket.model.dto.response.AdminPerformanceDetailResponseDTO;

/**
 * 管理员端演出管理Service
 */
public interface AdminPerformanceService {
    /**
     * 添加演出信息
     */
    void addPerformance(AdminPerformanceAddRequestDTO request);

    /**
     * 修改演出信息
     */
    void updatePerformance(AdminPerformanceUpdateRequestDTO request);

    /**
     * 分页查询演出列表
     */
    IPage<AdminPerformanceDetailResponseDTO> queryPerformancePage(AdminPerformanceQueryRequestDTO request);

    /**
     * 查询演出详情（包含库存数字）
     */
    AdminPerformanceDetailResponseDTO getPerformanceDetail(Long id);

    /**
     * 删除演出信息
     */
    void deletePerformance(Long id);
}


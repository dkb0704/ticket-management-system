package com.ticket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ticket.exception.BusinessException;
import com.ticket.exception.ErrorCode;
import com.ticket.mapper.*;
import com.ticket.model.dto.request.AdminPerformanceAddRequestDTO;
import com.ticket.model.dto.request.AdminPerformanceQueryRequestDTO;
import com.ticket.model.dto.request.AdminPerformanceUpdateRequestDTO;
import com.ticket.model.dto.response.AdminPerformanceDetailResponseDTO;
import com.ticket.model.dto.response.AdminSessionDetailDTO;
import com.ticket.model.dto.response.AdminTicketGradeDetailDTO;
import com.ticket.model.entity.*;
import com.ticket.service.AdminPerformanceService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理员端演出管理Service实现
 */
@Service
public class AdminPerformanceServiceImpl implements AdminPerformanceService {

    @Resource
    private PerformanceMapper performanceMapper;

    @Resource
    private PerformanceCategoryMapper performanceCategoryMapper;

    @Resource
    private RegionMapper regionMapper;

    @Resource
    private PerformanceSessionMapper performanceSessionMapper;

    @Resource
    private TicketGradeMapper ticketGradeMapper;

    @Override
    @Transactional
    public void addPerformance(AdminPerformanceAddRequestDTO request) {
        // 校验分类是否存在
        PerformanceCategory category = performanceCategoryMapper.selectById(request.getCategoryId());
        if (category == null) {
            throw new BusinessException(ErrorCode.PERFORMANCE_OPERATION_FAIL);
        }

        // 校验城市是否存在
        Region region = regionMapper.selectById(request.getCityId());
        if (region == null) {
            throw new BusinessException(ErrorCode.PERFORMANCE_OPERATION_FAIL);
        }

        // 构建演出实体
        Performance performance = new Performance();
        BeanUtils.copyProperties(request, performance);
        performance.setCreateTime(LocalDateTime.now());
        performance.setUpdateTime(LocalDateTime.now());
        
        // 如果状态为空，默认设置为未开票
        if (performance.getStatus() == null) {
            performance.setStatus(0);
        }

        performanceMapper.insert(performance);
    }

    @Override
    @Transactional
    public void updatePerformance(AdminPerformanceUpdateRequestDTO request) {
        // 查询演出是否存在
        Performance performance = performanceMapper.selectById(request.getId());
        if (performance == null) {
            throw new BusinessException(ErrorCode.PERFORMANCE_IS_EMPTY);
        }

        // 如果修改了分类，校验分类是否存在
        if (request.getCategoryId() != null) {
            PerformanceCategory category = performanceCategoryMapper.selectById(request.getCategoryId());
            if (category == null) {
                throw new BusinessException(ErrorCode.PERFORMANCE_OPERATION_FAIL);
            }
        }

        // 如果修改了城市，校验城市是否存在
        if (request.getCityId() != null) {
            Region region = regionMapper.selectById(request.getCityId());
            if (region == null) {
                throw new BusinessException(ErrorCode.PERFORMANCE_OPERATION_FAIL);
            }
        }

        // 更新演出信息
        Performance updatePerformance = new Performance();
        BeanUtils.copyProperties(request, updatePerformance);
        updatePerformance.setUpdateTime(LocalDateTime.now());
        
        performanceMapper.updateById(updatePerformance);
    }

    @Override
    public IPage<AdminPerformanceDetailResponseDTO> queryPerformancePage(AdminPerformanceQueryRequestDTO request) {
        // 构建分页参数
        IPage<Performance> page = new Page<>(request.getPageNum(), request.getPageSize());

        // 构建查询条件
        LambdaQueryWrapper<Performance> queryWrapper = new LambdaQueryWrapper<>();
        
        // 城市筛选
        if (request.getCityId() != null) {
            queryWrapper.eq(Performance::getCityId, request.getCityId());
        }
        
        // 分类筛选
        if (request.getCategoryId() != null && request.getCategoryId() > 0) {
            queryWrapper.eq(Performance::getCategoryId, request.getCategoryId());
        }
        
        // 状态筛选
        if (request.getStatus() != null) {
            queryWrapper.eq(Performance::getStatus, request.getStatus());
        }
        
        // 关键词筛选（演出名/明星名）
        if (StringUtils.hasText(request.getKeyword())) {
            String keyword = request.getKeyword().trim();
            queryWrapper.and(wrapper -> wrapper
                    .like(Performance::getName, keyword)
                    .or().like(Performance::getStar, keyword)
            );
        }

        // 按创建时间降序
        queryWrapper.orderByDesc(Performance::getCreateTime);

        // 执行查询
        IPage<Performance> performancePage = performanceMapper.selectPage(page, queryWrapper);

        // 转换为响应DTO
        List<AdminPerformanceDetailResponseDTO> dtoList = performancePage.getRecords().stream()
                .map(performance -> {
                    AdminPerformanceDetailResponseDTO dto = new AdminPerformanceDetailResponseDTO();
                    BeanUtils.copyProperties(performance, dto);

                    // 查询分类名称
                    PerformanceCategory category = performanceCategoryMapper.selectById(performance.getCategoryId());
                    if (category != null) {
                        dto.setCategoryName(category.getName());
                    }

                    // 查询城市名称
                    Region region = regionMapper.selectById(performance.getCityId());
                    if (region != null) {
                        dto.setCityName(region.getName());
                    }

                    return dto;
                })
                .collect(Collectors.toList());

        // 构建分页响应
        Page<AdminPerformanceDetailResponseDTO> resultPage = new Page<>(performancePage.getCurrent(), performancePage.getSize(), performancePage.getTotal());
        resultPage.setRecords(dtoList);

        return resultPage;
    }

    @Override
    public AdminPerformanceDetailResponseDTO getPerformanceDetail(Long id) {
        // 查询演出
        Performance performance = performanceMapper.selectById(id);
        if (performance == null) {
            throw new BusinessException(ErrorCode.PERFORMANCE_IS_EMPTY);
        }

        // 构建响应DTO
        AdminPerformanceDetailResponseDTO response = new AdminPerformanceDetailResponseDTO();
        BeanUtils.copyProperties(performance, response);

        // 查询分类名称
        PerformanceCategory category = performanceCategoryMapper.selectById(performance.getCategoryId());
        if (category != null) {
            response.setCategoryName(category.getName());
        }

        // 查询城市名称
        Region region = regionMapper.selectById(performance.getCityId());
        if (region != null) {
            response.setCityName(region.getName());
        }

        // 查询场次和票档信息（包含库存数字）
        List<PerformanceSession> sessions = performanceSessionMapper.selectList(
                new LambdaQueryWrapper<PerformanceSession>()
                        .eq(PerformanceSession::getPerformanceId, id)
                        .orderByAsc(PerformanceSession::getSessionTime)
        );

        List<AdminSessionDetailDTO> sessionDetailDTOs = new ArrayList<>();
        for (PerformanceSession session : sessions) {
            AdminSessionDetailDTO sessionDTO = new AdminSessionDetailDTO();
            BeanUtils.copyProperties(session, sessionDTO);

            // 查询该场次的票档（包含库存数字）
            List<TicketGrade> ticketGrades = ticketGradeMapper.selectList(
                    new LambdaQueryWrapper<TicketGrade>()
                            .eq(TicketGrade::getPerformanceId, id)
                            .eq(TicketGrade::getSessionId, session.getId())
                            .orderByAsc(TicketGrade::getPrice)
            );

            List<AdminTicketGradeDetailDTO> gradeDTOs = ticketGrades.stream().map(grade -> {
                AdminTicketGradeDetailDTO gradeDTO = new AdminTicketGradeDetailDTO();
                BeanUtils.copyProperties(grade, gradeDTO);
                return gradeDTO;
            }).collect(Collectors.toList());

            sessionDTO.setTicketGrades(gradeDTOs);
            sessionDetailDTOs.add(sessionDTO);
        }

        response.setSessions(sessionDetailDTOs);

        return response;
    }

    @Override
    @Transactional
    public void deletePerformance(Long id) {
        // 查询演出是否存在
        Performance performance = performanceMapper.selectById(id);
        if (performance == null) {
            throw new BusinessException(ErrorCode.PERFORMANCE_IS_EMPTY);
        }

        // 删除演出（物理删除或逻辑删除，这里使用物理删除）
        // 注意：实际业务中可能需要检查是否有订单关联，如果有订单则不允许删除
        performanceMapper.deleteById(id);

        // 可选：同时删除关联的场次和票档
        // 这里可以根据业务需求决定是否级联删除
    }
}


package com.ticket.service.impl;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ticket.exception.BusinessException;
import com.ticket.exception.ErrorCode;
import com.ticket.mapper.*;
import com.ticket.model.dto.request.GrabTicketRequestDTO;
import com.ticket.model.dto.response.PerformanceDetailResponseDTO;
import com.ticket.model.dto.response.PerformancePageResponseDTO;
import com.ticket.model.dto.request.PerformanceQueryRequestDTO;
import com.ticket.model.dto.response.SessionDetailDTO;
import com.ticket.model.dto.response.TicketGradeDetailDTO;
import com.ticket.model.entity.*;
import com.ticket.service.PerformanceService;
import com.ticket.util.IpUtils;
import com.ticket.util.RedisUtils;
import com.ticket.util.UserContext;
import org.apache.ibatis.annotations.Lang;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PerformanceServiceImpl implements PerformanceService {
    @Resource
    private RegionMapper regionMapper;
    @Resource
    private PerformanceCategoryMapper performanceCategoryMapper;
    @Resource
    private PerformanceMapper performanceMapper;
    @Resource
    private PerformanceSessionMapper performanceSessionMapper;
    @Resource
    private TicketGradeMapper ticketGradeMapper;
    @Resource
    private RedisUtils redisUtils;

    private static final Integer MAX_GRAB_TICKET_COUNT = 5;

    // Redis缓存键常量
    private static final String REDIS_PERFORMANCE_STOCK_KEY = "performance:stock:"; // 票档库存缓存
    private static final String REDIS_USER_GRAB_LIMIT_KEY = "performance:grab:limit:"; // 用户抢票限制
    private static final String REDIS_GRAB_LOCK_KEY = "performance:grab:lock:"; // 抢票分布式锁

    /**
     * 条件+分页查询演出
     */
    @Override
    public PerformancePageResponseDTO queryPerformance(PerformanceQueryRequestDTO requestDTO,HttpServletRequest request) {
        // 构建分页参数
        IPage<Performance> page = new Page<>(requestDTO.getPageNum(), requestDTO.getPageSize());

        // 分页查询演出
        String keyword = requestDTO.getKeyword() != null ? requestDTO.getKeyword().trim() : null;
        Long cityId = requestDTO.getCityId() == null ? getCurrentCityId(request) : requestDTO.getCityId();
        Long categoryId = requestDTO.getCategoryId();

        // 构建查询条件
        LambdaQueryWrapper<Performance> queryWrapper = new LambdaQueryWrapper<Performance>()
                .eq(Performance::getCityId, cityId)
                //默认查四大类
                .in(categoryId == null || categoryId <= 0, Performance::getCategoryId, 1, 2, 3, 4)
                .eq(categoryId != null && categoryId > 0, Performance::getCategoryId, categoryId);

        // 处理keyword
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                    .like(Performance::getName, keyword)
                    .or().like(Performance::getStar, keyword)
            );
        }

        IPage<Performance> performancePage = performanceMapper.selectPage(page, queryWrapper);
        //转换为响应DTO
        List<Performance> performanceList = performancePage.getRecords();
        //空值校验：避免空指针
        if (performanceList == null || performanceList.isEmpty()) {
            throw new BusinessException(ErrorCode.PERFORMANCE_IS_EMPTY);
        }
        List<PerformanceDetailResponseDTO> dtoList = performanceList.stream()
                .map(performance -> {
                    PerformanceDetailResponseDTO dto = new PerformanceDetailResponseDTO();
                    dto.setId(performance.getId());
                    dto.setName(performance.getName());
                    dto.setStar(performance.getStar());
                    dto.setVenue(performance.getVenue());
                    dto.setStartTime(performance.getStartTime());
                    dto.setEndTime(performance.getEndTime());
                    dto.setCoverImage(performance.getCoverImage());
                    dto.setDescription(performance.getDescription());
                    dto.setStatus(performance.getStatus());
                    //todo
                    //没有去查询SessionDetailDTO

                    return dto;
                })
                .collect(Collectors.toList());


        // 4. 构建分页响应
        PerformancePageResponseDTO response = new PerformancePageResponseDTO();
        response.setRecords(dtoList);
        response.setTotal(performancePage.getTotal());
        response.setPageNum((int) performancePage.getCurrent());
        response.setPageSize((int) performancePage.getSize());
        response.setPages((int) performancePage.getPages());

        return response;
    }

    @Override
    public PerformanceDetailResponseDTO getPerformanceDetail(Long performanceId) {
        Performance performance = performanceMapper.selectById(performanceId);
        if (performance == null) {
            throw new BusinessException(ErrorCode.PERFORMANCE_IS_EMPTY);
        }

        List<PerformanceSession> sessions = performanceSessionMapper.selectList(new LambdaQueryWrapper<PerformanceSession>()
                .eq(PerformanceSession::getPerformanceId,performanceId));
        if (sessions.isEmpty()) {
            throw new BusinessException(ErrorCode.PERFORMANCE_SESSION_IS_EMPTY);
        }

        // 处理场次+票档信息（用户端仅显示是否有库存）
        List<SessionDetailDTO> sessionDetailDTOs = new ArrayList<>();
        for (PerformanceSession session : sessions) {
            SessionDetailDTO sessionDTO = new SessionDetailDTO();
            BeanUtils.copyProperties(session, sessionDTO);

            // 查询该场次的票档
            List<TicketGrade> ticketGrades = ticketGradeMapper.selectList(new LambdaQueryWrapper<TicketGrade>()
                            .eq(TicketGrade::getPerformanceId,performanceId)
                            .eq(TicketGrade::getSessionId,session.getId()));
            List<TicketGradeDetailDTO> gradeDTOs = ticketGrades.stream().map(grade -> {
                TicketGradeDetailDTO gradeDTO = new TicketGradeDetailDTO();
                BeanUtils.copyProperties(grade, gradeDTO);
                // 设置是否有库存
                boolean hasStock = grade.getRemainingStock() > 0 && grade.getStatus() == 1;
                gradeDTO.setHasStock(hasStock);
                return gradeDTO;
            }).collect(Collectors.toList());

            sessionDTO.setTicketGrades(gradeDTOs);
            sessionDetailDTOs.add(sessionDTO);
        }

        // 4. 构建响应DTO
        PerformanceDetailResponseDTO response = new PerformanceDetailResponseDTO();
        BeanUtils.copyProperties(performance, response);
        response.setSessions(sessionDetailDTOs);

        return response;
    }
    /**
     * 抢票接口
     */
    @Override
    @Transactional
    public void grabTicket(GrabTicketRequestDTO request) {
        Long userId = UserContext.getCurrentUserId();
        Long performanceId = request.getPerformanceId();
        Long sessionId = request.getSessionId();
        Long gradeId = request.getTicketGradeId();
        Integer count = request.getCount();

        // 基础校验
        baseGrabCheck(performanceId, sessionId, gradeId, count);

        // Redis预减库存
        String stockKey = REDIS_PERFORMANCE_STOCK_KEY + gradeId;
        Long remainStock = redisUtils.decrBy(stockKey, count);
        if (remainStock < 0) {
            // 库存不足，回滚Redis库存
            redisUtils.incrBy(stockKey, count);
            throw new BusinessException(ErrorCode.TICKET_STOCK_INSUFFICIENT);
        }

        // 防重复抢票
        String limitKey = REDIS_USER_GRAB_LIMIT_KEY + userId + ":" + performanceId;
        if (redisUtils.exists(limitKey)) {
            // 回滚库存
            redisUtils.incrBy(stockKey, count);
            throw new BusinessException(ErrorCode.REPEAT_GRAB_TICKET);
        }

        // 分布式锁控制并发（防止超卖）
        String lockKey = REDIS_GRAB_LOCK_KEY + gradeId;
        boolean lockValid = redisUtils.tryLock(lockKey, 5, java.util.concurrent.TimeUnit.SECONDS);
        if (!lockValid) {
            // 获取锁失败，回滚库存
            redisUtils.incrBy(stockKey, count);
            throw new BusinessException(ErrorCode.GRAB_TICKET_FAIL_RETRY);
        }

        try {
            //  查询票档信息
            TicketGrade ticketGrade = ticketGradeMapper.selectById(gradeId);
            if (ticketGrade == null || ticketGrade.getRemainingStock() < count) {
                throw new BusinessException(ErrorCode.TICKET_STOCK_INSUFFICIENT);
            }

            // 乐观锁扣减库存（version字段控制并发）
            ticketGrade.setRemainingStock(ticketGrade.getRemainingStock() - count);
            int rows = ticketGradeMapper.updateById(ticketGrade);
            if (rows == 0) {
                // 扣减失败（并发冲突），回滚Redis库存
                redisUtils.incrBy(stockKey, count);
                throw new BusinessException(ErrorCode.GRAB_TICKET_FAIL_RETRY);
            }

            //  设置抢票限制
            redisUtils.set(limitKey, "1", 30, java.util.concurrent.TimeUnit.MINUTES);

            //todo
            // 创建订单（通过RabbitMQ异步处理，此处省略，后续订单模块实现）


        } finally {
            // 释放分布式锁
            redisUtils.unlock(lockKey);
        }
    }
    /**
     * 获取当前用户所在城市ID
     */
    private Long getCurrentCityId(HttpServletRequest request) {
        Long cityId = null;
        try {
            // 已登录用户：根据IP获取城市
            String ip = IpUtils.getRealIp(request);
            String cityName = IpUtils.getCityArray(ip);
            LambdaQueryWrapper<Region> queryWrapper = new LambdaQueryWrapper<Region>()
                    .select(Region::getId)
                    .eq(Region::getName, cityName);
            Region region = regionMapper.selectOne(queryWrapper);
            cityId = region.getId();
        } catch (Exception e) {
            // IP解析失败，默认返回北京
            cityId = 1L;
        }
        return cityId != null ? cityId : 1L; // 北京的ID为1
    }
    /**
     * 抢票基础校验
     */
    private void baseGrabCheck(Long performanceId, Long sessionId, Long gradeId, Integer count) {
        // 校验演出是否存在且已开票
        Performance performance = performanceMapper.selectById(performanceId);
        if (performance == null) {
            throw new BusinessException(ErrorCode.PERFORMANCE_IS_EMPTY);
        }
        if (performance.getStatus() != 1) {
            throw new BusinessException(ErrorCode.PERFORMANCE_NOT_ON_SALE);
        }

        // 校验场次是否属于该演出且可售票
        Long sessionValid = performanceSessionMapper.selectCount(new LambdaQueryWrapper<PerformanceSession>()
                .eq(PerformanceSession::getPerformanceId,performanceId)
                .eq(PerformanceSession::getId,sessionId));

        if (sessionValid == null || sessionValid == 0L) {
            throw new BusinessException(ErrorCode.SESSION_NOT_VALID);
        }
        PerformanceSession session = performanceSessionMapper.selectById(sessionId);
        if (session.getStatus() != 1) {
            throw new BusinessException(ErrorCode.SESSION_NOT_ON_SALE);
        }

        // 校验票档是否属于该场次
        Long gradeValid = ticketGradeMapper.selectCount(new LambdaQueryWrapper<TicketGrade>()
                .eq(TicketGrade::getId,gradeId)
                .eq(TicketGrade::getSessionId,sessionId));
        if (gradeValid == null || gradeValid == 0) {
            throw new BusinessException(ErrorCode.TICKET_GRADE_NOT_VALID);
        }

        // 校验购票数量
        if (count > MAX_GRAB_TICKET_COUNT) {
            throw new BusinessException(ErrorCode.TICKET_COUNT_EXCEED_LIMIT);
        }

        // 初始化Redis库存缓存（若不存在）
        String stockKey = REDIS_PERFORMANCE_STOCK_KEY + gradeId;
        if (!redisUtils.exists(stockKey)) {
            Integer dbStock = ticketGradeMapper.selectOne(new LambdaQueryWrapper<TicketGrade>()
                    .eq(TicketGrade::getId,gradeId)).getRemainingStock();
            redisUtils.set(stockKey, dbStock != null ? dbStock : 0);
        }
    }
}

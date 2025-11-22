package com.ticket.service.impl;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ticket.config.RabbitMQConfig;
import com.ticket.enums.OrderStatusEnum;
import com.ticket.exception.BusinessException;
import com.ticket.exception.ErrorCode;
import com.ticket.mapper.*;
import com.ticket.model.dto.msg.OrderCreateMsgDTO;
import com.ticket.model.dto.request.GrabTicketRequestDTO;
import com.ticket.model.dto.response.PerformanceDetailResponseDTO;
import com.ticket.model.dto.response.PerformancePageResponseDTO;
import com.ticket.model.dto.request.PerformanceQueryRequestDTO;
import com.ticket.model.dto.response.SessionDetailDTO;
import com.ticket.model.dto.response.TicketGradeDetailDTO;
import com.ticket.model.entity.*;
import com.ticket.service.PerformanceService;
import com.ticket.util.IpUtils;
import com.ticket.util.OrderNoGeneratorUtils;
import com.ticket.util.RedisUtils;
import com.ticket.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Lang;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



@Service
@Slf4j
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
    private OrderMapper orderMapper;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private RabbitTemplate rabbitTemplate;

    private static final Integer MAX_GRAB_TICKET_COUNT = 5;

    // 票档库存缓存
    private static final String REDIS_PERFORMANCE_STOCK_KEY = "performance:stock:";
    // 用户抢票限制
    private static final String REDIS_USER_GRAB_LIMIT_KEY = "performance:grab:limit:";
    // 抢票分布式锁
    private static final String REDIS_GRAB_LOCK_KEY = "performance:grab:lock:";

    // RabbitMQ交换机/队列名
    private static final String ORDER_EXCHANGE = "order.direct";
    private static final String ORDER_ROUTING_KEY = "order.create";

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

            // 生成订单
            Order order = buildOrder(request, userId, ticketGrade);
            orderMapper.insert(order);

            // 6. 发送RabbitMQ消息（订单创建成功后，用于超时取消、通知用户等）
            sendOrderCreateMsg(order);

        } finally {
            // 释放分布式锁
            redisUtils.unlock(lockKey);
        }
    }

    /**
     * 构建订单实体
     */
    private Order buildOrder(GrabTicketRequestDTO request, Long userId, TicketGrade grade) {
        Order order = new Order();
        // 生成唯一订单号
        order.setOrderNo(OrderNoGeneratorUtils.generate());
        order.setUserId(userId);
        order.setPerformanceId(request.getPerformanceId());
        order.setSessionId(request.getSessionId());
        order.setTicketGradeId(request.getTicketGradeId());
        order.setGradeName(grade.getGradeName());
        order.setPrice(grade.getPrice());
        order.setCount(request.getCount());
        order.setTotalAmount(grade.getPrice().multiply(new BigDecimal(request.getCount())));
        // 待支付
        order.setStatus(OrderStatusEnum.PENDING_PAY.getCode());
        order.setCreateTime(LocalDateTime.now());
        // 15分钟过期
        //todo 测试只设置为15秒
//        order.setExpireTime(LocalDateTime.now().plusMinutes(15));
        order.setExpireTime(LocalDateTime.now().plusSeconds(15));
        return order;
    }
    /**
     * 发送订单创建消息到RabbitMQ
     */
    private void sendOrderCreateMsg(Order order) {
        try {
            OrderCreateMsgDTO msg = new OrderCreateMsgDTO();
            msg.setOrderId(order.getId());
            msg.setOrderNo(order.getOrderNo());
            msg.setExpireTime(order.getExpireTime()); // 订单过期时间（用于二次校验）

            // 发送延迟消息：指定延迟时间为15分钟
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ORDER_DELAY_EXCHANGE,
                    RabbitMQConfig.ORDER_DELAY_ROUTING_KEY,
                    msg,
                    message -> {
                        // 设置延迟时间（毫秒）
//                        message.getMessageProperties().setHeader("x-delay", 15 * 60 * 1000);
                        message.getMessageProperties().setHeader("x-delay", 15 * 1000);
                        return message;
                    },
                    new CorrelationData(order.getOrderNo()) // 消息ID，用于确认
            );
        } catch (Exception e) {
            log.error("发送延迟订单消息失败：{}", e.getMessage());
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

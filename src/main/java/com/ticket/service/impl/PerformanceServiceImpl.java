package com.ticket.service.impl;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ticket.exception.BusinessException;
import com.ticket.exception.ErrorCode;
import com.ticket.mapper.PerformanceCategoryMapper;
import com.ticket.mapper.PerformanceMapper;
import com.ticket.mapper.RegionMapper;
import com.ticket.model.dto.request.GrabTicketRequestDTO;
import com.ticket.model.dto.response.PerformanceDetailResponseDTO;
import com.ticket.model.dto.response.PerformancePageResponseDTO;
import com.ticket.model.dto.request.PerformanceQueryRequestDTO;
import com.ticket.model.entity.Performance;
import com.ticket.model.entity.PerformanceCategory;
import com.ticket.model.entity.Region;
import com.ticket.service.PerformanceService;
import com.ticket.util.IpUtils;
import org.springframework.stereotype.Service;

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

    /**
     * 条件+分页查询演出
     */
    @Override
    public PerformancePageResponseDTO queryPerformance(PerformanceQueryRequestDTO request) {
        // 1. 构建分页参数
        IPage<Performance> page = new Page<>(request.getPageNum(), request.getPageSize());

        // 2. 分页查询演出
        String keyword = request.getKeyword();
        Long cityId = request.getCityId();
        Long categoryId = request.getCategoryId();

        LambdaQueryWrapper<Performance> queryWrapper = new LambdaQueryWrapper<Performance>()
                // 精确匹配cityId
                .eq(cityId != null,Performance::getCityId, cityId)
                // 精确匹配categoryId
                .eq(categoryId !=null,Performance::getCategoryId, categoryId);

        //处理keyword模糊查询
        if (keyword != null && !keyword.trim().isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                    // 模糊匹配演出名
                    .like(Performance::getName, keyword)
                    // 或模糊匹配明星名
                    .or().like(Performance::getStar, keyword)
            );
        }
        IPage<Performance> performancePage = performanceMapper.selectPage(page, queryWrapper);
        // 3. 转换为响应DTO
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
        return null;
    }

    @Override
    public void grabTicket(GrabTicketRequestDTO request) {

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
}

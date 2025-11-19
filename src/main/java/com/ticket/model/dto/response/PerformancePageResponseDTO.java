package com.ticket.model.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class PerformancePageResponseDTO {
    private List<PerformanceDetailResponseDTO> records;
    // 总条数
    private Long total;
    private Integer pageNum;
    private Integer pageSize;
    // 总页数
    private Integer pages;
}
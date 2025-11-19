package com.ticket.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("performance_session")
public class PerformanceSession {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long performanceId;
    private LocalDateTime sessionTime;
    // 0-未开售，1-售票中，2-已售罄，3-已结束
    private Integer status;
}
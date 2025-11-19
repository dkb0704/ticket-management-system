package com.ticket.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("performance")
public class Performance {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Long categoryId;
    private Long cityId;
    private String venue;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String star;
    private String coverImage;
    private String description;
    // 0-未开票，1-已开票，2-已结束
    private Integer status;
    private LocalDateTime ticketStartTime;
    private LocalDateTime ticketEndTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
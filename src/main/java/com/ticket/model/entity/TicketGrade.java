package com.ticket.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;
import java.math.BigDecimal;

@Data
@TableName("ticket_grade")
public class TicketGrade {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long performanceId;
    private Long sessionId;
    private String gradeName;
    private BigDecimal price;
    private Integer totalStock;
    private Integer remainingStock;
    // 0-未开售，1-销售中，2-已售罄
    private Integer status;
    //乐观所注解
    @Version
    private Integer version;
}
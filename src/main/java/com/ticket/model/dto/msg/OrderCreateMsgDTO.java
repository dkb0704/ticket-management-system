package com.ticket.model.dto.msg;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 订单创建消息DTO（用于RabbitMQ消息传递）
 */
@Data
public class OrderCreateMsgDTO implements Serializable {
    /**
     * 序列化版本号（保证消息序列化/反序列化兼容）
     */
    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单编号（唯一标识）
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 订单创建时间
     */
    private LocalDateTime createTime;

    /**
     * 订单过期时间（用于判断是否超时未支付）
     */
    private LocalDateTime expireTime;

    /**
     * 票档ID（如需操作库存时使用）
     */
    private Long ticketGradeId;

    /**
     * 购票数量（取消订单时恢复库存用）
     */
    private Integer count;
}
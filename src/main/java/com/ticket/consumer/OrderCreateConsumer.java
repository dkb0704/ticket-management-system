package com.ticket.consumer;

import com.rabbitmq.client.Channel;
import com.ticket.config.RabbitMQConfig;
import com.ticket.enums.OrderStatusEnum;
import com.ticket.exception.BusinessException;
import com.ticket.model.dto.msg.OrderCreateMsgDTO;
import com.ticket.model.entity.Order;
import com.ticket.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class OrderCreateConsumer {

    @Autowired
    private OrderService orderService;

    /**
     * 监听订单延迟队列
     * 手动ACK
     */

    @RabbitListener(queues = RabbitMQConfig.ORDER_TIMEOUT_QUEUE,ackMode = "MANUAL" )
    public void handleOrderTimeout(OrderCreateMsgDTO msg, Channel channel, Message message) throws Exception {
        try {
            log.info("处理订单超时检查：{}", msg.getOrderNo());

            // 查询订单
            Order order = orderService.getOrderByNo(msg.getOrderNo());
            if (order == null) {
                log.warn("订单不存在：{}", msg.getOrderNo());
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }

            // 校验订单状态和过期时间
            if (OrderStatusEnum.PENDING_PAY.getCode().equals(order.getStatus())
                    && LocalDateTime.now().isAfter(order.getExpireTime())) {
                // 订单未支付且已过期，将订单设置为已取消
                orderService.setOrderStatus(order.getId(), order.getUserId(), OrderStatusEnum.CANCELLED.getCode());
                log.info("订单超时自动取消：{}", msg.getOrderNo());
            }

            // 手动ACK确认消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (BusinessException e) {
            log.error("处理订单超时异常：{}", e.getMessage());
            //todo 这里设置为了直接删除
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
        }
    }
}

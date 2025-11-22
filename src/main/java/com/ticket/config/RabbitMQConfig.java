package com.ticket.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    // 延迟交换机（处理订单超时）
    public static final String ORDER_DELAY_EXCHANGE = "order.delay.exchange";
    // 订单超时处理队列
    public static final String ORDER_TIMEOUT_QUEUE = "order.timeout.queue";
    // 延迟路由键
    public static final String ORDER_DELAY_ROUTING_KEY = "order.delay";

    /**
     * 声明延迟交换机（类型为x-delayed-message）
     */
    @Bean
    public CustomExchange orderDelayExchange() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct"); // 延迟交换机的底层类型（direct/fanout等）
        // 类型为x-delayed-message，持久化、非自动删除
        return new CustomExchange(ORDER_DELAY_EXCHANGE, "x-delayed-message", true, false, args);
    }

    /**
     * 订单超时队列（处理订单超时取消）
     */
    @Bean
    public Queue orderTimeoutQueue() {
        return QueueBuilder.durable(ORDER_TIMEOUT_QUEUE).build();
    }

    /**
     * 绑定延迟交换机和超时队列
     */
    @Bean
    public Binding orderDelayBinding() {
        return BindingBuilder.bind(orderTimeoutQueue())
                .to(orderDelayExchange())
                .with(ORDER_DELAY_ROUTING_KEY)
                .noargs();
    }
}
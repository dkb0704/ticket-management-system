package com.ticket.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class OrderNoGeneratorUtils {
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /**
     * 生成唯一订单号：时间戳（14位） + UUID + userId
     */
    public static String generate() {
        String time = LocalDateTime.now().format(DF);
        UUID uuid = UUID.randomUUID();
        return time + uuid + "-" ;

    }
}

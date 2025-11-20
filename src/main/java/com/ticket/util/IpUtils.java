package com.ticket.util;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ticket.exception.ErrorCode;
import com.ticket.exception.UtilException;
import org.springframework.util.StringUtils;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class IpUtils {
    // 定义直辖市列表（匹配前缀用）
    private static final Set<String> MUNICIPALITIES = new HashSet<>(Arrays.asList("北京", "上海", "天津", "重庆"));

    /**
     * 获取用户真实IP（处理反向代理、负载均衡场景）
     */
    public static String getRealIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (!StringUtils.isEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个IP值，第一个为真实IP
            int index = ip.indexOf(',');
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        ip = request.getHeader("Proxy-Client-IP");
        if (!StringUtils.isEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        ip = request.getHeader("WL-Proxy-Client-IP");
        if (!StringUtils.isEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }

    public static String getCityArray(String ip) {
        try {
            // 这里调用pconline的接口
            String url = "https://opendata.baidu.com/api.php";
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("query", ip);
            paramMap.put("co", "");
            paramMap.put("resource_id", "6006");
            paramMap.put("oe", "utf8");
            // 带参GET请求
            String returnStr = HttpUtil.get(url, paramMap);
            System.out.println(returnStr);

            if (returnStr != null) {
                JSONObject rs = JSONUtil.parseObj(returnStr);

                JSONArray location = rs.getJSONArray("data");

                JSONObject o = location.getJSONObject(0);

                String rareLocation = o.getStr("location");
                return extractCity(rareLocation);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public static String extractCity(String ipParseStr) {
        if (ipParseStr == null || ipParseStr.trim().isEmpty()) {
            return null;
        }

        // 剥离运营商后缀
        String cityPart = ipParseStr.split("\\s+")[0];

        // 处理直辖市
        for (String municipality : MUNICIPALITIES) {
            if (cityPart.startsWith(municipality)) {
                return municipality + "市";
            }
        }

        // 处理普通省份
        if (cityPart.contains("省")) {
            String[] parts = cityPart.split("省");
            if (parts.length >= 2) {
                return parts[1];
            }
        }

        if (cityPart.contains("自治区")) {
            String[] parts = cityPart.split("自治区");
            if (parts.length >= 2) {
                return parts[1];
            }
        }

        throw new UtilException(ErrorCode.IP_OPERATION_FAIL);
    }


}
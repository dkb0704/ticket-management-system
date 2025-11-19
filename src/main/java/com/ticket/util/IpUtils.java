package com.ticket.util;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.util.StringUtils;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class IpUtils {
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

                String location1 = o.getStr("location");
                return location1;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
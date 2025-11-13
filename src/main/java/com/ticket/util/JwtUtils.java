package com.ticket.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {

    private static String secret;
    private static int expireHours;

    @Value("${jwt.secret}")
    public void setSecret(String secret) {
        JwtUtils.secret = secret;
    }

    @Value("${jwt.expire-hours}")
    public void setExpireHours(int expireHours) {
        JwtUtils.expireHours = expireHours;
    }

    public static String generateToken(Long userId) {
        Date expireDate = new Date(System.currentTimeMillis() + expireHours * 3600 * 1000);
        return Jwts.builder()
                .setSubject(userId.toString()) // 存储用户ID
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS256, secret) // 使用静态secret
                .compact();
    }

    public static Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secret) // 使用静态secret
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }
    // 解析 Token 获取过期时间
    public Date getExpiration(String token) {
        return parseClaims(token).getExpiration();
    }

    // 计算 Token 剩余有效期（秒）
    public long getRemainingSeconds(String token) {
        Date expiration = getExpiration(token);
        long remaining = (expiration.getTime() - System.currentTimeMillis()) / 1000;
        return Math.max(remaining, 0);
    }
    // 解析 Token 核心方法
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }
}

package com.ticket.config;

import com.ticket.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                //todo 路径分离出来
                .addPathPatterns("/api/user/info","/api/user/addresses","/api/user/addresses/**","/api/performance/grab")
                .excludePathPatterns("/api/auth/login", "/api/auth/logout","/api/performance/list");
    }
}
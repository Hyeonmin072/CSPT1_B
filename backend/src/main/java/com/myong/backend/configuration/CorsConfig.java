package com.myong.backend.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "DELETE", "PUT", "PATCH") // 허용할 HTTP 메소드
                .allowedHeaders("*") // 필요하면 헤더도 허용
                .allowCredentials(true); //  쿠키/인증 정보 포함 허용

    }
}

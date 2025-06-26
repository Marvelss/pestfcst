package cn.edu.hdu.pestfcst.featureoptimizationservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // 允许跨域的头部信息
        config.addAllowedHeader("*");
        // 允许跨域的方法
        config.addAllowedMethod("*");
        // 可访问的外域请求
        config.addAllowedOrigin("*");
        // 允许携带cookie跨域
        config.setAllowCredentials(true);
        // 暴露头部信息
        config.addExposedHeader("Authorization");
        
        // 添加映射路径，拦截一切请求
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
} 
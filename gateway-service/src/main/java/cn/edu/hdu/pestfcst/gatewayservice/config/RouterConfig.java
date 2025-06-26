package cn.edu.hdu.pestfcst.gatewayservice.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouterConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // API网关健康检查路径
                .route("gateway-health", r -> r.path("/api/health")
                        .filters(f -> f.rewritePath("/api/health", "/gateway/status"))
                        .uri("http://localhost:8080"))
                
                // 特征优化服务路由
                .route("feature-optimization", r -> r.path("/api/feature/**")
                        .filters(f -> f.rewritePath("/api/feature/(?<segment>.*)", "/${segment}"))
                        .uri("http://localhost:8091"))
                
                .build();
    }
} 
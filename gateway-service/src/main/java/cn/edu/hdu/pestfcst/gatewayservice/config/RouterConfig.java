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
                        .uri("lb://gateway-service"))
                
                // 在这里可以添加更多的路由配置
                
                .build();
    }
} 
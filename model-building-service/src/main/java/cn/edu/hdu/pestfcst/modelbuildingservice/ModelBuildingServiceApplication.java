package cn.edu.hdu.pestfcst.modelbuildingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
public class ModelBuildingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModelBuildingServiceApplication.class, args);
        System.out.println("模型构建服务已启动成功!");
    }
    
    // 添加配置类，允许禁用HDFS相关功能
    @Configuration
    public static class HDFSConfig {
        @Bean
        @ConditionalOnProperty(name = "hdfs.enabled", havingValue = "true", matchIfMissing = false)
        public Object hdfsInitializer() {
            // 这里可以添加HDFS初始化代码
            return new Object();
        }
    }
}

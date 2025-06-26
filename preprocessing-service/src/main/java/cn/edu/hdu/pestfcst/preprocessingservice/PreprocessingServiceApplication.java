package cn.edu.hdu.pestfcst.preprocessingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author : Claude
 * @Time: 2025-06-25
 * @File : PreprocessingServiceApplication.java
 * @Description : 预处理服务主应用
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class PreprocessingServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PreprocessingServiceApplication.class, args);
    }
} 
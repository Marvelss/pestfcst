package cn.edu.hdu.pestfcst.datamanagementservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author : Claude
 * @Time: 2025-06-25
 * @File : DataManagementServiceApplication.java
 * @Description : 数据管理服务主应用
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class DataManagementServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DataManagementServiceApplication.class, args);
    }
} 
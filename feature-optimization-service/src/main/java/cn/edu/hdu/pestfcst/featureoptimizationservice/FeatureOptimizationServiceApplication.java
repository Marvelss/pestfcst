package cn.edu.hdu.pestfcst.featureoptimizationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FeatureOptimizationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FeatureOptimizationServiceApplication.class, args);
        System.out.println("特征优化服务已启动成功!");
    }

}

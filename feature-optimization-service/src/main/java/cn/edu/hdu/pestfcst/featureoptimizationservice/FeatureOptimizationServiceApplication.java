package cn.edu.hdu.pestfcst.featureoptimizationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

// 暂时排除数据源和JPA自动配置，避免因数据库连接问题导致服务无法启动
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class FeatureOptimizationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FeatureOptimizationServiceApplication.class, args);
        System.out.println("特征优化服务已启动成功!");
    }

}

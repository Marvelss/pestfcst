package cn.edu.hdu.pestfcst.modelbuildingservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FileUploadConfig implements WebMvcConfigurer {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置静态资源映射，用于直接访问上传的文件
        Path uploadDir = Paths.get(uploadPath);
        String uploadAbsolutePath = uploadDir.toFile().getAbsolutePath();
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadAbsolutePath + "/");
    }
} 
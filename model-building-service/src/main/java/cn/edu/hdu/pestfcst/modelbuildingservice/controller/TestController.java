package cn.edu.hdu.pestfcst.modelbuildingservice.controller;

import cn.edu.hdu.pestfcst.modelbuildingservice.bean.ApiResponse;
import cn.edu.hdu.pestfcst.modelbuildingservice.bean.User;
import cn.edu.hdu.pestfcst.modelbuildingservice.dao.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test")
@CrossOrigin(origins = "*")
public class TestController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/hello")
    public ApiResponse<String> hello() {
        return ApiResponse.success("Hello from Model Building Service!");
    }

    @GetMapping("/users")
    public ApiResponse<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ApiResponse.success("获取所有用户成功", users);
    }

    @PostMapping("/create-test-user")
    public ApiResponse<User> createTestUser() {
        User user = new User();
        user.setUsername("testuser_" + System.currentTimeMillis());
        user.setPassword("123456");
        user.setCreateTime(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        return ApiResponse.success("测试用户创建成功", savedUser);
    }

    @GetMapping("/db-test")
    public ApiResponse<Map<String, Object>> testDatabase() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            long userCount = userRepository.count();
            result.put("userCount", userCount);
            result.put("databaseStatus", "Connected");
            result.put("timestamp", LocalDateTime.now());
            
            return ApiResponse.success("数据库连接测试成功", result);
        } catch (Exception e) {
            result.put("error", e.getMessage());
            return ApiResponse.error("数据库连接测试失败", result);
        }
    }

    @GetMapping("/config-test")
    public ApiResponse<Map<String, Object>> testConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("serverPort", "8092");
        config.put("databaseUrl", "jdbc:postgresql://localhost:5432/pest_model_building");
        config.put("fileUploadPath", "uploads");
        config.put("maxFileSize", "10MB");
        config.put("timestamp", LocalDateTime.now());
        
        return ApiResponse.success("配置信息", config);
    }
} 
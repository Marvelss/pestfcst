package cn.edu.hdu.pestfcst.modelbuildingservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ApiDocController {

    @GetMapping("/docs")
    public Map<String, Object> getApiDocs() {
        Map<String, Object> docs = new HashMap<>();
        
        // 用户管理接口
        Map<String, Object> userApis = new HashMap<>();
        userApis.put("register", Map.of(
            "method", "POST",
            "url", "/api/user/register",
            "description", "用户注册",
            "request", Map.of(
                "username", "String (3-20字符)",
                "password", "String (6-20字符)"
            ),
            "response", Map.of(
                "success", "Boolean",
                "message", "String",
                "userId", "Long",
                "username", "String",
                "createTime", "LocalDateTime"
            )
        ));
        
        userApis.put("login", Map.of(
            "method", "POST",
            "url", "/api/user/login",
            "description", "用户登录",
            "request", Map.of(
                "username", "String",
                "password", "String"
            ),
            "response", Map.of(
                "success", "Boolean",
                "message", "String",
                "userId", "Long",
                "username", "String",
                "createTime", "LocalDateTime",
                "loginTime", "LocalDateTime"
            )
        ));
        
        userApis.put("changePassword", Map.of(
            "method", "POST",
            "url", "/api/user/change-password",
            "description", "修改密码",
            "request", Map.of(
                "userId", "Long",
                "oldPassword", "String",
                "newPassword", "String"
            ),
            "response", Map.of(
                "success", "Boolean",
                "message", "String"
            )
        ));
        
        userApis.put("getUserInfo", Map.of(
            "method", "GET",
            "url", "/api/user/info/{userId}",
            "description", "获取用户信息",
            "response", Map.of(
                "success", "Boolean",
                "data", Map.of(
                    "userId", "Long",
                    "username", "String",
                    "createTime", "LocalDateTime"
                )
            )
        ));
        
        userApis.put("checkUsername", Map.of(
            "method", "GET",
            "url", "/api/user/check-username/{username}",
            "description", "检查用户名是否可用",
            "response", Map.of(
                "success", "Boolean",
                "available", "Boolean",
                "message", "String"
            )
        ));
        
        // 文件管理接口
        Map<String, Object> fileApis = new HashMap<>();
        fileApis.put("upload", Map.of(
            "method", "POST",
            "url", "/api/user/upload",
            "description", "上传单个文件",
            "request", Map.of(
                "file", "MultipartFile",
                "userId", "String",
                "description", "String (可选)"
            ),
            "response", Map.of(
                "success", "Boolean",
                "message", "String",
                "fileId", "Long",
                "fileName", "String",
                "fileSize", "Long",
                "uploadTime", "String"
            )
        ));
        
        fileApis.put("uploadMultiple", Map.of(
            "method", "POST",
            "url", "/api/user/upload-multiple",
            "description", "批量上传文件",
            "request", Map.of(
                "files", "MultipartFile[]",
                "userId", "String"
            ),
            "response", Map.of(
                "success", "Boolean",
                "message", "String",
                "totalFiles", "Integer",
                "successCount", "Integer",
                "failCount", "Integer",
                "uploadResults", "List<Map>"
            )
        ));
        
        fileApis.put("download", Map.of(
            "method", "GET",
            "url", "/api/user/download/{fileId}?userId={userId}",
            "description", "下载文件",
            "response", "File Resource"
        ));
        
        fileApis.put("delete", Map.of(
            "method", "DELETE",
            "url", "/api/user/delete-file/{fileId}?userId={userId}",
            "description", "删除文件",
            "response", Map.of(
                "success", "Boolean",
                "message", "String"
            )
        ));
        
        fileApis.put("getFiles", Map.of(
            "method", "GET",
            "url", "/api/user/files/{userId}?status={status}",
            "description", "获取用户文件列表",
            "response", Map.of(
                "success", "Boolean",
                "data", "List<Map>"
            )
        ));
        
        fileApis.put("getFileStats", Map.of(
            "method", "GET",
            "url", "/api/user/files/{userId}/stats",
            "description", "获取用户文件统计信息",
            "response", Map.of(
                "success", "Boolean",
                "data", Map.of(
                    "fileCount", "Long",
                    "totalSize", "Long",
                    "totalSizeMB", "String"
                )
            )
        ));
        
        docs.put("user", userApis);
        docs.put("file", fileApis);
        docs.put("baseUrl", "http://localhost:8092");
        docs.put("version", "1.0.0");
        docs.put("description", "病虫害预测系统 - 模型构建服务 API 文档");
        
        return docs;
    }
    
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "model-building-service");
        health.put("timestamp", System.currentTimeMillis());
        return health;
    }
} 
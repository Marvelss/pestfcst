package cn.edu.hdu.pestfcst.modelbuildingservice.controller;

import cn.edu.hdu.pestfcst.modelbuildingservice.bean.FileRecord;
import cn.edu.hdu.pestfcst.modelbuildingservice.bean.User;
import cn.edu.hdu.pestfcst.modelbuildingservice.dao.UserRepository;
import cn.edu.hdu.pestfcst.modelbuildingservice.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*") // 允许跨域请求
public class UserController {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private FileService fileService;

    @Value("${file.upload.path}")
    private String uploadPath;

    /**
     * 用户注册接口
     * @param req 包含username和password的请求体
     * @return 注册结果
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> req) {
        try {
            String username = req.get("username");
            String password = req.get("password");
            
            // 参数验证
            if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false, 
                    "message", "用户名和密码不能为空"
                ));
            }
            
            // 用户名长度验证
            if (username.length() < 3 || username.length() > 20) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false, 
                    "message", "用户名长度必须在3-20个字符之间"
                ));
            }
            
            // 密码长度验证
            if (password.length() < 6 || password.length() > 20) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false, 
                    "message", "密码长度必须在6-20个字符之间"
                ));
            }
            
            // 检查用户名是否已存在
            if (userRepository.findByUsername(username) != null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false, 
                    "message", "用户名已存在，请选择其他用户名"
                ));
            }
            
            // 创建新用户
            User user = new User();
            user.setUsername(username.trim());
            user.setPassword(password); // 实际项目中应该加密存储
            user.setCreateTime(LocalDateTime.now());
            
            User savedUser = userRepository.save(user);
            
            return ResponseEntity.ok(Map.of(
                "success", true, 
                "message", "注册成功",
                "userId", savedUser.getId(),
                "username", savedUser.getUsername(),
                "createTime", savedUser.getCreateTime()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false, 
                "message", "注册失败：" + e.getMessage()
            ));
        }
    }

    /**
     * 用户登录接口
     * @param req 包含username和password的请求体
     * @return 登录结果
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> req) {
        try {
            String username = req.get("username");
            String password = req.get("password");
            
            // 参数验证
            if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false, 
                    "message", "用户名和密码不能为空"
                ));
            }
            
            // 查找用户
            User user = userRepository.findByUsername(username.trim());
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false, 
                    "message", "用户名不存在"
                ));
            }
            
            // 验证密码
            if (!user.getPassword().equals(password)) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false, 
                    "message", "密码错误"
                ));
            }
            
            // 登录成功，返回用户信息
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("message", "登录成功");
            resp.put("userId", user.getId());
            resp.put("username", user.getUsername());
            resp.put("createTime", user.getCreateTime());
            resp.put("loginTime", LocalDateTime.now());
            
            return ResponseEntity.ok(resp);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false, 
                "message", "登录失败：" + e.getMessage()
            ));
        }
    }

    /**
     * 文件上传接口
     * @param file 上传的文件
     * @param userId 用户ID
     * @param description 文件描述
     * @return 上传结果
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, 
                                       @RequestParam("userId") String userId,
                                       @RequestParam(value = "description", required = false) String description) {
        try {
            Long userIdLong = Long.parseLong(userId);
            Map<String, Object> result = fileService.uploadFile(file, userIdLong, description);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "用户ID格式错误"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "文件上传失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 批量文件上传接口
     * @param files 文件数组
     * @param userId 用户ID
     * @return 上传结果
     */
    @PostMapping("/upload-multiple")
    public ResponseEntity<?> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files,
                                                @RequestParam("userId") String userId) {
        try {
            Long userIdLong = Long.parseLong(userId);
            Map<String, Object> result = fileService.uploadMultipleFiles(files, userIdLong);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "用户ID格式错误"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "批量上传失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 文件下载接口
     * @param fileId 文件ID
     * @param userId 用户ID
     * @return 文件资源
     */
    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId,
                                                @RequestParam("userId") String userId) {
        try {
            Long userIdLong = Long.parseLong(userId);
            Resource resource = fileService.downloadFile(fileId, userIdLong);
            
            if (resource != null) {
                FileRecord fileRecord = fileService.getFileInfo(fileId, userIdLong);
                String filename = fileRecord != null ? fileRecord.getOriginalFilename() : "file";
                
                return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"" + filename + "\"")
                    .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 删除文件接口
     * @param fileId 文件ID
     * @param userId 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/delete-file/{fileId}")
    public ResponseEntity<?> deleteFile(@PathVariable Long fileId,
                                       @RequestParam("userId") String userId) {
        try {
            Long userIdLong = Long.parseLong(userId);
            Map<String, Object> result = fileService.deleteFile(fileId, userIdLong);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "用户ID格式错误"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "文件删除失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 获取用户文件列表接口
     * @param userId 用户ID
     * @param status 文件状态（可选，默认为active）
     * @return 文件列表
     */
    @GetMapping("/files/{userId}")
    public ResponseEntity<?> getUserFiles(@PathVariable String userId,
                                         @RequestParam(value = "status", defaultValue = "active") String status) {
        try {
            Long userIdLong = Long.parseLong(userId);
            List<FileRecord> fileList = fileService.getUserFiles(userIdLong, status);
            
            // 转换为前端需要的格式
            List<Map<String, Object>> fileInfoList = fileList.stream()
                .map(fileRecord -> {
                    Map<String, Object> fileInfo = new HashMap<>();
                    fileInfo.put("fileId", fileRecord.getId());
                    fileInfo.put("fileName", fileRecord.getOriginalFilename());
                    fileInfo.put("fileSize", fileRecord.getFileSize());
                    fileInfo.put("fileType", fileRecord.getFileType());
                    fileInfo.put("uploadTime", fileRecord.getUploadTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    fileInfo.put("status", fileRecord.getStatus());
                    fileInfo.put("description", fileRecord.getDescription());
                    return fileInfo;
                })
                .toList();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", fileInfoList
            ));
            
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "用户ID格式错误"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "获取文件列表失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 获取用户文件统计信息
     * @param userId 用户ID
     * @return 统计信息
     */
    @GetMapping("/files/{userId}/stats")
    public ResponseEntity<?> getUserFileStats(@PathVariable String userId) {
        try {
            Long userIdLong = Long.parseLong(userId);
            Map<String, Object> stats = fileService.getUserFileStats(userIdLong);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", stats
            ));
            
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "用户ID格式错误"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "获取统计信息失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 获取用户信息接口
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/info/{userId}")
    public ResponseEntity<?> getUserInfo(@PathVariable Long userId) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("createTime", user.getCreateTime());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", userInfo
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false, 
                "message", "获取用户信息失败：" + e.getMessage()
            ));
        }
    }

    /**
     * 检查用户名是否可用
     * @param username 用户名
     * @return 检查结果
     */
    @GetMapping("/check-username/{username}")
    public ResponseEntity<?> checkUsername(@PathVariable String username) {
        try {
            User existingUser = userRepository.findByUsername(username);
            boolean isAvailable = existingUser == null;
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "available", isAvailable,
                "message", isAvailable ? "用户名可用" : "用户名已存在"
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false, 
                "message", "检查用户名失败：" + e.getMessage()
            ));
        }
    }

    /**
     * 修改密码接口
     * @param req 包含userId、oldPassword和newPassword的请求体
     * @return 修改结果
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> req) {
        try {
            String userIdStr = req.get("userId");
            String oldPassword = req.get("oldPassword");
            String newPassword = req.get("newPassword");
            
            if (userIdStr == null || oldPassword == null || newPassword == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "参数不完整"
                ));
            }
            
            Long userId = Long.parseLong(userIdStr);
            User user = userRepository.findById(userId).orElse(null);
            
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "用户不存在"
                ));
            }
            
            if (!user.getPassword().equals(oldPassword)) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "原密码错误"
                ));
            }
            
            if (newPassword.length() < 6 || newPassword.length() > 20) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "新密码长度必须在6-20个字符之间"
                ));
            }
            
            user.setPassword(newPassword);
            userRepository.save(user);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "密码修改成功"
            ));
            
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "用户ID格式错误"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "密码修改失败：" + e.getMessage()
            ));
        }
    }
}
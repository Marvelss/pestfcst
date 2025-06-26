package cn.edu.hdu.pestfcst.datamanagementservice.controller;

import cn.edu.hdu.pestfcst.datamanagementservice.bean.SpatialDataset;
import cn.edu.hdu.pestfcst.datamanagementservice.service.SpatialDatasetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author : Claude
 * @Time: 2025-06-25
 * @File : SpatialDatasetController.java
 * @Description : 空间数据集控制器
 */
@RestController
@RequestMapping("/spatial-dataset")
public class SpatialDatasetController {

    @Autowired
    private SpatialDatasetService spatialDatasetService;

    @Value("${file.upload.tempDir}")
    private String tempDir;

    /**
     * 上传数据集
     * @param userId 用户ID
     * @param file 文件
     * @param dataType 数据类型
     * @param category 数据类别
     * @param description 描述
     * @return 上传结果
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadDataset(
            @RequestParam("userId") Long userId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("dataType") String dataType,
            @RequestParam("category") String category,
            @RequestParam(value = "description", required = false) String description) {
        
        try {
            // 创建元数据
            Map<String, Object> metadata = new HashMap<>();
            if (description != null && !description.isEmpty()) {
                metadata.put("description", description);
            }
            
            // 上传数据集
            SpatialDataset dataset = spatialDatasetService.uploadDataset(userId, file, dataType, category, metadata);
            return ResponseEntity.ok(dataset);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("上传失败: " + e.getMessage());
        }
    }

    /**
     * 获取数据集详情
     * @param id 数据集ID
     * @return 数据集详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getDatasetById(@PathVariable Long id) {
        try {
            SpatialDataset dataset = spatialDatasetService.getDatasetById(id);
            return ResponseEntity.ok(dataset);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("数据集不存在: " + e.getMessage());
        }
    }

    /**
     * 获取用户数据集列表
     * @param userId 用户ID
     * @return 数据集列表
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getDatasetsByUserId(@PathVariable Long userId) {
        List<SpatialDataset> datasets = spatialDatasetService.getDatasetsByUserId(userId);
        return ResponseEntity.ok(datasets);
    }

    /**
     * 按类型获取用户数据集
     * @param userId 用户ID
     * @param dataType 数据类型
     * @return 数据集列表
     */
    @GetMapping("/user/{userId}/type/{dataType}")
    public ResponseEntity<?> getDatasetsByType(
            @PathVariable Long userId,
            @PathVariable String dataType) {
        List<SpatialDataset> datasets = spatialDatasetService.getDatasetsByUserIdAndType(userId, dataType);
        return ResponseEntity.ok(datasets);
    }

    /**
     * 按类别获取用户数据集
     * @param userId 用户ID
     * @param category 数据类别
     * @return 数据集列表
     */
    @GetMapping("/user/{userId}/category/{category}")
    public ResponseEntity<?> getDatasetsByCategory(
            @PathVariable Long userId,
            @PathVariable String category) {
        List<SpatialDataset> datasets = spatialDatasetService.getDatasetsByUserIdAndCategory(userId, category);
        return ResponseEntity.ok(datasets);
    }

    /**
     * 读取数据集内容
     * @param id 数据集ID
     * @param limit 限制行数
     * @return 数据集内容
     */
    @GetMapping("/{id}/content")
    public ResponseEntity<?> readDatasetContent(
            @PathVariable Long id,
            @RequestParam(value = "limit", defaultValue = "100") int limit) {
        try {
            List<String> content = spatialDatasetService.readDatasetContent(id, limit);
            return ResponseEntity.ok(content);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("读取失败: " + e.getMessage());
        }
    }

    /**
     * 下载数据集
     * @param id 数据集ID
     * @return 文件下载
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<?> downloadDataset(@PathVariable Long id) {
        try {
            // 获取数据集信息
            SpatialDataset dataset = spatialDatasetService.getDatasetById(id);
            
            // 创建临时目录
            Path tempPath = Paths.get(tempDir);
            if (!Files.exists(tempPath)) {
                Files.createDirectories(tempPath);
            }
            
            // 下载到临时文件
            String localFilePath = tempDir + File.separator + dataset.getName();
            spatialDatasetService.downloadDataset(id, localFilePath);
            
            // 读取文件内容
            byte[] data = Files.readAllBytes(Paths.get(localFilePath));
            
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", dataset.getName());
            
            // 删除临时文件
            Files.deleteIfExists(Paths.get(localFilePath));
            
            return new ResponseEntity<>(data, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("下载失败: " + e.getMessage());
        }
    }

    /**
     * 删除数据集
     * @param id 数据集ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDataset(@PathVariable Long id) {
        try {
            boolean deleted = spatialDatasetService.deleteDataset(id);
            if (deleted) {
                return ResponseEntity.ok("数据集删除成功");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("数据集删除失败");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("删除失败: " + e.getMessage());
        }
    }
} 
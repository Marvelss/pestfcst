package cn.edu.hdu.pestfcst.modelbuildingservice.controller;/*
 * @Author : Vagrant
 * @Time: 2025-03-26 10:26
 * @File : ModelBuildingDataSetController.java
 * @Description :
 */

import cn.edu.hdu.pestfcst.modelbuildingservice.bean.ModelingRecord;
import cn.edu.hdu.pestfcst.modelbuildingservice.service.ModelBuildingDataSetService;
import cn.edu.hdu.pestfcst.modelbuildingservice.service.impl.ModelBuildingDataSetServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/model-building-dataset")
public class ModelBuildingDataSetController {
    @Autowired
    private ModelBuildingDataSetService modelBuildingDataSetService;

    @PostMapping("/model-train")
    public ResponseEntity<String> modelTrain(@RequestBody Map<String, Object> request) {
        System.out.println("----1----" + ModelBuildingDataSetController.class.getName() +
                "----/model-building-dataset/model-train----接收API接口请求");

        // 提取 userId 参数
        Object userIdObj = request.get("userId");
        if (userIdObj == null) {
            return ResponseEntity.badRequest().body("参数错误：缺少 userId");
        }
        Long userId;
        try {
            userId = Long.parseLong(userIdObj.toString());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("参数错误：userId 格式非法");
        }

        // 调用建模服务
        modelBuildingDataSetService.buildModel(userId);

        return ResponseEntity.ok("模型训练任务提交成功！");
    }

    /**
     * 单文件上传接口
     * @param file 上传的文件
     * @return 上传结果
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        System.out.println("----文件上传----" + ModelBuildingDataSetController.class.getName() +
                "----/model-building-dataset/upload----接收文件上传请求");
        
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "文件为空，请选择要上传的文件"
                ));
            }

            // 调用服务保存文件
            String savedPath = modelBuildingDataSetService.saveUploadedFile(file);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "文件上传成功",
                "fileName", file.getOriginalFilename(),
                "fileSize", file.getSize(),
                "savedPath", savedPath,
                "uploadTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ));
            
        } catch (Exception e) {
            System.err.println("文件上传失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "文件上传失败: " + e.getMessage()
            ));
        }
    }

    /**
     * 多文件上传接口
     * @param files 上传的文件数组
     * @return 上传结果
     */
    @PostMapping("/upload-multiple")
    public ResponseEntity<Map<String, Object>> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        System.out.println("----多文件上传----" + ModelBuildingDataSetController.class.getName() +
                "----/model-building-dataset/upload-multiple----接收多文件上传请求");
        
        try {
            if (files == null || files.length == 0) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "没有选择文件"
                ));
            }

            List<Map<String, Object>> uploadResults = new java.util.ArrayList<>();
            int successCount = 0;
            int failCount = 0;

            for (MultipartFile file : files) {
                try {
                    if (!file.isEmpty()) {
                        String savedPath = modelBuildingDataSetService.saveUploadedFile(file);
                        uploadResults.add(Map.of(
                            "fileName", file.getOriginalFilename(),
                            "fileSize", file.getSize(),
                            "savedPath", savedPath,
                            "status", "success"
                        ));
                        successCount++;
                    }
                } catch (Exception e) {
                    uploadResults.add(Map.of(
                        "fileName", file.getOriginalFilename(),
                        "status", "failed",
                        "error", e.getMessage()
                    ));
                    failCount++;
                }
            }

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", String.format("批量上传完成，成功: %d, 失败: %d", successCount, failCount),
                "totalFiles", files.length,
                "successCount", successCount,
                "failCount", failCount,
                "uploadResults", uploadResults,
                "uploadTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ));
            
        } catch (Exception e) {
            System.err.println("多文件上传失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "多文件上传失败: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/send-message")
    public String sendMessage() {
        String message = "Hello, Kafka!";
//        kafkaProducerService.sendMessage("test",  new ModelBuildingDataSet());
        return "Message sent to Kafka topic: test";
    }
}

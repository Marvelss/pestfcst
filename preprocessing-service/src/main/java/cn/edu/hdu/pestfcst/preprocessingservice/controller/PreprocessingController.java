package cn.edu.hdu.pestfcst.preprocessingservice.controller;

import cn.edu.hdu.pestfcst.preprocessingservice.bean.PreprocessingResult;
import cn.edu.hdu.pestfcst.preprocessingservice.bean.PreprocessingTask;
import cn.edu.hdu.pestfcst.preprocessingservice.service.PreprocessingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @Author : Claude
 * @Time: 2025-06-25
 * @File : PreprocessingController.java
 * @Description : 预处理服务控制器
 */
@RestController
@RequestMapping("/preprocessing")
public class PreprocessingController {

    @Autowired
    private PreprocessingService preprocessingService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 创建预处理任务
     * @param requestBody 请求参数
     * @return 创建结果
     */
    @PostMapping("/tasks")
    public ResponseEntity<?> createTask(@RequestBody Map<String, Object> requestBody) {
        try {
            Long userId = Long.parseLong(requestBody.get("userId").toString());
            Long datasetId = Long.parseLong(requestBody.get("datasetId").toString());
            String taskType = requestBody.get("taskType").toString();
            String inputPath = requestBody.get("inputPath").toString();
            
            @SuppressWarnings("unchecked")
            Map<String, Object> taskParams = (Map<String, Object>) requestBody.get("taskParams");
            
            PreprocessingTask task = preprocessingService.createTask(userId, datasetId, taskType, taskParams, inputPath);
            return ResponseEntity.ok(task);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("创建任务失败: " + e.getMessage());
        }
    }

    /**
     * 获取任务详情
     * @param taskId 任务ID
     * @return 任务详情
     */
    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<?> getTaskById(@PathVariable Long taskId) {
        try {
            PreprocessingTask task = preprocessingService.getTaskById(taskId);
            return ResponseEntity.ok(task);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("任务不存在: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("获取任务失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户任务列表
     * @param userId 用户ID
     * @return 任务列表
     */
    @GetMapping("/tasks/user/{userId}")
    public ResponseEntity<?> getTasksByUserId(@PathVariable Long userId) {
        try {
            List<PreprocessingTask> tasks = preprocessingService.getTasksByUserId(userId);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("获取任务列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取数据集任务列表
     * @param datasetId 数据集ID
     * @return 任务列表
     */
    @GetMapping("/tasks/dataset/{datasetId}")
    public ResponseEntity<?> getTasksByDatasetId(@PathVariable Long datasetId) {
        try {
            List<PreprocessingTask> tasks = preprocessingService.getTasksByDatasetId(datasetId);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("获取任务列表失败: " + e.getMessage());
        }
    }

    /**
     * 执行预处理任务
     * @param taskId 任务ID
     * @return 处理结果
     */
    @PostMapping("/tasks/{taskId}/execute")
    public ResponseEntity<?> executeTask(@PathVariable Long taskId) {
        try {
            PreprocessingResult result = preprocessingService.executeTask(taskId);
            return ResponseEntity.ok(result);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("任务不存在: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("执行任务失败: " + e.getMessage());
        }
    }

    /**
     * 空间点提取处理
     * @param taskId 任务ID
     * @return 处理结果
     */
    @PostMapping("/tasks/{taskId}/extract-spatial-points")
    public ResponseEntity<?> extractSpatialPoints(@PathVariable Long taskId) {
        try {
            PreprocessingResult result = preprocessingService.extractSpatialPoints(taskId);
            return ResponseEntity.ok(result);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("任务不存在: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("点提取失败: " + e.getMessage());
        }
    }

    /**
     * 数据清洗处理
     * @param taskId 任务ID
     * @return 处理结果
     */
    @PostMapping("/tasks/{taskId}/clean-data")
    public ResponseEntity<?> cleanData(@PathVariable Long taskId) {
        try {
            PreprocessingResult result = preprocessingService.cleanData(taskId);
            return ResponseEntity.ok(result);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("任务不存在: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("数据清洗失败: " + e.getMessage());
        }
    }

    /**
     * 数据标准化处理
     * @param taskId 任务ID
     * @return 处理结果
     */
    @PostMapping("/tasks/{taskId}/normalize-data")
    public ResponseEntity<?> normalizeData(@PathVariable Long taskId) {
        try {
            PreprocessingResult result = preprocessingService.normalizeData(taskId);
            return ResponseEntity.ok(result);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("任务不存在: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("数据标准化失败: " + e.getMessage());
        }
    }

    /**
     * 获取处理结果
     * @param resultId 结果ID
     * @return 结果详情
     */
    @GetMapping("/results/{resultId}")
    public ResponseEntity<?> getResultById(@PathVariable Long resultId) {
        try {
            PreprocessingResult result = preprocessingService.getResultById(resultId);
            return ResponseEntity.ok(result);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("结果不存在: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("获取结果失败: " + e.getMessage());
        }
    }

    /**
     * 获取任务结果列表
     * @param taskId 任务ID
     * @return 结果列表
     */
    @GetMapping("/results/task/{taskId}")
    public ResponseEntity<?> getResultsByTaskId(@PathVariable Long taskId) {
        try {
            List<PreprocessingResult> results = preprocessingService.getResultsByTaskId(taskId);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("获取结果列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户结果列表
     * @param userId 用户ID
     * @return 结果列表
     */
    @GetMapping("/results/user/{userId}")
    public ResponseEntity<?> getResultsByUserId(@PathVariable Long userId) {
        try {
            List<PreprocessingResult> results = preprocessingService.getResultsByUserId(userId);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("获取结果列表失败: " + e.getMessage());
        }
    }
} 
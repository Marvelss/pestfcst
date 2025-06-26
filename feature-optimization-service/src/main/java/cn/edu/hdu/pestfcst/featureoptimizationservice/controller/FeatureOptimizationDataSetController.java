package cn.edu.hdu.pestfcst.featureoptimizationservice.controller;/*
 * @Author : Vagrant
 * @Time: 2025-03-19 10:29
 * @File : FeatureOptimizationDataSetController.java
 * @Description :
 */

import cn.edu.hdu.pestfcst.featureoptimizationservice.bean.ApiResponse;
import cn.edu.hdu.pestfcst.featureoptimizationservice.bean.FeatureOptimizationDataSet;
import cn.edu.hdu.pestfcst.featureoptimizationservice.service.FeatureOptimizationDataSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/feature-optimization")
@CrossOrigin(origins = "*")
public class FeatureOptimizationDataSetController {
    
    @Autowired
    private FeatureOptimizationDataSetService featureOptimizationDataSetService;
    
    private final String UPLOAD_DIR = "./uploads/";
    
    /**
     * 根据ID获取特征优化数据集
     */
    @GetMapping("/{id}")
    public ApiResponse<FeatureOptimizationDataSet> getFeatureOptimizationDataSetById(@PathVariable long id) {
        try {
            FeatureOptimizationDataSet dataSet = featureOptimizationDataSetService.getFeatureOptimizationDataSetByID(id);
            return ApiResponse.success(dataSet);
        } catch (Exception e) {
            return ApiResponse.error(404, "找不到指定ID的数据集: " + e.getMessage());
        }
    }
    
    /**
     * 根据用户ID获取特征优化数据集列表
     */
    @GetMapping("/user/{userId}")
    public ApiResponse<List<FeatureOptimizationDataSet>> getFeatureOptimizationDataSetsByUserId(@PathVariable long userId) {
        try {
            List<FeatureOptimizationDataSet> dataSets = featureOptimizationDataSetService.getFeatureOptimizationDataSetsByUserId(userId);
            return ApiResponse.success(dataSets);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取用户数据集失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建新的特征优化数据集
     */
    @PostMapping
    public ApiResponse<FeatureOptimizationDataSet> createFeatureOptimizationDataSet(@RequestBody FeatureOptimizationDataSet dataSet) {
        try {
            FeatureOptimizationDataSet created = featureOptimizationDataSetService.createFeatureOptimizationDataSet(dataSet);
            return ApiResponse.success("创建数据集成功", created);
        } catch (Exception e) {
            return ApiResponse.error(500, "创建数据集失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新特征优化数据集
     */
    @PutMapping("/{id}")
    public ApiResponse<FeatureOptimizationDataSet> updateFeatureOptimizationDataSet(@PathVariable long id, 
                                                                                  @RequestBody FeatureOptimizationDataSet dataSet) {
        try {
            FeatureOptimizationDataSet existing = featureOptimizationDataSetService.getFeatureOptimizationDataSetByID(id);
            dataSet.setId(id);
            FeatureOptimizationDataSet updated = featureOptimizationDataSetService.updateFeatureOptimizationDataSet(dataSet);
            return ApiResponse.success("更新数据集成功", updated);
        } catch (Exception e) {
            return ApiResponse.error(500, "更新数据集失败: " + e.getMessage());
        }
    }
    
    /**
     * 上传数据文件
     */
    @PostMapping("/upload")
    public ApiResponse<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // 创建上传目录
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // 生成唯一的文件名
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR + fileName);
            
            // 保存文件
            Files.copy(file.getInputStream(), filePath);
            
            return ApiResponse.success("文件上传成功", filePath.toString());
        } catch (IOException e) {
            return ApiResponse.error(500, "文件上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 执行T检验特征优化
     */
    @PostMapping("/t-test")
    public ApiResponse<Map<String, Object>> performTTest(@RequestParam("targetVariable") String targetVariable,
                                                        @RequestParam("comparedVariables") String comparedVariablesStr,
                                                        @RequestParam("threshold") double threshold,
                                                        @RequestParam("filePath") String filePath) {
        try {
            List<String> comparedVariables = Arrays.asList(comparedVariablesStr.split(","));
            Map<String, Object> result = featureOptimizationDataSetService.performTTest(
                    targetVariable, comparedVariables, threshold, filePath);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error(500, "执行T检验失败: " + e.getMessage());
        }
    }
    
    /**
     * 执行ReliefF特征优化
     */
    @PostMapping("/relief-f")
    public ApiResponse<Map<String, Object>> performReliefF(@RequestParam("targetVariable") String targetVariable,
                                                         @RequestParam("featureVariables") String featureVariablesStr,
                                                         @RequestParam("selectionMethod") String selectionMethod,
                                                         @RequestParam("selectionParam") String selectionParam,
                                                         @RequestParam("filePath") String filePath) {
        try {
            List<String> featureVariables = Arrays.asList(featureVariablesStr.split(","));
            Map<String, Object> result = featureOptimizationDataSetService.performReliefF(
                    targetVariable, featureVariables, selectionMethod, selectionParam, filePath);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error(500, "执行ReliefF失败: " + e.getMessage());
        }
    }
    
    /**
     * 执行Pearson相关性特征优化
     */
    @PostMapping("/pearson")
    public ApiResponse<Map<String, Object>> performPearson(@RequestParam("targetVariable") String targetVariable,
                                                         @RequestParam("featureVariables") String featureVariablesStr,
                                                         @RequestParam("threshold") double threshold,
                                                         @RequestParam("filePath") String filePath) {
        try {
            List<String> featureVariables = Arrays.asList(featureVariablesStr.split(","));
            Map<String, Object> result = featureOptimizationDataSetService.performPearson(
                    targetVariable, featureVariables, threshold, filePath);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error(500, "执行Pearson相关性分析失败: " + e.getMessage());
        }
    }
}

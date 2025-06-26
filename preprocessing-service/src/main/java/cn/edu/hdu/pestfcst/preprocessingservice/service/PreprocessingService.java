package cn.edu.hdu.pestfcst.preprocessingservice.service;

import cn.edu.hdu.pestfcst.preprocessingservice.bean.PreprocessingResult;
import cn.edu.hdu.pestfcst.preprocessingservice.bean.PreprocessingTask;

import java.util.List;
import java.util.Map;

/**
 * @Author : Claude
 * @Time: 2025-06-25
 * @File : PreprocessingService.java
 * @Description : 预处理服务接口
 */
public interface PreprocessingService {
    /**
     * 创建预处理任务
     * @param userId 用户ID
     * @param datasetId 数据集ID
     * @param taskType 任务类型
     * @param taskParams 任务参数
     * @param inputPath 输入文件路径
     * @return 创建的任务
     */
    PreprocessingTask createTask(Long userId, Long datasetId, String taskType, 
                               Map<String, Object> taskParams, String inputPath);
    
    /**
     * 获取任务详情
     * @param taskId 任务ID
     * @return 任务对象
     */
    PreprocessingTask getTaskById(Long taskId);
    
    /**
     * 获取用户的预处理任务列表
     * @param userId 用户ID
     * @return 任务列表
     */
    List<PreprocessingTask> getTasksByUserId(Long userId);
    
    /**
     * 获取数据集对应的预处理任务列表
     * @param datasetId 数据集ID
     * @return 任务列表
     */
    List<PreprocessingTask> getTasksByDatasetId(Long datasetId);
    
    /**
     * 执行预处理任务
     * @param taskId 任务ID
     * @return 处理结果
     */
    PreprocessingResult executeTask(Long taskId);
    
    /**
     * 获取预处理结果
     * @param resultId 结果ID
     * @return 结果对象
     */
    PreprocessingResult getResultById(Long resultId);
    
    /**
     * 获取任务对应的结果列表
     * @param taskId 任务ID
     * @return 结果列表
     */
    List<PreprocessingResult> getResultsByTaskId(Long taskId);
    
    /**
     * 获取用户的预处理结果列表
     * @param userId 用户ID
     * @return 结果列表
     */
    List<PreprocessingResult> getResultsByUserId(Long userId);
    
    /**
     * 空间点提取处理
     * @param taskId 任务ID
     * @return 处理结果
     */
    PreprocessingResult extractSpatialPoints(Long taskId);
    
    /**
     * 数据清洗处理
     * @param taskId 任务ID
     * @return 处理结果
     */
    PreprocessingResult cleanData(Long taskId);
    
    /**
     * 数据标准化处理
     * @param taskId 任务ID
     * @return 处理结果
     */
    PreprocessingResult normalizeData(Long taskId);
} 
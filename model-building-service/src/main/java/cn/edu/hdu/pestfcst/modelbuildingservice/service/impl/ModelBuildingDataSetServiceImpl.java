package cn.edu.hdu.pestfcst.modelbuildingservice.service.impl;/*
 * @Author : Vagrant
 * @Time: 2025-03-26 10:26
 * @File : ModelBuildingDataSetServiceImpl.java
 * @Description :
 */

import cn.edu.hdu.pestfcst.modelbuildingservice.bean.ModelingRecord;
import cn.edu.hdu.pestfcst.modelbuildingservice.dao.ModelingRecordRepository;
import cn.edu.hdu.pestfcst.modelbuildingservice.processor.ModelTrainingProcessor;
import cn.edu.hdu.pestfcst.modelbuildingservice.service.ModelBuildingDataSetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
public class ModelBuildingDataSetServiceImpl implements ModelBuildingDataSetService {

    @Autowired
    private ModelingRecordRepository modelBuildingDataRepository;
    @Autowired
    private ModelTrainingProcessor modelTrainingProcessor;

    @Override
    public void buildModel(Long userId) {
        // 根据用户ID获取任务清单
        System.out.println("userId:" + userId);
        List<ModelingRecord> optionalTaskList = modelBuildingDataRepository.findByUserId(userId);
        if (optionalTaskList.isEmpty()) {
            System.out.println("没有找到用户ID为 " + userId + " 的任务清单");
            return;
        }
        // 打印任务清单内容，用于调试
        List<ModelingRecord> tasksToProcess = new ArrayList<>();

        for (ModelingRecord task : optionalTaskList) {
            System.out.println("任务清单: " + task);
            // 检查任务状态，3或4表示已完成或失败，跳过不处理
            if (task.getModelStatus() != 3 && task.getModelStatus() != 4) tasksToProcess.add(task);
            else System.out.println("跳过已处理任务，ID: " + task.getModelId() + "，状态: " + task.getModelStatus());
        }
        // 如果有需要处理的任务，直接处理
        if (!tasksToProcess.isEmpty()) {
            System.out.println("----2----" + ModelBuildingDataSetServiceImpl.class.getName() +
                    "----buildModel()----开始处理建模任务");
            // 直接处理任务
            for (ModelingRecord task : tasksToProcess) {
                try {
                    String modelingInfo = new ObjectMapper().writeValueAsString(task);
                    executeModelTraining(modelingInfo);
                } catch (IOException e) {
                    System.out.println("处理任务失败: " + e.getMessage());
                }
            }
        } else {
            System.out.println("没有需要处理的新建模任务");
        }
    }

    public void executeModelTraining(String modelingInfo) throws IOException {
        System.out.println("----4----" + ModelBuildingDataSetServiceImpl.class.getName() +
                "----executeModelTraining()----开始模型训练");
//        反序列化建模信息
        ModelingRecord tempModelingRecord = parseModelingRecord(modelingInfo);
//        ②从数据库获取该任务，更新任务状态为运行中
        tempModelingRecord.setModelStatus(2); // 假设状态2表示运行中
        modelBuildingDataRepository.save(tempModelingRecord);
        // 执行模型训练
        System.out.println("************开始模型训练************");
        System.out.println(tempModelingRecord);
        ModelingRecord trainResult = new ModelingRecord();
        try {
            //  ③传入建模参数：1.输入数据2.模型方法3.方法参数4.特征5.标签6.数据集分配比例7.精度指标
            String modelID = tempModelingRecord.getModelId(); // 获取模型ID
            String dataPath = tempModelingRecord.getModelData(); // 输入数据路径
            String modelName = tempModelingRecord.getModelMethodName(); // 模型名称
            JsonNode modelMethodParam = tempModelingRecord.getModelMethodParam(); // 模型参数
            String featureCols = tempModelingRecord.getFeatures(); // 特征列（JSON字符串格式）
            String labelCol = tempModelingRecord.getLabel(); // 标签列
            String trainTestSplitRatio = tempModelingRecord.getDatasetSplitRatio(); // 数据集分配比例
            JsonNode metricName = tempModelingRecord.getEvaluationMetrics(); // 精度指标

//            调用建模方法
            trainResult = modelTrainingProcessor.trainModel(modelID,
                    dataPath, modelName, modelMethodParam,
                    featureCols, labelCol,
                    trainTestSplitRatio,
                    metricName);

            // 直接保存结果
            saveBuildResult(new ObjectMapper().writeValueAsString(trainResult));

        } catch (Exception e) {
            System.out.println("模型训练失败: " + e.getMessage());
            // 更新失败状态
            tempModelingRecord.setModelStatus(4); // 假设状态4表示失败
            modelBuildingDataRepository.save(tempModelingRecord);
        }
    }

    @Override
    public void saveBuildResult(String result) throws IOException {
        //  从数据库获取并更新模型记录
        //  获取模型记录结果
        ModelingRecord tempModelingRecord = parseModelingRecord(result);
        System.out.println("----8----" + ModelBuildingDataSetServiceImpl.class.getName() +
                "----saveBuildResult()----保存建模结果" + "编号:" + tempModelingRecord.getModelId());
        // ⑤获取现有的模型记录
        ModelingRecord existingRecord = modelBuildingDataRepository.findById(tempModelingRecord.getModelId())
                .orElseThrow(() -> new RuntimeException("ModelingRecord not found for ID: " + tempModelingRecord.getModelId()));
        // ⑥更新训练结果
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(result);
        JsonNode evaluationMetricsNode = rootNode.path("evaluationMetrics");
        if (evaluationMetricsNode.isObject()) {
            existingRecord.setEvaluationMetrics(evaluationMetricsNode);
        }
//        更新内容：1.任务状态2.精度3.模型结构4.模型结果
        existingRecord.setModelStatus(tempModelingRecord.getModelStatus());
        existingRecord.setModelStructure(tempModelingRecord.getModelStructure());
        existingRecord.setTrainingResult(tempModelingRecord.getTrainingResult());
        // 保存更新后的记录
        modelBuildingDataRepository.save(existingRecord);
    }

    public static ModelingRecord parseModelingRecord(String jsonString) throws IOException {
        if (jsonString == null || jsonString.isEmpty()) {
            throw new IllegalArgumentException("JSON string is null or empty");
        }
        return new ObjectMapper().readValue(jsonString, ModelingRecord.class);
    }
}

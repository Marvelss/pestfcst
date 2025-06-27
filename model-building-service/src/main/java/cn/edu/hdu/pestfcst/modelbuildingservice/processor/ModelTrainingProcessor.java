package cn.edu.hdu.pestfcst.modelbuildingservice.processor;/*
 * @Author : Vagrant
 * @Time: 2025-03-31 20:50
 * @File : ModelTrainingProcessor.java
 * @Description : 模型训练
 */

import cn.edu.hdu.pestfcst.modelbuildingservice.bean.ModelingRecord;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;


@Service
public class ModelTrainingProcessor {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public ModelingRecord trainModel(String modelID, String dataPath, String modelName, JsonNode modelParams,
                                     String featuresJson, String labelCol,
                                     String trainTestSplitRatio,
                                     JsonNode metricNames) {

        ModelingRecord result = new ModelingRecord();
        try {
            // 模拟训练过程
            System.out.println("开始模拟模型训练过程...");
            System.out.println("模型ID: " + modelID);
            System.out.println("数据路径: " + dataPath);
            System.out.println("模型名称: " + modelName);
            System.out.println("标签列: " + labelCol);
            System.out.println("训练测试比例: " + trainTestSplitRatio);
            
            // 模拟特征解析
            String[] featureCols;
            try {
                featureCols = objectMapper.readValue(featuresJson, String[].class);
                System.out.println("特征列: " + Arrays.toString(featureCols));
            } catch (Exception e) {
                throw new IllegalArgumentException("无法解析特征JSON字符串: " + featuresJson, e);
            }
            
            // 模拟训练过程
            Thread.sleep(2000); // 模拟训练时间
            
            // 模拟评估指标
            Map<String, Double> metricResults = new HashMap<>();
            for (JsonNode metricNode : metricNames) {
                String metric = metricNode.asText();
                // 生成0.7到0.95之间的随机精度
                double score = 0.7 + Math.random() * 0.25;
                metricResults.put(metric, Math.round(score * 1000.0) / 1000.0);
            }
            
            // 模拟预测结果和模型保存路径
            String predictionOutputPath = "./data/" + modelID + "_predictions.csv";
            String modelSavePath = "./data/" + modelID + "_model";
            
            // 更新训练结果
            result.setModelId(modelID);
            ObjectNode metricsNode = objectMapper.createObjectNode();
            for (Map.Entry<String, Double> entry : metricResults.entrySet()) {
                metricsNode.put(entry.getKey(), entry.getValue());
            }
            result.setEvaluationMetrics(metricsNode);
            result.setTrainingResult(predictionOutputPath);
            result.setModelStructure(modelSavePath);
            result.setModelStatus(3); // 成功状态

            System.out.println("模型训练完成，评估指标: " + metricsNode);

        } catch (Exception e) {
            result.setModelId(modelID);
            result.setModelStatus(4); // 失败状态
            e.printStackTrace();
        }

        return result;
    }

    // 解析训练与测试数据集划分
    private double[] parseTrainTestSplitRatio(String ratio) {
        switch (ratio) {
            case "8:2":
                return new double[]{0.8, 0.2};
            case "7:3":
                return new double[]{0.7, 0.3};
            case "6:4":
                return new double[]{0.6, 0.4};
            default:
                throw new IllegalArgumentException("仅支持以下比例: 8:2, 7:3, 6:4，当前传入：" + ratio);
        }
    }
}
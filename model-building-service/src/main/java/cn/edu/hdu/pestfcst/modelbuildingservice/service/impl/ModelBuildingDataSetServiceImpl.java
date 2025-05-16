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
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.kafka.core.KafkaTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


@Service
public class ModelBuildingDataSetServiceImpl implements ModelBuildingDataSetService {

    @Autowired
    private ModelingRecordRepository modelBuildingDataRepository;
    @Autowired
    private ModelTrainingProcessor modelTrainingProcessor;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;


//    @Autowired
//    private RedisTemplate<String, String> redisTemplate;

//    @Value("${redis.key.model.precision}")
//    private String precisionKeyPattern;


    @Override
    public void buildModel(String userID) {

//        构建用户ID获取任务清单
//        modelingInfo.setModelStatus(1); // 设置模型状态为等待中
//        ModelingRecord savedRecord = modelBuildingDataRepository.save(modelingInfo);
//        System.out.println("建模任务已保存到数据库: " + savedRecord);
        // 根据用户ID获取任务清单
        Optional<ModelingRecord> optionalTaskList = modelBuildingDataRepository.findById(userID);
//        List<ModelingRecord> taskList = optionalTaskList.orElseThrow(() -> new EXCEP("No modeling record found for user ID: " + userID));
        //多个任务批量发送kafka消息？

        System.out.println("----2----" + ModelBuildingDataSetServiceImpl.class.getName() +
                "----buildModel()----Service发送kafka建模任务");
        // 发送Kafka消息
        sendMessage("model-building-tasks", optionalTaskList);

    }

    public void   executeModelTraining(String modelingInfo) throws IOException {
        System.out.println("----4----" + ModelBuildingDataSetServiceImpl.class.getName() +
                "----executeModelTraining()----Service调用Processor开始模型训练");
//        反序列化建模信息
        ModelingRecord tempModelingRecord = parseModelingRecord(modelingInfo);
//        ②从数据库获取该任务，更新任务状态为运行中
//        ModelingRecord record = modelBuildingDataRepository.findById(tempModelingRecord.getModelId())
//                .orElseThrow(() -> new RuntimeException("ModelingRecord not found"));
        tempModelingRecord.setModelStatus(2); // 假设状态2表示运行中
        modelBuildingDataRepository.save(tempModelingRecord);
        // 执行模型训练
        System.out.println("************开始模型训练************");
        System.out.println(tempModelingRecord);

        JSONObject result = new JSONObject();

        try {
            //  ③传入建模参数：1.输入数据2.模型方法3.方法参数4.特征5.标签6.数据集分配比例7.精度指标
            String dataPath = tempModelingRecord.getModelData(); // 输入数据路径
            String modelName = tempModelingRecord.getModelMethod(); // 模型名称
            String modelMethodParam = tempModelingRecord.getModelMethodParam(); // 模型参数
            String featureCols = tempModelingRecord.getFeatures(); // 特征列
            // 假设 featureCols 是一个以逗号分隔的字符串
            List<String> featureArray = Arrays.asList(featureCols.split(","));
            String labelCol = tempModelingRecord.getLabel(); // 标签列
            String trainTestSplitRatio = tempModelingRecord.getDatasetSplitRatio(); // 数据集分配比例
            String metricName = tempModelingRecord.getEvaluationMetrics(); // 精度指标


            JSONObject trainResult = modelTrainingProcessor.trainModel(
                    dataPath, modelName, modelMethodParam,
                    featureArray, labelCol,
                    trainTestSplitRatio,
                    metricName);

            result.put("msg", "模型训练成功！");
            result.put("训练结果", trainResult);
            result.put("success", true);

        } catch (Exception e) {
            result.put("msg", "模型训练失败: " + e.getMessage());
            result.put("success", false);
            // log.error("模型训练异常", e); // 可选日志
        }

        // ④发送建模结果到 Kafka
        System.out.println("----6----" + ModelBuildingDataSetServiceImpl.class.getName() +
                "----executeModelTraining()----Service发送kafka建模结果");
        sendMessage("model-building-results", result.toJSONString());
    }

    @Override
    public void saveBuildResult(String result) throws IOException {
        //  从数据库获取并更新模型记录
        ModelingRecord tempModelingRecord = parseModelingRecord(result);
        System.out.println("----8----" + ModelBuildingDataSetServiceImpl.class.getName() +
                "----saveBuildResult()----Service保存建模结果" + "编号:" + tempModelingRecord.getModelId());
        // ⑤获取现有的模型记录
        ModelingRecord existingRecord = modelBuildingDataRepository.findById(tempModelingRecord.getModelId())
                .orElseThrow(() -> new RuntimeException("ModelingRecord not found for ID: " + tempModelingRecord.getModelId()));

        // ⑥更新信息：1.任务状态2.精度3.模型结构4.模型结果
        existingRecord.setModelStatus(2);

        // 保存更新后的记录
        modelBuildingDataRepository.save(existingRecord);

//        // 缓存精度到Redis（防雪崩设计）
//        String precisionKey = precisionKeyPattern
//                .replace("{user_id}", modelRepo.findById(modelId).get().getUserId().toString())
//                .replace("{model_name}", modelType);
//        redisTemplate.opsForValue().set(precisionKey,
//                JSON.toJSONString(Map.of(
//                        "accuracy", accuracy,
//                        "timestamp", System.currentTimeMillis()
//                )),
//                24 + new Random().nextInt(12), // 随机过期24-36小时
//                TimeUnit.HOURS);
    }


    public void sendMessage(String topic, Object message) {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString;
        try {
            jsonString = objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        kafkaTemplate.send(topic, jsonString).addCallback(
                success -> {
                    if (success != null) {
                        System.out.println("Message sent successfully: " + jsonString);
                    }
                },
                failure -> {
                    System.err.println("Failed to send message: " + failure.getMessage());
                }
        );
    }

    public static ModelingRecord parseModelingRecord(String jsonString) throws IOException {
        if (jsonString == null || jsonString.isEmpty()) {
            throw new IllegalArgumentException("JSON string is null or empty");
        }
        return new ObjectMapper().readValue(jsonString, ModelingRecord.class);
    }


//    // 模型缓存方法
//    @Cacheable(value = "modelCache", key = "#modelId")
//    public byte[] getModelBytes(Long modelId) {
//        Query query = new Query(Criteria.where("modelId").is(modelId));
//        MLModel model = mongoTemplate.findOne(query, MLModel.class);
//        return model != null ? model.getModelBytes() : null;
//    }

//    @Autowired
//    private ModelBuildingDataRepository ModelBuildingDataRepository;
//
//    @Override
//    public ModelBuildingDataSet getModelBuildingDataSetByID(long id) {
//        return ModelBuildingDataRepository.findById(id).orElseThrow(RuntimeException::new);
//    }
}

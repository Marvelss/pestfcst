package cn.edu.hdu.pestfcst.modelbuildingservice.service.impl;/*
 * @Author : Vagrant
 * @Time: 2025-03-26 10:26
 * @File : ModelBuildingDataSetServiceImpl.java
 * @Description :
 */

import cn.edu.hdu.pestfcst.modelbuildingservice.bean.ModelingRecord;
import cn.edu.hdu.pestfcst.modelbuildingservice.controller.ModelBuildingDataSetController;
import cn.edu.hdu.pestfcst.modelbuildingservice.dao.ModelBuildingDataRepository;
import cn.edu.hdu.pestfcst.modelbuildingservice.processor.ModelTrainingProcessor;
import cn.edu.hdu.pestfcst.modelbuildingservice.service.ModelBuildingDataSetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ModelBuildingDataSetServiceImpl implements ModelBuildingDataSetService {

    @Autowired
    private ModelBuildingDataRepository modelBuildingDataRepository;
    @Autowired
    private ModelTrainingProcessor modelTrainingProcessor;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

//    @Autowired
//    private RedisTemplate<String, String> redisTemplate;

//    @Value("${redis.key.model.precision}")
//    private String precisionKeyPattern;

//
//    @Override
//    public List<ModelBuildingDataSet> getAllModelBuildingDataSets() {
//        return modelBuildingDataRepository.findAll();
//    }


//    @Override
//    public ModelBuildingDataSet getModelBuildingDataSetByID(String id) {
//        return null;
////        return modelBuildingDataRepository.findById(id)
////                .orElseThrow(() -> new RuntimeException("Service-ModelBuildingDataSet not found with ID: " + id));
//
//    }
//

    @Override
    public void buildModel(ModelingRecord modelingInfo) {
//        发送建模任务


        // 创建模型记录（MySQL）
//        ModelingRecord record = new ModelingRecord();
//        record.setUserId(userId);
//        record.setModelMethod(modelMethod);
//        record.setModelStatus(1);
//        modelBuildingDataRepository.save(record);

//        获取模型建模任务（MySQL）

        String modelId = modelingInfo.getModelId();
        String modelMethod = modelingInfo.getModelMethod();
        Double accuracy = 0.95; // 示例精度
        System.out.println("----2----" + ModelBuildingDataSetServiceImpl.class.getName() +
                "----buildModel()----Service发送kafka建模任务");
        // 发送Kafka消息
        sendMessage("model-building-tasks", modelingInfo);


//        通过Kafka消息获取建模结果保存到数据库
    }

    public void executeModelTraining(String modelingInfo) {
        // 发送建模结果到 Kafka
        System.out.println("----4----" + ModelBuildingDataSetServiceImpl.class.getName() +
                "----executeModelTraining()----Service调用Processor开始模型训练");
        // 执行模型训练
        System.out.println("************开始模型训练************");
        System.out.println(modelingInfo);
        String result = modelTrainingProcessor.trainSVMModel(modelingInfo);
        System.out.println("************训练完成,获得结果************");
        System.out.println(result);
        ModelingRecord temp = new ModelingRecord();
        temp.setModelMethodParam(result);
        // 发送建模结果到 Kafka
        System.out.println("----6----" + ModelBuildingDataSetServiceImpl.class.getName() +
                "----executeModelTraining()----Service发送kafka建模结果");
        sendMessage("model-building-results", temp);
    }

    @Override
    public void saveBuildResult(String result) {
        // 更新模型记录（MySQL）
        Long modelId = Long.valueOf(result);
        System.out.println("----8----" + ModelBuildingDataSetServiceImpl.class.getName() +
                "----saveBuildResult()----Service保存建模结果" + "编号:" + modelId);
//        // 获取现有的模型记录
//        ModelingRecord existingRecord = modelBuildingDataRepository.findById(modelId)
//                .orElseThrow(() -> new RuntimeException("ModelingRecord not found for ID: " + modelId));
        ModelingRecord existingRecord = new ModelingRecord();

        // 更新模型状态和精度
        existingRecord.setModelStatus(2);

        // 保存更新后的记录
//        modelBuildingDataRepository.save(existingRecord);
    }


    public void sendMessage(String topic, ModelingRecord modelingInfo) {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString;
        try {
            jsonString = objectMapper.writeValueAsString(modelingInfo);
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

package cn.edu.hdu.pestfcst.modelbuildingservice.service.impl;/*
 * @Author : Vagrant
 * @Time: 2025-03-26 10:26
 * @File : ModelBuildingDataSetServiceImpl.java
 * @Description :
 */

import cn.edu.hdu.pestfcst.modelbuildingservice.bean.ModelingRecord;
import cn.edu.hdu.pestfcst.modelbuildingservice.dao.ModelBuildingDataSetRepository;
import cn.edu.hdu.pestfcst.modelbuildingservice.dao.ModelingRecordRepository;
import cn.edu.hdu.pestfcst.modelbuildingservice.processor.ModelTrainingProcessor;
import cn.edu.hdu.pestfcst.modelbuildingservice.service.ModelBuildingDataSetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.kafka.core.KafkaTemplate;

import java.io.IOException;


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

//
//    @Override
//    public List<ModelBuildingDataSet> getAllModelBuildingDataSets() {
//        return modelBuildingDataRepository.findAll();
//    }


//    @Override
//    public ModelBuildingDataSet getModelBuildingDataSetByID(String id) {
//        return null;

    /// /        return modelBuildingDataRepository.findById(id)
    /// /                .orElseThrow(() -> new RuntimeException("Service-ModelBuildingDataSet not found with ID: " + id));
//
//    }
//
    @Override
    public void buildModel(ModelingRecord modelingInfo) {
//        发送建模任务
        // 使用 JPA 数据源
        ModelingRecord record = modelBuildingDataRepository.findById("1").orElse(null);
        System.out.println("使用 JPA 数据源");
        System.out.println(record);

        // ①创建模型记录（PostgresSQL）
        modelBuildingDataRepository.save(modelingInfo);

        System.out.println("----2----" + ModelBuildingDataSetServiceImpl.class.getName() +
                "----buildModel()----Service发送kafka建模任务");
        // 发送Kafka消息
        sendMessage("model-building-tasks", modelingInfo);

    }

    public void executeModelTraining(String modelingInfo) throws IOException {

        System.out.println("----4----" + ModelBuildingDataSetServiceImpl.class.getName() +
                "----executeModelTraining()----Service调用Processor开始模型训练");
//        反序列化建模信息
        ModelingRecord tempModelingRecord = parseModelingRecord(modelingInfo);
//        ②从数据库获取该任务，更新任务状态为运行中
        // 执行模型训练
        System.out.println("************开始模型训练************");
        System.out.println(tempModelingRecord);
        //  ③传入建模参数：1.输入数据2.特征3.标签4.数据集分配比例5.参数6.精度指标
        String result = modelTrainingProcessor.trainSVMModel(modelingInfo);
        System.out.println("************训练完成,获得结果************");
        System.out.println(result);

        // ④发送建模结果到 Kafka
        System.out.println("----6----" + ModelBuildingDataSetServiceImpl.class.getName() +
                "----executeModelTraining()----Service发送kafka建模结果");
        sendMessage("model-building-results", tempModelingRecord);
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

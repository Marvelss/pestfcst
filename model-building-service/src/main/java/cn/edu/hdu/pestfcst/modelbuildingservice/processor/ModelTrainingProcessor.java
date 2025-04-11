package cn.edu.hdu.pestfcst.modelbuildingservice.processor;/*
 * @Author : Vagrant
 * @Time: 2025-03-31 20:50
 * @File : ModelTrainingProcessor.java
 * @Description : 模型训练
 */

import cn.edu.hdu.pestfcst.modelbuildingservice.bean.ModelingRecord;
import cn.edu.hdu.pestfcst.modelbuildingservice.service.impl.ModelBuildingDataSetServiceImpl;
import com.sun.rowset.internal.Row;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class ModelTrainingProcessor {
    //    @KafkaListener(topics = "model-building-tasks")
    public String trainSVMModel(String message) {
        System.out.println("----5----" + ModelTrainingProcessor.class.getName() +
                "----trainSVMModel()----Processor接收Service执行模型训练" + message);
//        // 获取优选特征（MongoDB）
//        Query featureQuery = new Query(Criteria.where("taskId").is(optimizationTaskId));
//        FeatureOptimizationData featureData = mongoTemplate.findOne(featureQuery, FeatureOptimizationData.class);
//
//        // 获取训练数据（示例：Spark MLlib）
//        Dataset<Row> dataset = spark.read()
//                .format("mongo")
//                .option("uri", "mongodb://host/db.preprocessed_data")
//                .load()
//                .selectExpr(featureData.getFeatureList().toArray(new String[0]));
//
//        // 模型训练（示例：XGBoost）
//        XGBoostClassifier model = new XGBoostClassifier()
//                .setLabelCol("label")
//                .setFeaturesCol("features");
//        XGBoostClassificationModel trainedModel = model.fit(dataset);
//
//        // 保存模型（MongoDB）
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        trainedModel.save(baos);
//        MLModel mlModel = new MLModel();
//        mlModel.setModelId(modelId);
//        mlModel.setModelBytes(baos.toByteArray());
//        mongoTemplate.save(mlModel);
//
//        // 评估精度（示例）
//        Dataset<Row> predictions = trainedModel.transform(dataset);
//        MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
//                .setLabelCol("label")
//                .setPredictionCol("prediction")
//                .setMetricName("accuracy");
//        Double accuracy = evaluator.evaluate(predictions);
//
//        // 更新记录（MySQL）
//        modelRepo.updateStatusAndAccuracy(modelId, "COMPLETED", accuracy);
//
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
        return "OA=0.9";
    }
}
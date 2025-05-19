package cn.edu.hdu.pestfcst.modelbuildingservice.processor;/*
 * @Author : Vagrant
 * @Time: 2025-03-31 20:50
 * @File : ModelTrainingProcessor.java
 * @Description : 模型训练
 */

import cn.edu.hdu.pestfcst.modelbuildingservice.bean.ModelingRecord;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.LinearSVC;
import org.apache.spark.ml.classification.RandomForestClassifier;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;


@Service
public class ModelTrainingProcessor {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final SparkSession spark = SparkSession.builder()
            .appName("ModelTrainingProcessor")
            .master("local[*]") // 可替换为集群地址
            .getOrCreate();

    public ModelingRecord trainModel(String modelID, String dataPath, String modelName, JsonNode modelParams,
                                     String[] featureCols, String labelCol,
                                     String trainTestSplitRatio,
                                     JsonNode metricNames) {

        ModelingRecord result = new ModelingRecord();
        try {
            // 1. 获取数据集
            Dataset<Row> data = spark.read()
                    .option("header", "true")
                    .option("inferSchema", "true")
                    .csv(dataPath);
            // 1.1. 特征向量化
            VectorAssembler assembler = new VectorAssembler()
                    .setInputCols(featureCols)
                    .setOutputCol("features");
            // 1.2. 获取指定特征与标签数据
            Dataset<Row> assembledData = assembler.transform(data)
                    .withColumnRenamed(labelCol, "label")
                    .select("features", "label");

            // 2. 拆分数据集
            double[] splitRatios = parseTrainTestSplitRatio(trainTestSplitRatio); // e.g., "8:2" => [0.8, 0.2]
            Dataset<Row>[] splits = assembledData.randomSplit(splitRatios, 1234);
            Dataset<Row> trainData = splits[0];
            Dataset<Row> testData = splits[1];

            // 3. 初始化模型
            PipelineStage classifier = createClassifier(modelName, modelParams);
            if (classifier == null) {
                throw new UnsupportedOperationException("不支持的模型类型: " + modelName);
            }

            // 4. 构建管道并训练
            Pipeline pipeline = new Pipeline().setStages(new PipelineStage[]{classifier});
            PipelineModel model = pipeline.fit(trainData);
            Dataset<Row> predictions = model.transform(testData);

            // 5. 评估指标
            Map<String, Double> metricResults = new HashMap<>();
            for (JsonNode metricNode : metricNames) {
                String metric = metricNode.asText();
                MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
                        .setLabelCol("label")
                        .setPredictionCol("prediction")
                        .setMetricName(metric);
                double score = evaluator.evaluate(predictions);
                metricResults.put(metric, Math.round(score * 1000.0) / 1000.0);
            }
            // 6. 保存预测结果
            String predictionOutputPath = "hdfs://localhost:9000/data/" + modelID + "_predictions.csv";
            predictions.select("prediction", "label")
                    .write()
                    .option("header", "true")
                    .mode(SaveMode.Overwrite)
                    .csv(predictionOutputPath);

            // 7. 保存完整可用模型（用于预测应用）
            String modelSavePath = "hdfs://localhost:9000/data/" + modelID + "_model";
            model.write().overwrite().save(modelSavePath);

            // 8. 更新训练后的结果封装（可用于预测应用）
            result.setModelId(modelID);
            ObjectNode metricsNode = objectMapper.createObjectNode();
            for (Map.Entry<String, Double> entry : metricResults.entrySet()) {
                metricsNode.put(entry.getKey(), entry.getValue());
            }
            result.setEvaluationMetrics(metricsNode);
            result.setTrainingResult(predictionOutputPath);
            result.setModelStructure(modelSavePath);
            result.setModelStatus(3);

        } catch (Exception e) {
            result.setModelId(modelID);
            result.setModelStatus(4);
            e.printStackTrace();
        }

        return result;
    }

    //    解析训练与测试数据集划分
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

    //    构建管道并训练
    private PipelineStage createClassifier(String modelName, JsonNode modelParams) {
        switch (modelName.toLowerCase()) {
            case "svm":
                LinearSVC svm = new LinearSVC()
                        .setLabelCol("label")
                        .setFeaturesCol("features")
                        .setMaxIter(modelParams.path("maxIter").asInt(100))
                        .setRegParam(modelParams.path("regParam").asDouble(0.1));
                return svm;
            case "rf":
                RandomForestClassifier rf = new RandomForestClassifier()
                        .setLabelCol("label")
                        .setFeaturesCol("features")
                        .setMaxDepth(modelParams.path("maxDepth").asInt(5))
                        .setNumTrees(modelParams.path("numTrees").asInt(10));
                return rf;
            // 可扩展支持更多模型
            default:
                return null;
        }
    }
}
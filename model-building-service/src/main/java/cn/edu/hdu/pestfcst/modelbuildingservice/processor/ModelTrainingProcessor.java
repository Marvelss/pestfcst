package cn.edu.hdu.pestfcst.modelbuildingservice.processor;/*
 * @Author : Vagrant
 * @Time: 2025-03-31 20:50
 * @File : ModelTrainingProcessor.java
 * @Description : 模型训练
 */

import net.minidev.json.JSONObject;
import org.apache.spark.ml.classification.LinearSVC;
import org.apache.spark.ml.classification.LinearSVCModel;
import org.apache.spark.ml.classification.RandomForestClassificationModel;
import org.apache.spark.ml.classification.RandomForestClassifier;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.List;


@Service
public class ModelTrainingProcessor {
    private static final SparkSession spark = SparkSession.builder()
            .appName("ModelTrainingProcessor")
            .master("local[*]") // 可替换为集群地址
            .getOrCreate();

    public JSONObject trainModel(String dataPath, String modelName, String modelMethodParam,
                                 List<String> featureCols, String labelCol,
                                 String trainTestSplitRatio,
                                 String metricName) {
        JSONObject result = new JSONObject();
        try {
            if (!"svm".equalsIgnoreCase(modelName)) {
                throw new UnsupportedOperationException("仅支持 SVM 模型，目前传入类型: " + modelName);
            }
//            String[] featureCols = parseFeatureCols(featureCols);
//            double[] trainTestSplitRatio = parseTrainTestSplitRatio(trainTestSplitRatio);

            Dataset<Row> data = spark.read()
                    .option("header", "true")
                    .option("inferSchema", "true")
                    .csv(dataPath);

//            VectorAssembler assembler = new VectorAssembler()
//                    .setInputCols(featureCols)
//                    .setOutputCol("features");
//            Dataset<Row> assembledData = assembler.transform(data)
//                    .withColumnRenamed(labelCol, "label")
//                    .select("features", "label");
//
//            Dataset<Row>[] splits = assembledData.randomSplit(trainTestSplitRatio, 1234);
//            Dataset<Row> trainData = splits[0];
//            Dataset<Row> testData = splits[1];
            Dataset<Row> trainData = null;
            Dataset<Row> testData = null;
            LinearSVC svm = new LinearSVC()
                    .setMaxIter(100)
                    .setRegParam(0.1)
                    .setLabelCol("label")
                    .setFeaturesCol("features");
            LinearSVCModel model = svm.fit(trainData);

            Dataset<Row> predictions = model.transform(testData);

            MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
                    .setLabelCol("label")
                    .setPredictionCol("prediction")
                    .setMetricName(metricName);

            double accuracy = evaluator.evaluate(predictions);

            saveModel(model, modelName);

            result.put("msg", "模型训练完成");
            result.put("模型准确率", accuracy);
            result.put("success", true);

        } catch (Exception e) {
            result.put("msg", "模型训练失败：" + e.getMessage());
            result.put("success", false);
            e.printStackTrace();
        }

        return result;
    }


    private String[] parseFeatureCols(String featureColsJson) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> featureColsList = objectMapper.readValue(featureColsJson, new TypeReference<List<String>>() {
        });
        return featureColsList.toArray(new String[0]);
    }


    private double[] parseTrainTestSplitRatio(String ratioStr) {
        String[] parts = ratioStr.split(":");
        double[] ratio = new double[parts.length];
        for (int i = 0; i < parts.length; i++) {
            ratio[i] = Double.parseDouble(parts[i]);
        }
        return ratio;
    }

    private void saveModel(LinearSVCModel model, String modelName) {
        try {
            String modelPath = "models/" + modelName;
            model.write().overwrite().save(modelPath);
            System.out.println("[模型保存成功] 路径: " + modelPath);
        } catch (Exception e) {
            System.err.println("[模型保存失败] " + e.getMessage());
        }
    }
}
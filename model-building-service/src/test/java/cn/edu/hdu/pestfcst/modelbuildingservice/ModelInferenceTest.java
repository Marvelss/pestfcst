package cn.edu.hdu.pestfcst.modelbuildingservice;

/**
 * @Author : Vagrant
 * @Time: 2025-05-19 19:23
 * @File : ModelInferenceTest.java
 * @Description :加载已训练模型并进行预测
 */

import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class ModelInferenceTest {
    @Test
    public void testModelPredict() {
        // 初始化 SparkSession
        SparkSession spark = SparkSession.builder()
                .appName("ModelInferenceTest")
                .master("local[*]")
                .getOrCreate();

        // 模型和输入路径（与保存路径一致）
        String modelPath = "hdfs://localhost:9000/data/4a_model"; // 替换为实际 ID
        String inputDataPath = "hdfs://localhost:9000/data/input2-predict2.csv";
        // 加载模型
        PipelineModel loadedModel = PipelineModel.load(modelPath);

        // 加载新数据（用于预测）
        Dataset<Row> inputData = spark.read()
                .option("header", "true")
                .option("inferSchema", "true")
                .csv(inputDataPath);
        inputData.show();
        // 可选：手动检查列名
        String[] cols = inputData.columns();
        System.out.println("Input columns: " + Arrays.toString(cols));

        VectorAssembler assembler = new VectorAssembler()
                .setInputCols(new String[]{"3月上旬温度", "3月中旬温度", "5月上旬温度", "6月上旬温度"})
                .setOutputCol("features");
        Dataset<Row> assembled = assembler.transform(inputData);
        assembled.show();

        // 应用模型进行预测
        Dataset<Row> predictions = loadedModel.transform(assembled);
        System.out.println("训练完成,前10行结果如下:");
        // 显示部分预测结果
        predictions.select("features", "prediction").show(10);

        spark.stop();
    }
}

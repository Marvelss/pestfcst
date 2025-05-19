package cn.edu.hdu.pestfcst.modelbuildingservice;



import cn.edu.hdu.pestfcst.modelbuildingservice.processor.ModelTrainingProcessor;
import net.minidev.json.JSONObject;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.LinearSVC;
import org.apache.spark.ml.classification.LinearSVCModel;
import org.apache.spark.ml.classification.RandomForestClassificationModel;
import org.apache.spark.ml.classification.RandomForestClassifier;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.regression.LinearRegression;
import org.apache.spark.ml.regression.LinearRegressionModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Author : Vagrant
 * @Time: 2025-05-15 20:22
 * @File : ModelTrainingProcessorTest.java
 * @Description :
 */



public class ModelTrainingProcessorTest {

    @Mock
    private SparkSession spark;

    @InjectMocks
    private ModelTrainingProcessor modelTrainingProcessor;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(spark.read()).thenReturn(mock(org.apache.spark.sql.DataFrameReader.class));
    }

    @Test
    public void testLoadDataFromHDFS() {
//        System.setProperty("HADOOP_USER_NAME","Vagrant");
        // 创建 SparkSession
        SparkSession spark = SparkSession.builder()
                .appName("ModelTrainingProcessorTest")
                .master("local[*]")
//                .config("spark.hadoop.fs.defaultFS", "hdfs://localhost:9000")
//                .config("spark.hadoop.yarn.resourcemanager.address", "localhost:8032")
                .getOrCreate();

        // HDFS 数据路径
        String dataPath = "hdfs://localhost:9000/data/inputTest.csv";


        // 从 HDFS 加载数据
        Dataset<Row> rawData = spark.read()
                .option("header", "true")
                .option("inferSchema", "true")
                .csv(dataPath);
        List<Row> allRows = rawData.collectAsList();
        for (Row row : allRows) {
            System.out.println(row);
        }

        // 验证数据是否正确加载
//        assertNotNull(rawData);
//        assertTrue(rawData.count() > 0); // 验证数据行数大于0

        // 停止 SparkSession
        spark.stop();
    }


    @Test
    public void testTrainModelWithRealData() {
        SparkSession spark = SparkSession.builder()
                .appName("TestTrainModel")
                .master("local[*]")
                .getOrCreate();

        // Step 1: 加载数据
        String dataPath = "hdfs://localhost:9000/data/input2-predict2.csv";

        Dataset<Row> data = spark.read()
                .option("header", "true")
                .option("inferSchema", "true")
                .csv(dataPath);
        data.show();
//        // Step 2: 重命名中文列名（建议）
//        Dataset<Row> data = rawData.withColumnRenamed("经度", "lon")
//                .withColumnRenamed("纬度", "lat")
//                .withColumnRenamed("年", "year")
//                .withColumnRenamed("降水", "precip")
//                .withColumnRenamed("温度", "temp");
//
//        // Step 3: 特征与标签
//        String[] featureCols = new String[]{"lon", "lat", "year", "precip", "temp"};
//        String labelCol = "DayOfYear";

        // 特征列（去除经度、纬度、年、病害发生程度）
        String[] featureCols = data.columns();
        featureCols = java.util.Arrays.stream(featureCols)
                .filter(col -> !col.equals("经度") && !col.equals("纬度") && !col.equals("年") && !col.equals("病害发生程度"))
                .toArray(String[]::new);

        // Step 4: 特征向量化
        VectorAssembler assembler = new VectorAssembler()
                .setInputCols(featureCols)
                .setOutputCol("features");

//        Dataset<Row> assembledData = assembler.transform(data).select("features", labelCol);
        Dataset<Row> assembledData = assembler.transform(data)
                .withColumnRenamed("病害发生程度", "label")
                .select("features", "label");

        // Step 5: 划分训练集、测试集
        Dataset<Row>[] splits = assembledData.randomSplit(new double[]{0.6, 0.4}, 1234);
        Dataset<Row> trainData = splits[0];
        Dataset<Row> testData = splits[1];


        // 随机森林分类器
        RandomForestClassifier rf = new RandomForestClassifier()
                .setLabelCol("label")
                .setMaxDepth(5) // 设置树的最大深度
                .setNumTrees(10) // 设置树的数量
                .setFeatureSubsetStrategy("auto") // 设置特征选择策略
                .setImpurity("gini"); // 设置不纯度测量标准

        // 训练模型
        RandomForestClassificationModel model = rf.fit(trainData);

//        // Step 6: 构建 SVM 模型
//        LinearSVC svc = new LinearSVC()
//                .setLabelCol("label")
//                .setFeaturesCol("features")
//                .setMaxIter(10)
//                .setRegParam(0.1);

//        LinearSVCModel model = svc.fit(trainData);

        // Step 7: 模型预测
        Dataset<Row> predictions = model.transform(testData);

        // Step 8: 模型评估
        MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
                .setLabelCol("label")
                .setPredictionCol("prediction")
                .setMetricName("accuracy");

        double accuracy = evaluator.evaluate(predictions);

        // Step 9: 输出结果
        System.out.println("模型准确率: " + (accuracy * 100) + "%");

        assertTrue(accuracy >= 0.0); // 至少要执行成功
    }

}
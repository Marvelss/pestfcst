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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.kafka.core.KafkaTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
public class ModelBuildingDataSetServiceImpl implements ModelBuildingDataSetService {

    @Autowired
    private ModelingRecordRepository modelBuildingDataRepository;
    @Autowired
    private ModelTrainingProcessor modelTrainingProcessor;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    // 文件上传配置
    @Value("${file.upload.path:uploads}")
    private String uploadPath;

    @Value("${file.upload.max-size:10485760}")
    private long maxFileSize; // 默认10MB

//    @Autowired
//    private RedisTemplate<String, String> redisTemplate;

//    @Value("${redis.key.model.precision}")
//    private String precisionKeyPattern;

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
        // 如果有需要处理的任务才发送
        if (!tasksToProcess.isEmpty()) {
            System.out.println("----2----" + ModelBuildingDataSetServiceImpl.class.getName() +
                    "----buildModel()----Service发送kafka建模任务");
            // 发送Kafka消息
            sendMessage("model-building-tasks", tasksToProcess);
        } else {
            System.out.println("没有需要处理的新建模任务");
        }
    }

    public void executeModelTraining(String modelingInfo) throws IOException {
        System.out.println("----4----" + ModelBuildingDataSetServiceImpl.class.getName() +
                "----executeModelTraining()----Service调用Processor开始模型训练");
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
            String[] featureCols = tempModelingRecord.getFeatures(); // 特征列
            String labelCol = tempModelingRecord.getLabel(); // 标签列
            String trainTestSplitRatio = tempModelingRecord.getDatasetSplitRatio(); // 数据集分配比例
            JsonNode metricName = tempModelingRecord.getEvaluationMetrics(); // 精度指标

//            调用建模方法
            trainResult = modelTrainingProcessor.trainModel(modelID,
                    dataPath, modelName, modelMethodParam,
                    featureCols, labelCol,
                    trainTestSplitRatio,
                    metricName);

        } catch (Exception e) {
            System.out.println("模型训练失败: " + e.getMessage());
            // log.error("模型训练异常", e); // 可选日志
        } finally {
            // ④发送建模结果到 Kafka
            System.out.println("----6----" + ModelBuildingDataSetServiceImpl.class.getName() +
                    "----executeModelTraining()----Service发送kafka建模结果");
            sendMessage("model-building-results", trainResult);
        }
    }

    @Override
    public void saveBuildResult(String result) throws IOException {
        //  从数据库获取并更新模型记录
        //  获取模型记录结果
        ModelingRecord tempModelingRecord = parseModelingRecord(result);
        System.out.println("----8----" + ModelBuildingDataSetServiceImpl.class.getName() +
                "----saveBuildResult()----Service保存建模结果" + "编号:" + tempModelingRecord.getModelId());
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

    @Override
    public String saveUploadedFile(MultipartFile file) throws IOException {
        System.out.println("----文件保存----" + ModelBuildingDataSetServiceImpl.class.getName() +
                "----saveUploadedFile()----开始保存上传文件: " + file.getOriginalFilename());

        // 1. 验证文件
        validateFile(file);

        // 2. 创建上传目录
        String uploadDir = createUploadDirectory();

        // 3. 生成唯一文件名
        String fileName = generateUniqueFileName(file.getOriginalFilename());

        // 4. 构建完整文件路径
        Path filePath = Paths.get(uploadDir, fileName);

        // 5. 保存文件
        try {
            Files.copy(file.getInputStream(), filePath);
            System.out.println("文件保存成功: " + filePath.toString());
            return filePath.toString();
        } catch (IOException e) {
            System.err.println("文件保存失败: " + e.getMessage());
            throw new IOException("文件保存失败: " + e.getMessage(), e);
        }
    }

    /**
     * 验证上传的文件
     * @param file 上传的文件
     * @throws IOException 验证失败时抛出异常
     */
    private void validateFile(MultipartFile file) throws IOException {
        // 检查文件是否为空
        if (file.isEmpty()) {
            throw new IOException("上传的文件为空");
        }

        // 检查文件大小
        if (file.getSize() > maxFileSize) {
            throw new IOException("文件大小超过限制，最大允许: " + (maxFileSize / 1024 / 1024) + "MB");
        }

        // 检查文件扩展名（可选，根据业务需求调整）
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            String extension = getFileExtension(originalFilename);
            String[] allowedExtensions = {".csv", ".xlsx", ".xls", ".txt", ".json", ".xml"};
            boolean isValidExtension = false;
            for (String allowedExt : allowedExtensions) {
                if (allowedExt.equalsIgnoreCase(extension)) {
                    isValidExtension = true;
                    break;
                }
            }
            if (!isValidExtension) {
                throw new IOException("不支持的文件类型: " + extension + "，支持的类型: " + String.join(", ", allowedExtensions));
            }
        }
    }

    /**
     * 创建上传目录
     * @return 上传目录路径
     * @throws IOException 目录创建失败时抛出异常
     */
    private String createUploadDirectory() throws IOException {
        // 创建按日期分组的目录结构
        String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String fullUploadDir = uploadPath + File.separator + dateDir;

        Path uploadDirPath = Paths.get(fullUploadDir);
        if (!Files.exists(uploadDirPath)) {
            try {
                Files.createDirectories(uploadDirPath);
                System.out.println("创建上传目录: " + uploadDirPath.toString());
            } catch (IOException e) {
                throw new IOException("创建上传目录失败: " + e.getMessage(), e);
            }
        }

        return fullUploadDir;
    }

    /**
     * 生成唯一文件名
     * @param originalFilename 原始文件名
     * @return 唯一文件名
     */
    private String generateUniqueFileName(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String baseName = originalFilename != null ? 
            originalFilename.substring(0, originalFilename.lastIndexOf('.')) : "file";
        
        // 生成UUID作为唯一标识
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        
        // 添加时间戳
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
        
        return baseName + "_" + timestamp + "_" + uuid + extension;
    }

    /**
     * 获取文件扩展名
     * @param filename 文件名
     * @return 文件扩展名（包含点号）
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf('.') == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.')).toLowerCase();
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

}

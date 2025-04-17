package cn.edu.hdu.pestfcst.modelbuildingservice.controller;/*
 * @Author : Vagrant
 * @Time: 2025-03-26 10:26
 * @File : ModelBuildingDataSetController.java
 * @Description :
 */

import cn.edu.hdu.pestfcst.modelbuildingservice.bean.ModelingRecord;
import cn.edu.hdu.pestfcst.modelbuildingservice.service.ModelBuildingDataSetService;
import cn.edu.hdu.pestfcst.modelbuildingservice.service.impl.ModelBuildingDataSetServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/model-building-dataset")
public class ModelBuildingDataSetController {
    @Autowired
    private ModelBuildingDataSetService modelBuildingDataSetService;

    @PostMapping("/model-train")
    public ResponseEntity<String> modelTrain(@RequestBody Map<String, Object> request) {
        List<String> modelIds = (List<String>) request.get("modelIds"); // modelIds 现在是 String 类型
        String model = (String) request.get("model");
        Map<String, Object> modelParams = (Map<String, Object>) request.get("modelParams");
        List<String> features = (List<String>) request.get("features");
        String label = (String) request.get("label");
        List<String> evaluationMetrics = (List<String>) request.get("evaluationMetrics");
        String datasetSplitRatio = (String) request.get("datasetSplitRatio");
        String modelStructure = (String) request.get("modelStructure");
        System.out.println("----1----" + ModelBuildingDataSetController.class.getName() +
                "----/model-building-dataset/model-train----接收API接口请求");
        for (String modelId : modelIds) {
            ModelingRecord pestData = new ModelingRecord();
            pestData.setModelId(modelId);
            pestData.setModelMethod(model);
            pestData.setModelMethodParam(modelParams.toString());
            pestData.setFeatures(features.toString());
            pestData.setLabel(label);
            pestData.setEvaluationMetrics(evaluationMetrics.toString());
            pestData.setDatasetSplitRatio(datasetSplitRatio);
            pestData.setModelStructure(modelStructure);
            System.out.println("发送子建模任务ID：" + modelId);
            modelBuildingDataSetService.buildModel(pestData);
        }
        return ResponseEntity.ok("Model training tasks have been successfully submitted.");

    }

    @GetMapping("/send-message")
    public String sendMessage() {
        String message = "Hello, Kafka!";
//        kafkaProducerService.sendMessage("test",  new ModelBuildingDataSet());
        return "Message sent to Kafka topic: test";
    }
}

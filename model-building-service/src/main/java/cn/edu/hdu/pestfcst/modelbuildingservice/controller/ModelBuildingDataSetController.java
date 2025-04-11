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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/model-building-dataset")
public class ModelBuildingDataSetController {
    @Autowired
    private ModelBuildingDataSetService modelBuildingDataSetService;

    @GetMapping("/model-train/{modelIdList}")
    public ResponseEntity<String> modelTrain(@PathVariable List<String> modelIdList) {
        for (String id : modelIdList) {
//            ModelingRecord pestData = modelBuildingDataSetService.getModelBuildingRecordSetByID(id);
//            测试数据
            ModelingRecord pestData = new ModelingRecord();
            pestData.setModelId(Long.valueOf(id));
            pestData.setModelMethod("SVM-Test");
            pestData.setFeatureOptimizationId(Long.valueOf("666"));
            System.out.println("----1----" + ModelBuildingDataSetController.class.getName() +
                    "----Controller传入参数至Service" + "ID：" + id);
            modelBuildingDataSetService.buildModel(pestData);
        }
        return ResponseEntity.ok("/model-train/{model-id-list}----success");
    }

    @GetMapping("/send-message")
    public String sendMessage() {
        String message = "Hello, Kafka!";
//        kafkaProducerService.sendMessage("test",  new ModelBuildingDataSet());
        return "Message sent to Kafka topic: test";
    }
}

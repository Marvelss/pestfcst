package cn.edu.hdu.pestfcst.modelbuildingservice.controller;/*
 * @Author : Vagrant
 * @Time: 2025-03-26 10:26
 * @File : ModelBuildingDataSetController.java
 * @Description :
 */

import cn.edu.hdu.pestfcst.modelbuildingservice.bean.ModelBuildingDataSet;
import cn.edu.hdu.pestfcst.modelbuildingservice.service.ModelBuildingDataSetService;
import cn.edu.hdu.pestfcst.modelbuildingservice.stream.KafkaProducerServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/model-building-dataset")
public class ModelBuildingDataSetController {
    @Autowired
    private ModelBuildingDataSetService modelBuildingDataSetService;

    @Autowired
    private KafkaProducerServiceImpl kafkaProducerService;

//    @GetMapping("/username/{id}")
//    public String getModelBuildingDataSetByIdMessage(@PathVariable long id) {
//        // 从数据库中获取数据
//        ModelBuildingDataSet dataSet = modelBuildingDataSetService.getModelBuildingDataSetByID(id);
//
//        // 将数据转换为JSON字符串
//        ObjectMapper objectMapper = new ObjectMapper();
//        String message;
//        try {
//            message = objectMapper.writeValueAsString(dataSet);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Failed to convert data to JSON";
//        }
//
//        // 发送消息到Kafka
//        kafkaProducerService.sendMessage("test", new ModelBuildingDataSet());
//
//        return "Message sent to Kafka topic: test";
//    }

//
//    @GetMapping("/id/{id}")
//    public ResponseEntity<String> getModelBuildingDataSetById(@PathVariable String id) {
//        try {
//            // 从MongoDB获取数据
//            ModelBuildingDataSet pestData = modelBuildingDataSetService.getModelBuildingDataSetByID(id);
//            System.out.println("内容: " + pestData);
//
//            // 发送数据到Kafka
//            kafkaProducerService.sendMessage("test-topic", pestData);
//
//            return ResponseEntity.ok("Message sent to Kafka topic: test-topic");
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Controller-ModelBuildingDataSet not found with ID: " + id);
//        }
//    }
    @GetMapping("/all")
    public ResponseEntity<List<ModelBuildingDataSet>> getAllModelBuildingDataSets() {
        List<ModelBuildingDataSet> dataSets = modelBuildingDataSetService.getAllModelBuildingDataSets();
        System.out.println("内容: " + dataSets);

        // 异步发送数据到 Kafka
        kafkaProducerService.sendDataSetsToKafkaAsync("test-topic", dataSets);

        return ResponseEntity.ok(dataSets);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<ModelBuildingDataSet> getModelBuildingDataSetById(@PathVariable String id) {
        // 创建一个PestData对象
//        ModelBuildingDataSet pestData = new ModelBuildingDataSet();
        ModelBuildingDataSet pestData = modelBuildingDataSetService.getModelBuildingDataSetByID(id);
        // 发送数据到Kafka
        kafkaProducerService.sendMessage("test-topic", pestData);

        return ResponseEntity.ok(pestData);

    }

    @GetMapping("/send-message")
    public String sendMessage() {
        String message = "Hello, Kafka!";
//        kafkaProducerService.sendMessage("test",  new ModelBuildingDataSet());
        return "Message sent to Kafka topic: test";
    }
}

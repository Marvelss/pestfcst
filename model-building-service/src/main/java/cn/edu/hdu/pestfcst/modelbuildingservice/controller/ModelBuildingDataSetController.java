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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/model-building-dataset")
public class ModelBuildingDataSetController {
    @Autowired
    private ModelBuildingDataSetService modelBuildingDataSetService;

    @PostMapping("/model-train")
    public ResponseEntity<String> modelTrain(@RequestBody Map<String, Object> request) {
        System.out.println("----1----" + ModelBuildingDataSetController.class.getName() +
                "----/model-building-dataset/model-train----接收API接口请求");

        // 提取 userId 参数
        Object userIdObj = request.get("userId");
        if (userIdObj == null) {
            return ResponseEntity.badRequest().body("参数错误：缺少 userId");
        }
        Long userId;
        try {
            userId = Long.parseLong(userIdObj.toString());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("参数错误：userId 格式非法");
        }

        // 调用建模服务
        modelBuildingDataSetService.buildModel(userId);

        return ResponseEntity.ok("模型训练任务提交成功！");
    }

    @GetMapping("/send-message")
    public String sendMessage() {
        String message = "Hello, Kafka!";
//        kafkaProducerService.sendMessage("test",  new ModelBuildingDataSet());
        return "Message sent to Kafka topic: test";
    }
}

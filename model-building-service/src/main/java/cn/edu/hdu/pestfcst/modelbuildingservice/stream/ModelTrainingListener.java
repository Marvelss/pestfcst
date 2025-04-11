package cn.edu.hdu.pestfcst.modelbuildingservice.stream;/*
 * @Author : Vagrant
 * @Time: 2025-03-26 10:15
 * @File : ModelTrainingListener.java
 * @Description : 模型训练任务消息监听
 */

import cn.edu.hdu.pestfcst.modelbuildingservice.service.impl.ModelBuildingDataSetServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;



@Service
public class ModelTrainingListener {
    private static final Logger logger = LoggerFactory.getLogger(ModelTrainingListener.class);
    @Autowired
    private ModelBuildingDataSetServiceImpl modelTrainingService;

    @KafkaListener(topics = "model-building-tasks", groupId = "my-group")
    public void receiveMessage(String message) {
        System.out.println("----3----" + ModelTrainingListener.class.getName() +
                "----receiveMessage()----kafka接收Service建模任务" + message);
        System.out.println("接收到建模任务,调用建模方法: " + message);
        modelTrainingService.executeModelTraining(message);
        try {

        } catch (Exception e) {
            logger.error("Failed to parse message: {}", message, e);
        }
    }

    @KafkaListener(topics = "model-building-results", groupId = "my-group")
    public void onModelTrainingResult(String result) {
        result = "71";
        System.out.println("----7----" + ModelTrainingListener.class.getName() +
                "----onModelTrainingResult()----kafka接收Service建模结果" + "71");
        modelTrainingService.saveBuildResult(result);
    }
}


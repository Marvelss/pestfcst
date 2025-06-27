package cn.edu.hdu.pestfcst.modelbuildingservice.stream;/*
 * @Author : Vagrant
 * @Time: 2025-03-26 10:15
 * @File : ModelTrainingListener.java
 * @Description : 模型训练任务消息监听
 */

import cn.edu.hdu.pestfcst.modelbuildingservice.bean.ModelingRecord;
import cn.edu.hdu.pestfcst.modelbuildingservice.service.impl.ModelBuildingDataSetServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.List;


@Service
public class ModelTrainingListener {
    private static final Logger logger = LoggerFactory.getLogger(ModelTrainingListener.class);
    @Autowired
    private ModelBuildingDataSetServiceImpl modelTrainingService;

    // 移除了Kafka监听器，改为普通方法
    public void processModelTrainingTask(String message) throws IOException {
        System.out.println("----3----" + ModelTrainingListener.class.getName() +
                "----processModelTrainingTask()----处理建模任务" + message);

        ObjectMapper objectMapper = new ObjectMapper();
        List<ModelingRecord> taskList = objectMapper.readValue(message, new TypeReference<List<ModelingRecord>>() {
        });
        for (ModelingRecord task : taskList) {
            System.out.println("接收到的任务" + objectMapper.writeValueAsString(task));
            modelTrainingService.executeModelTraining(objectMapper.writeValueAsString(task));
        }
    }

    // 移除了Kafka监听器，改为普通方法
    public void processModelTrainingResult(String result) throws IOException {
        System.out.println("----7----" + ModelTrainingListener.class.getName() +
                "----processModelTrainingResult()----处理建模结果" + result);
        modelTrainingService.saveBuildResult(result);
    }
}


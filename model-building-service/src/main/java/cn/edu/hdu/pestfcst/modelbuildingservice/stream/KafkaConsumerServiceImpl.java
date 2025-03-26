package cn.edu.hdu.pestfcst.modelbuildingservice.stream;/*
 * @Author : Vagrant
 * @Time: 2025-03-26 10:15
 * @File : KafkaConsumerServiceImpl.java
 * @Description : kafka服务消费者
 */

import cn.edu.hdu.pestfcst.modelbuildingservice.bean.ModelBuildingDataSet;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerServiceImpl {
    @KafkaListener(topics = "test-topic", groupId = "my-group")
    public void receiveMessage(String message) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ModelBuildingDataSet pestData = objectMapper.readValue(message, ModelBuildingDataSet.class);
            System.out.println("Received message: " + pestData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

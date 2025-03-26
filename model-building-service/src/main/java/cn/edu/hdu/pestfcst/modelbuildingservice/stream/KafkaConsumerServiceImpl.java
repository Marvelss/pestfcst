package cn.edu.hdu.pestfcst.modelbuildingservice.stream;/*
 * @Author : Vagrant
 * @Time: 2025-03-26 10:15
 * @File : KafkaConsumerServiceImpl.java
 * @Description : kafka服务消费者
 */

import cn.edu.hdu.pestfcst.modelbuildingservice.bean.ModelBuildingDataSet;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


@Component
public class KafkaConsumerServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerServiceImpl.class);

    @KafkaListener(topics = "test-topic", groupId = "my-group")
    public void receiveMessage(String message) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ModelBuildingDataSet pestData = objectMapper.readValue(message, ModelBuildingDataSet.class);
            logger.info("Received message: {}", pestData);
        } catch (Exception e) {
            logger.error("Failed to parse message: {}", message, e);
        }
    }
}


//@Service
//public class KafkaConsumerServiceImpl {
//    @KafkaListener(topics = "test-topic", groupId = "my-group")
//    public void receiveMessage(String message) {
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            ModelBuildingDataSet pestData = objectMapper.readValue(message, ModelBuildingDataSet.class);
//            System.out.println("Received message: " + pestData);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}

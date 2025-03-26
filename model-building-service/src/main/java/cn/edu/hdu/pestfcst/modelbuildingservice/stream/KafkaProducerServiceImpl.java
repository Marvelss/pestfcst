package cn.edu.hdu.pestfcst.modelbuildingservice.stream;/*
 * @Author : Vagrant
 * @Time: 2025-03-26 10:14
 * @File : KafkaProducerServiceImpl.java
 * @Description : kafka生产者
 */

import cn.edu.hdu.pestfcst.modelbuildingservice.bean.ModelBuildingDataSet;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerServiceImpl {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String topic, ModelBuildingDataSet message) {
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
}

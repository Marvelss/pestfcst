package cn.edu.hdu.pestfcst.modelbuildingservice.stream;/*
 * @Author : Vagrant
 * @Time: 2025-03-26 10:14
 * @File : KafkaProducerServiceImpl.java
 * @Description : kafka生产者
 */

import cn.edu.hdu.pestfcst.modelbuildingservice.bean.ModelBuildingDataSet;
import cn.edu.hdu.pestfcst.modelbuildingservice.dao.ModelBuildingDataRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public void sendDataSetsToKafkaAsync(String topic, List<ModelBuildingDataSet> dataSets) {
        ObjectMapper objectMapper = new ObjectMapper();
        dataSets.forEach(dataSet -> {
            try {
                String jsonString = objectMapper.writeValueAsString(dataSet);
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}

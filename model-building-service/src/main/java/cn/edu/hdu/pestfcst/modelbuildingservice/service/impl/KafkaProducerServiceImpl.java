package cn.edu.hdu.pestfcst.modelbuildingservice.service.impl;/*
 * @Author : Vagrant
 * @Time: 2025-03-26 10:14
 * @File : KafkaProducerServiceImpl.java
 * @Description : kafka生产者
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerServiceImpl {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message).addCallback(
                success -> {
                    if (success != null) {
                        System.out.println("Message sent successfully: " + message);
                    }
                },
                failure -> {
                    System.err.println("Failed to send message: " + failure.getMessage());
                }
        );
    }
}

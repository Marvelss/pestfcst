package cn.edu.hdu.pestfcst.modelbuildingservice.service.impl;/*
 * @Author : Vagrant
 * @Time: 2025-03-26 10:15
 * @File : KafkaConsumerServiceImpl.java
 * @Description : kafka服务消费者
 */

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerServiceImpl {
    @KafkaListener(topics = "test", groupId = "my-group")
    public void receiveMessage(String message) {
        System.out.println("Received message: " + message);
    }
}

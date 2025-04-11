//package cn.edu.hdu.pestfcst.modelbuildingservice.config;
//
//
//import cn.edu.hdu.pestfcst.modelbuildingservice.bean.ModelingRecord;
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.clients.producer.ProducerConfig;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.apache.kafka.common.serialization.StringSerializer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.annotation.EnableKafka;
//import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
//import org.springframework.kafka.core.*;
//import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
//import org.springframework.kafka.listener.ContainerProperties;
//import org.springframework.kafka.listener.DefaultErrorHandler;
//import org.springframework.kafka.listener.ErrorHandler;
//import org.springframework.kafka.support.serializer.JsonDeserializer;
//import org.springframework.kafka.support.serializer.JsonSerializer;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @Author : Vagrant
// * @Time: 2025-04-10 19:50
// * @File : KafkaConfig.java
// * @Description :
// */
//
//
//@Configuration
//@EnableKafka
//public class KafkaConfig {
//
//    @Bean
//    public KafkaTemplate<String, String> kafkaTemplate() {
//        return new KafkaTemplate<>(producerFactory());
//    }
//
//    @Bean
//    public ProducerFactory<String, String> producerFactory() {
//        Map<String, Object> configProps = new HashMap<>();
//        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//        return new DefaultKafkaProducerFactory<>(configProps);
//    }
//
//    @Bean
//    public ConsumerFactory<String, ModelingRecord> consumerFactory() {
//        Map<String, Object> configProps = new HashMap<>();
//        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "my-group");
//        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
//        return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), new JsonDeserializer<>(ModelingRecord.class));
//    }
//
////    @Bean
////    public ConcurrentKafkaListenerContainerFactory<String, ModelingRecord> kafkaListenerContainerFactory() {
////        ConcurrentKafkaListenerContainerFactory<String, ModelingRecord> factory = new ConcurrentKafkaListenerContainerFactory<>();
////        factory.setConsumerFactory(consumerFactory());
////        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
////        factory.setErrorHandler((ErrorHandler) new DefaultErrorHandler());
////        return factory;
////    }
//}
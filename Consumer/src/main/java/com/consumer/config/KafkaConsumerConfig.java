package com.consumer.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Value("${kafka.consumer.bootstrap}")
    private String bootstrapServers;

    @Value("${kafka.consumer.group1.id}")
    private String group1;

    @Value("${kafka.consumer.group2.id}")
    private String group2;

    @Value("${kafka.consumer.group3.id}")
    private String group3;

    @Value("${kafka.consumer.group4.id}")
    private String group4;

    @Value("${kafka.consumer.group5.id}")
    private String group5;

    @Value("${kafka.consumer.group6.id}")
    private String group6;

    @Value("${kafka.consumer.group7.id}")
    private String group7;

    @Value("${kafka.consumer.offset}")
    private String offsetConfig;

    // Consumer configs common for all consumer groups
    @Bean
    public Map<String, Object> baseConsumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        // list of host:port pairs used for establishing the initial connections to the Kafka cluster
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers);
        // deserialization types
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                IntegerDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);
        // automatically reset the offset to the earliest offset
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetConfig);
        return props;
    }
    // Helper function to configure a consumer group
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>> groupKafkaListenerContainerFactory(String groupName) {
        // 1. This factory is primarily for building containers for @KafkaListener annotated methods.
        ConcurrentKafkaListenerContainerFactory<Integer, String> listenerFactory = new ConcurrentKafkaListenerContainerFactory<>();

        // 2. Set up the config for this consumer group
        Map<String, Object> config = baseConsumerConfigs();
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupName);

        // 3. This factory is used to create new Consumer instances where all consumers
        // share common configuration properties mentioned in this bean.
        ConsumerFactory<Integer, String> consumerFactory = new DefaultKafkaConsumerFactory<>(config);
        listenerFactory.setConsumerFactory(consumerFactory);
        return listenerFactory;
    }

    // Consumer group 1
    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>> group1KafkaListenerContainerFactory() {
        return groupKafkaListenerContainerFactory(group1);
    }

    // Consumer group 2
    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>> group2KafkaListenerContainerFactory() {
        return groupKafkaListenerContainerFactory(group2);
    }

    // Consumer group 3
    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>> group3KafkaListenerContainerFactory() {
        return groupKafkaListenerContainerFactory(group3);
    }

    // Consumer group 4
    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>> group4KafkaListenerContainerFactory() {
        return groupKafkaListenerContainerFactory(group4);
    }

    // Consumer group 5
    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>> group5KafkaListenerContainerFactory() {
        return groupKafkaListenerContainerFactory(group5);
    }

    // Consumer group 6
    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>> group6KafkaListenerContainerFactory() {
        return groupKafkaListenerContainerFactory(group6);
    }

    // Consumer group 7
    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>> group7KafkaListenerContainerFactory() {
        return groupKafkaListenerContainerFactory(group7);
    }

    // Start up the consumer service to start receiving messages
    /*@Bean
    public ConsumerService receiver() {
        return new ConsumerService();
    }*/
}
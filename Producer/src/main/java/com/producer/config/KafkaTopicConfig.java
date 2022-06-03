package com.producer.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.producer.topic1.name}")
    private String topic1Name;

    @Value("${kafka.producer.topic1.partitions}")
    private Integer topic1Partitions;

    @Value("${kafka.producer.topic1.replicas}")
    private Integer topic1Replicas;

    @Value("${kafka.producer.topic2.name}")
    private String topic2Name;

    @Value("${kafka.producer.topic2.partitions}")
    private Integer topic2Partitions;

    @Value("${kafka.producer.topic2.replicas}")
    private Integer topic2Replicas;

    @Value("${kafka.producer.topic3.name}")
    private String topic3Name;

    @Value("${kafka.producer.topic3.partitions}")
    private Integer topic3Partitions;

    @Value("${kafka.producer.topic3.replicas}")
    private Integer topic3Replicas;

    @Value("${kafka.producer.partitions}")
    private Integer numPartitions;

    @Bean
    public NewTopic topic1()
    {
        return TopicBuilder.name(topic1Name).partitions(numPartitions).replicas(1).build();
    }
    @Bean
    public NewTopic topic2()
    {
        return TopicBuilder.name(topic2Name).partitions(numPartitions).replicas(1).build();
    }
    @Bean
    public NewTopic topic3()
    {
        return TopicBuilder.name(topic3Name).partitions(numPartitions).replicas(1).build();
    }
}

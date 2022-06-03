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

    @Value("${kafka.producer.topic4.name}")
    private String topic4Name;

    @Value("${kafka.producer.topic4.partitions}")
    private Integer topic4Partitions;

    @Value("${kafka.producer.topic4.replicas}")
    private Integer topic4Replicas;

    @Value("${kafka.producer.topic5.name}")
    private String topic5Name;

    @Value("${kafka.producer.topic5.partitions}")
    private Integer topic5Partitions;

    @Value("${kafka.producer.topic5.replicas}")
    private Integer topic5Replicas;

    @Value("${kafka.producer.topic6.name}")
    private String topic6Name;

    @Value("${kafka.producer.topic6.partitions}")
    private Integer topic6Partitions;

    @Value("${kafka.producer.topic6.replicas}")
    private Integer topic6Replicas;

    @Value("${kafka.producer.topic7.name}")
    private String topic7Name;

    @Value("${kafka.producer.topic7.partitions}")
    private Integer topic7Partitions;

    @Value("${kafka.producer.topic7.replicas}")
    private Integer topic7Replicas;
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

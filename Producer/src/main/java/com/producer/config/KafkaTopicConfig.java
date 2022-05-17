package com.producer.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    // ToDo : save topic names as constants in a separate file.

    public static final String Topic1 = "TopicMalone";
    @Bean
    public NewTopic weather()
    {
        return TopicBuilder.name("weather").partitions(3).build();
    }
    @Bean
    public NewTopic job()
    {
        return TopicBuilder.name("job").partitions(3).build();
    }
    @Bean
    public NewTopic stock()
    {
        return TopicBuilder.name("stock").partitions(3).build();
    }
}

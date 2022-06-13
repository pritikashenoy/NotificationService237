package com.consumer.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.Session;
import javax.mail.Transport;
import java.util.Map;
import java.util.Set;

@Service
public class ConsumerService {
    // To log the received messages
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerService.class);

    @Value("${kafka.consumer.group1.topic}")
    private String group1Topic;

    @Value("${kafka.consumer.group2.topic}")
    private String group2Topic;

    @Value("${kafka.consumer.group3.topic}")
    private String group3Topic;

    @Value("${kafka.consumer.group4.topic}")
    private String group4Topic;

    // To send mails for the notifications received by the consumer service
    @Autowired
    private MailService mailService;

    // Session and transport for the mail service
    private Session session;
    private Transport transport;

    // Topic to subscription list mapping
    private Map<String, Set<String>> subscriptions;

    // smtp configurations
    @Value("${spring.mail.host}")
    private String smtpHost;

    @Value("${spring.mail.username}")
    private String smtpUsername;

    @Value("${spring.mail.password}")
    private String smtpPassword;

    @Value("${spring.mail.port}")
    private String smtpPort;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private String smtpAuth;

    // Helper methods
    private void consume(String data, String topic) {
        LOGGER.info("Received message='{}'", topic + ": " + data);
        // TODO: send to slack

    }

    /* Listeners */
    // Listener for group 1
    @org.springframework.kafka.annotation.KafkaListener(topics = "${kafka.consumer.group1.topic}", concurrency = "${kafka.listener.concurrency}", groupId = "${kafka.consumer.group1.id}")
    public void group1Listener(String data) {
        consume(data, group1Topic);
    }

    // Listener for group 2
    @org.springframework.kafka.annotation.KafkaListener(topics = "${kafka.consumer.group2.topic}", concurrency = "${kafka.listener.concurrency}", groupId = "${kafka.consumer.group2.id}")
    public void group2Listener(String data) {
        consume(data, group2Topic);
    }

    // Listener for group 3
    @org.springframework.kafka.annotation.KafkaListener(topics = "${kafka.consumer.group3.topic}", concurrency = "${kafka.listener.concurrency}", groupId = "${kafka.consumer.group3.id}")
    public void group3Listener(String data) {
        consume(data, group3Topic);
    }

    // Listener for group 4
    @org.springframework.kafka.annotation.KafkaListener(topics = "${kafka.consumer.group4.topic}", concurrency = "${kafka.listener.concurrency}", groupId = "${kafka.consumer.group4.id}")
    public void group4Listener(String data) {
        consume(data, group4Topic);
    }

}



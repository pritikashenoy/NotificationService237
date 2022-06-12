package com.consumer.service;


import com.consumer.model.Mail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.kafka.event.*;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.core.io.*;
import java.util.*;
import java.net.*;
import java.io.*;
import javax.mail.*;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import java.util.stream.Collectors;
import java.nio.charset.*;

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

    @Value("${kafka.consumer.group5.topic}")
    private String group5Topic;

    @Value("${kafka.consumer.group6.topic}")
    private String group6Topic;

    @Value("${kafka.consumer.group7.topic}")
    private String group7Topic;

    // To send mails for the notifications received by the consumer service
    @Autowired
    private MailService mailService;

    // A session for the mail service
    // TBD: Even for multiple subscribers, this login will be same, only recipients vary, which can be set in mimemessage in MailService,
    // so this should work
    private Session session;
    private Transport transport;

    // Topic to subscription list mapping
    private Map<String, Set<String>> subscriptions;

    // Mail Service set up configurations
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

    private void consume(String data, String topic) {
        LOGGER.info("Received message='{}'", topic + ": " + data);

    }

    // Listener for group 5
    @org.springframework.kafka.annotation.KafkaListener(topics = "${kafka.consumer.group5.topic}", concurrency = "${kafka.consumer.group5.consumers}", groupId = "${kafka.consumer.group5.id}")
    public void group5Listener(String data) {
        consume(data, group5Topic);
    }

}



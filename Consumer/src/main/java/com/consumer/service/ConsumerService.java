package com.consumer.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.consumer.model.Mail;
import javax.sound.midi.Receiver;

@Component
public class ConsumerService {
    // To log the received messages
    private static final Logger LOGGER =
            LoggerFactory.getLogger(Receiver.class);

    @Value("${kafka.consumer.group1.topic}")
    private String group1Topic;

    @Value("${kafka.consumer.group2.topic}")
    private String group2Topic;

    @Value("${kafka.consumer.group3.topic}")
    private String group3Topic;

    // To send mails for the notifications received by the consumer service
    @Autowired
    private MailService mailService;

    private void consume(String data, String topic) {
        LOGGER.info("Received message='{}'", topic + ": " + data);
        Mail mail = new Mail();
        // TODO: Replace with config read from application.yaml
        mail.setMailFrom("cs237uci@gmail.com");
        mail.setMailTo("cs237uci@gmail.com");
        mail.setMailSubject(topic + "Notification");
        mail.setMailContent(data);
        // TODO: Fix connection pool
        // mailService.sendEmail(mail);
    }

    // Listener for group 1
    @org.springframework.kafka.annotation.KafkaListener(topics = "${kafka.consumer.group1.topic}", concurrency = "${kafka.consumer.group1.consumers}", groupId = "${kafka.consumer.group1.id}")
    public void group1Listener(String data) {consume(data, group1Topic);}

    // Listener for group 2
    @org.springframework.kafka.annotation.KafkaListener(topics = "${kafka.consumer.group2.topic}", concurrency = "${kafka.consumer.group2.consumers}", groupId = "${kafka.consumer.group2.id}")
    public void group2Listener(String data) {
        consume(data, group2Topic);
    }

    // Listener for group 3
    @org.springframework.kafka.annotation.KafkaListener(topics = "${kafka.consumer.group3.topic}", concurrency = "${kafka.consumer.group3.consumers}", groupId = "${kafka.consumer.group3.id}")
    public void group3Listener(String data) {
        consume(data, group3Topic);
    }
}



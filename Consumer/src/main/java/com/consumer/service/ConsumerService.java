package com.consumer.service;


import com.consumer.model.Mail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.event.*;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import java.util.Properties;
import java.net.*;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;


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

    // To send mails for the notifications received by the consumer service
    @Autowired
    private MailService mailService;

    // A session for the mail service
    // TBD: Even for multiple subscribers, this login will be same, only recipients vary, which can be set in mimemessage in MailService,
    // so this should work
    private Session session;
    private Transport transport;

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
    @EventListener(ConsumerStartedEvent.class)
    public void setUpTransport() throws Exception {
        Properties props = new Properties();
        props.put("mail.smtp.host", smtpHost); //SMTP Host
        props.put("mail.smtp.port", smtpPort); //TLS Port
        props.put("mail.smtp.auth", smtpAuth); //enable authentication
        props.put("mail.smtp.starttls.enable", smtpAuth); //enable STARTTLS
        final String fromEmail = smtpUsername + "@gmail.com";
        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, smtpPassword);
            }
        };
        Session session = Session.getInstance(props, auth);
        session.setDebug(true);
        try {
            LOGGER.debug("Creating transport for the consumer service" + " " + fromEmail);
            transport = session.getTransport("smtp");
            LOGGER.debug("Creating transport connection for the consumer service");
            transport.connect(smtpHost, 587, smtpUsername, smtpPassword);
        } catch(Exception e)
        {
            LOGGER.debug("Transport creation OR connection failed");
            e.printStackTrace();
        }
    }

    @EventListener(ConsumerStoppedEvent.class)
    public void closeTransport() throws MessagingException {
        LOGGER.debug("Closing transport for the consumer service");
        transport.close();
    }

    private void consume(String data, String topic) {
        LOGGER.info("Received message='{}'", topic + ": " + data);
        Mail mail = new Mail();
        // TODO: Replace with config read from application.yaml
        mail.setMailFrom("cs237uci@gmail.com");
        mail.setMailTo("cs237uci@gmail.com");
        mail.setMailSubject(topic + "Notification");
        mail.setMailContent(data);
        mailService.sendEmail(mail, session, transport);
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



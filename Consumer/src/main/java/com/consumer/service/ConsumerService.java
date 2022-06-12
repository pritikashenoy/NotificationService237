package com.consumer.service;


import com.consumer.model.Mail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.mail.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

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
        Mail mail = new Mail();
        mail.setMailFrom("pseudoresh@gmail.com");
        // Get the set of subscribers for this topic
        List<String> subsList = new ArrayList<String>(subscriptions.get(topic));
        mail.setMailTo(subsList);
        mail.setMailSubject(topic + " Notification");
        mail.setMailContent(data);
        mailService.sendEmail(mail, session, transport);
    }

    private void setUpSubscriptions() throws Exception {
        LOGGER.debug("In setup subscriptions phase");
        try {
            subscriptions = new HashMap<String, Set<String>>();
            InputStream subInputStream = new ClassPathResource("classpath:subscribers.txt").getInputStream();
            InputStream topicInputStream = new ClassPathResource("classpath:topics.txt").getInputStream();
            List<String> subscribers = new BufferedReader(new InputStreamReader(subInputStream,
                    StandardCharsets.UTF_8)).lines().collect(Collectors.toList());
            List<String> topics = new BufferedReader(new InputStreamReader(topicInputStream,
                    StandardCharsets.UTF_8)).lines().collect(Collectors.toList());

            Random random = new Random();
            int min = 2, max = subscribers.size();
            for (String topic : topics) {
                int numSubs = random.nextInt(max - min) + min;
                Collections.shuffle(subscribers);
                Set<String> randomSubsSet = new HashSet<String>(subscribers.subList(0, numSubs - 1));
                subscriptions.put(topic, randomSubsSet);
            }
            LOGGER.debug("Created topic subscriptions");
        } catch(Exception e)
        {
            LOGGER.debug("Topic subscription mapping failed");
            e.printStackTrace();
        }
    }

    private void setUpTransport() throws Exception {
        LOGGER.debug("In setup transport phase");
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
        session = Session.getInstance(props, auth);
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
    @EventListener(ApplicationStartedEvent.class)
    public void setUp() throws Exception {
        LOGGER.debug("In setup phase");
        // 1. Create subscription list
        setUpSubscriptions();
        // 2. Set up transport to send mails to the subscribers
        setUpTransport();
    }

    //@EventListener(ConsumerStoppedEvent.class)
    @EventListener(ContextClosedEvent.class)
    public void destroy() throws MessagingException {
        LOGGER.debug("Closing transport for the consumer service");
        transport.close();
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



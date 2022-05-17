package com.consumer.service;


import com.consumer.model.Mail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sound.midi.Receiver;

@Component
public class ConsumerService {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(Receiver.class);

    @Autowired
    private MailService mailService;
    @org.springframework.kafka.annotation.KafkaListener(topics = {"weather","stock","job"}, groupId = "groupid")
    public void listener(String data) {
        LOGGER.info("received message='{}'", data);
        Mail mail = new Mail();
        mail.setMailFrom("cs237uci@gmail.com");
        mail.setMailTo("cs237uci@gmail.com");
        mail.setMailSubject("New Notification");
        mail.setMailContent(data);
        mailService.sendEmail(mail);
    }
}



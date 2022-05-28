package com.consumer.service;

import com.consumer.model.Mail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import javax.mail.Session;
import javax.mail.Transport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Decide batch size, load balancing and time period to send
@Service("mailService")
public class MailServiceImpl implements MailService {
    // To log the received messages
    private static final Logger LOGGER = LoggerFactory.getLogger(MailServiceImpl.class);

    public void sendEmail(Mail mail, Session session,Transport transport){
        MimeMessage mimeMessage = new MimeMessage(session);

        try {
            // TODO: Remove hardcoded
            mimeMessage.setFrom(new InternetAddress(mail.getMailFrom(), "Peter Jhon"));
            // TODO: Use recipients for multiple subscribers
            mimeMessage.addRecipient(MimeMessage.RecipientType.TO, InternetAddress.parse(mail.getMailTo())[0]);
            mimeMessage.setSubject(mail.getMailSubject());
            mimeMessage.setText(mail.getMailContent());
            LOGGER.debug("Sending email for " + mail.getMailSubject() + " " + mail.getMailContent());
            transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
        } catch (Exception e) {
            LOGGER.debug("Sending failed for " + mail.getMailSubject() + " " + mail.getMailContent());
            e.printStackTrace();
        }
    }

}

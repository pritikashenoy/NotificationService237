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
        // TODO: Replace this to get the subscriber list for the topic, or set it in mail in the caller
        String recipients = String.join(",", mail.getMailTo());
        //String recipients = mail.getMailTo() + "," + "felicia.larson@hotmail.com";
        try {
            // TODO: Remove hardcoded
            mimeMessage.setFrom(new InternetAddress(mail.getMailFrom(), "Peter Jhon"));
            mimeMessage.addRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse(recipients));
            //mimeMessage.addRecipient(MimeMessage.RecipientType.TO, InternetAddress.parse(rec)[0]);
            mimeMessage.setSubject(mail.getMailSubject());
            mimeMessage.setText(mail.getMailContent());
            LOGGER.debug("Sending email for " + mail.getMailSubject() + " " + mail.getMailContent());
            transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
        } catch (Exception e) {
            LOGGER.error("Sending failed for " + mail.getMailSubject() + " " + mail.getMailContent());
            e.printStackTrace();
        }
    }

}

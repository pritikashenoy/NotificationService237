package com.consumer.service;

import com.consumer.model.Mail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

// TODO: Decide batch size, load balancing and time period to send
@Service("mailService")
public class MailServiceImpl implements MailService {
    // To log the received messages
    private static final Logger LOGGER = LoggerFactory.getLogger(MailServiceImpl.class);

    public void sendEmail(Mail mail, Session session,Transport transport){
        MimeMessage mimeMessage = new MimeMessage(session);
        String recipients = String.join(",", mail.getMailTo());
        try {
            mimeMessage.setFrom(new InternetAddress(mail.getMailFrom(), "Reshma Thomas"));
            mimeMessage.addRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse(recipients));
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

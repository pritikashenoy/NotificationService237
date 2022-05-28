package com.consumer.service;

import com.consumer.model.Mail;
import javax.mail.Transport;
import javax.mail.Session;

public interface MailService {
    public void sendEmail(Mail mail, Session session, Transport transport);

}

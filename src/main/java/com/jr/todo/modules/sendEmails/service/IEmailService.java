package com.jr.todo.modules.sendEmails.service;

import java.util.Map;
import jakarta.mail.MessagingException;

public interface IEmailService {
    void sendEmail(String recipient, String subject, String templateName, Map<String, Object> variables)
            throws MessagingException;
}

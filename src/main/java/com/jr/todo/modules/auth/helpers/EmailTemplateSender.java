package com.jr.todo.modules.auth.helpers;

import java.util.Map;
import org.springframework.stereotype.Service;
import com.jr.todo.modules.sendEmails.service.IEmailService;
import jakarta.mail.MessagingException;

@Service
public class EmailTemplateSender {

    private final IEmailService emailService;

    public EmailTemplateSender(IEmailService emailService) {
        this.emailService = emailService;
    }

    public void send(String recipient, String subject,
            String templateName, Map<String, Object> variables) {
        try {
            emailService.sendEmail(recipient, subject, templateName, variables);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar email de " + templateName, e);
        }
    }
}

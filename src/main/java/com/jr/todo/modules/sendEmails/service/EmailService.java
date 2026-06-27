package com.jr.todo.modules.sendEmails.service;

import java.util.HashMap;
import java.util.Map;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import com.jr.todo.modules.sendEmails.dto.EmailActivationDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService implements IEmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    public EmailService(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendEmail(EmailActivationDto email) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(email.getRecipient());
        helper.setSubject(email.getSubject());

        Context context = new Context();

        Map<String, Object> variables = new HashMap<>();
        variables.put("userName", email.getUserName());
        variables.put("activationUrl", email.getActivationUrl());
        variables.put("expirationHours", email.getExpirationHours());
        variables.put("currentYear", email.getCurrentYear());

        context.setVariables(variables);

        String contentHTML = templateEngine.process("ActivationEmail", context);
        helper.setText(contentHTML, true);

        javaMailSender.send(message);
    }
}

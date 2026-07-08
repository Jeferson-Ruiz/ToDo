package com.jr.todo.modules.auth.helpers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;
import com.jr.todo.modules.auth.dto.SendEmail;
import com.jr.todo.modules.auth.entity.AccountActivationToken;
import com.jr.todo.modules.auth.repository.AccountActivationTokenRepository;
import com.jr.todo.modules.sendEmails.service.IEmailService;
import com.jr.todo.modules.user.entity.User;
import jakarta.mail.MessagingException;

@Component
public class SendActivationEmail {

    private final IEmailService emailService;
    private final AccountActivationTokenRepository activationTokenRepository;

    public SendActivationEmail(IEmailService emailService, AccountActivationTokenRepository activationTokenRepository) {
        this.emailService = emailService;
        this.activationTokenRepository = activationTokenRepository;
    }

    public void sendActivationEmail(User user) {
        String token = generateActivationToken(user);
        SendEmail emailDto = buildEmail(user, token);

        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("username", emailDto.getUserName());
            variables.put("activationUrl", emailDto.getActivationUrl());
            variables.put("expirationHours", emailDto.getExpirationHours());
            variables.put("currentYear", emailDto.getCurrentYear());

            emailService.sendEmail(emailDto.getRecipient(), emailDto.getSubject(), "ActivationEmail", variables);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el email de activación", e);
        }
    }

    private String generateActivationToken(User user) {
        String token = UUID.randomUUID().toString();
        AccountActivationToken accountActivationToken = new AccountActivationToken();
        accountActivationToken.setToken(token);
        accountActivationToken.setUser(user);
        accountActivationToken.setExpiresAt(LocalDateTime.now().plusHours(24));
        accountActivationToken.setUsed(false);
        activationTokenRepository.save(accountActivationToken);
        user.setAccountActivationToken(accountActivationToken);
        return token;
    }

    private SendEmail buildEmail(User user, String token) {
        SendEmail emailDto = new SendEmail();
        emailDto.setRecipient(user.getEmail());
        emailDto.setSubject("Activacion de cuenta");
        emailDto.setUserName(user.getUsername());
        emailDto.setActivationUrl("http://localhost:8081/auth/activation?token=" + token);
        emailDto.setExpirationHours("24");
        emailDto.setCurrentYear(String.valueOf(LocalDate.now().getYear()));
        return emailDto;
    }

}

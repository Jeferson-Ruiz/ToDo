package com.jr.todo.modules.auth.helpers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;
import com.jr.todo.modules.sendEmails.dto.SendEmail;
import com.jr.todo.modules.auth.entity.AccountRecoveryToken;
import com.jr.todo.modules.auth.repository.AccountRecoveryTokenRepository;
import com.jr.todo.modules.sendEmails.service.IEmailService;
import com.jr.todo.modules.user.entity.User;
import jakarta.mail.MessagingException;

@Component
public class SendRecoveryAccount {

    private final IEmailService emailService;
    private final AccountRecoveryTokenRepository accountRecoveryTokenRepository;

    public SendRecoveryAccount(IEmailService emailService,
            AccountRecoveryTokenRepository accountRecoveryTokenRepository) {
        this.emailService = emailService;
        this.accountRecoveryTokenRepository = accountRecoveryTokenRepository;
    }

    public void sendEmailActivation(User user) {
        String token = generateActivationToken(user);
        SendEmail sendEmail = buildEmail(user, token);

        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("userName", sendEmail.getUserName());
            variables.put("formUrl", sendEmail.getActivationUrl());
            variables.put("expirationHours", sendEmail.getExpirationHours());
            variables.put("currentYear", sendEmail.getCurrentYear());

            emailService.sendEmail(sendEmail.getRecipient(), sendEmail.getSubject(), "AccountRecovery", variables);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el email de activación", e);
        }
    }

    private String generateActivationToken(User user) {
        String token = UUID.randomUUID().toString();
        AccountRecoveryToken accountRecoveryToken = new AccountRecoveryToken();
        accountRecoveryToken.setToken(token);
        accountRecoveryToken.setUser(user);
        accountRecoveryToken.setExpiresAt(LocalDateTime.now().plusHours(2));
        accountRecoveryToken.setUsed(false);
        accountRecoveryTokenRepository.save(accountRecoveryToken);
        user.setAccountRecoveryToken(accountRecoveryToken);
        return token;

    }

    private SendEmail buildEmail(User user, String token) {
        SendEmail emailDto = new SendEmail();
        emailDto.setRecipient(user.getEmail());
        emailDto.setSubject("Recuperacion de Cuenta");
        emailDto.setUserName(user.getUsername());
        emailDto.setActivationUrl("http://localhost:8081/recovery/recoveryform?token=" + token);
        emailDto.setExpirationHours("2");
        emailDto.setCurrentYear(String.valueOf(LocalDate.now().getYear()));
        return emailDto;
    }
}
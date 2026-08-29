package com.jr.todo.modules.auth.helpers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.jr.todo.modules.auth.entity.AccountRecoveryToken;
import com.jr.todo.modules.auth.repository.AccountRecoveryTokenRepository;
import com.jr.todo.modules.user.entity.User;

@Service
public class SendRecoveryAccount {

    private final AccountRecoveryTokenRepository accountRecoveryTokenRepository;
    private final EmailTemplateSender emailSender;
    private final String frontendUrl;

    public SendRecoveryAccount(AccountRecoveryTokenRepository accountRecoveryTokenRepository,
            EmailTemplateSender emailSender,
            @Value("${app.frontend.url:http://localhost:5173}") String frontendUrl) {
        this.accountRecoveryTokenRepository = accountRecoveryTokenRepository;
        this.emailSender = emailSender;
        this.frontendUrl = frontendUrl;
    }

    public void sendEmailActivation(User user) {
        String token = UUID.randomUUID().toString();
        AccountRecoveryToken recoveryToken = new AccountRecoveryToken();
        recoveryToken.setToken(token);
        recoveryToken.setUser(user);
        recoveryToken.setExpiresAt(LocalDateTime.now().plusHours(2));
        recoveryToken.setUsed(false);

        accountRecoveryTokenRepository.save(recoveryToken);
        user.setAccountRecoveryToken(recoveryToken);

        // variables - formUrl apunta al frontend (SPA) que consume POST /recovery/api/reset-password
        Map<String, Object> variables = new HashMap<>();
        variables.put("userName", user.getUsername());
        variables.put("formUrl", frontendUrl + "/reset-password?token=" + token);
        variables.put("expirationHours", "2");
        variables.put("currentYear", String.valueOf(LocalDate.now().getYear()));

        emailSender.send(user.getEmail(), "Recuperacion de Cuenta", "AccountRecovery", variables);
    }
}

package com.jr.todo.modules.auth.helpers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import com.jr.todo.modules.auth.entity.AccountActivationToken;
import com.jr.todo.modules.auth.repository.AccountActivationTokenRepository;
import com.jr.todo.modules.user.entity.User;

@Service
public class SendActivationEmail {

    private final AccountActivationTokenRepository activationTokenRepository;
    private final EmailTemplateSender emailSender;

    public SendActivationEmail(AccountActivationTokenRepository activationTokenRepository,
            EmailTemplateSender emailSender) {
        this.activationTokenRepository = activationTokenRepository;
        this.emailSender = emailSender;
    }

    public void sendActivationEmail(User user) {
        String token = UUID.randomUUID().toString();

        AccountActivationToken activationToken = new AccountActivationToken();
        activationToken.setToken(token);
        activationToken.setUser(user);
        activationToken.setExpiresAt(LocalDateTime.now().plusHours(24));
        activationToken.setUsed(false);

        activationTokenRepository.save(activationToken);
        user.setAccountActivationToken(activationToken);

        // variables de la plantilla
        Map<String, Object> variables = new HashMap<>();
        variables.put("username", user.getUsername());
        variables.put("activationUrl", "http://localhost:8081/auth/activation?token=" + token);
        variables.put("expirationHours", "24");
        variables.put("currentYear", String.valueOf(LocalDate.now().getYear()));

        emailSender.send(user.getEmail(), "Activacion de cuenta", "ActivationEmail", variables);
    }
}

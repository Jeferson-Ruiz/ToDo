package com.jr.todo.modules.auth.service;

import java.time.LocalDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.jr.todo.modules.auth.dto.ResetPasswordDto;
import com.jr.todo.modules.auth.entity.AccountRecoveryToken;
import com.jr.todo.modules.auth.helpers.SendRecoveryAccount;
import com.jr.todo.modules.auth.repository.AccountRecoveryTokenRepository;
import org.springframework.stereotype.Service;
import com.jr.todo.modules.user.entity.User;
import com.jr.todo.modules.user.repository.UserRepository;
import com.jr.todo.util.UserSearchMethods;
import jakarta.persistence.EntityNotFoundException;

@Service
public class RecoveryService implements IRecoveryService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserSearchMethods userSearchMethods;
    private final AccountRecoveryTokenRepository accountRecoveryTokenRepository;
    private final SendRecoveryAccount sendRecoveryAccount;

    public RecoveryService(UserRepository userRepository, PasswordEncoder passwordEncoder,
            UserSearchMethods userSearchMethods, AccountRecoveryTokenRepository accountRecoveryTokenRepository,
            SendRecoveryAccount sendRecoveryAccount) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userSearchMethods = userSearchMethods;
        this.accountRecoveryTokenRepository = accountRecoveryTokenRepository;
        this.sendRecoveryAccount = sendRecoveryAccount;
    }

    @Override
    public String initiateRecovery(String email) {
        User user = userSearchMethods.findByEmail(email);
        accountRecoveryTokenRepository.findByUser(user).ifPresent(accountRecoveryTokenRepository::delete);
        sendRecoveryAccount.sendEmailActivation(user);

        return "Email de recuperación enviado";
    }

    @Override
    public String updatePassword(String token, ResetPasswordDto request) {
        AccountRecoveryToken recoveryToken = accountRecoveryTokenRepository.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException("token de activacion no valido"));

        if (recoveryToken.isUsed()) {
            throw new IllegalStateException("El token ya fue utilizado");
        }

        if (LocalDateTime.now().isAfter(recoveryToken.getExpiresAt())) {
            throw new IllegalStateException("El token ha expirado, solicita uno nuevo");
        }

        if (!request.newPassword().equals(request.repeatPassword())) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }

        User user = recoveryToken.getUser();
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        recoveryToken.setUsed(true);
        accountRecoveryTokenRepository.save(recoveryToken);

        return "Contraseña actualizada correctamente";
    }

}

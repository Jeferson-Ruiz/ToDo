package com.jr.todo.modules.auth.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.jr.todo.dto.AuthRequest;
import com.jr.todo.dto.AuthResponse;
import com.jr.todo.dto.UserCreateDto;
import com.jr.todo.enums.Role;
import com.jr.todo.modules.auth.entity.AccountActivationToken;
import com.jr.todo.modules.auth.helpers.SendActivationEmail;
import com.jr.todo.modules.auth.helpers.SendRecoveryAccount;
import com.jr.todo.modules.auth.repository.AccountActivationTokenRepository;
import com.jr.todo.modules.auth.repository.AccountRecoveryTokenRepository;
import com.jr.todo.modules.user.entity.User;
import com.jr.todo.modules.user.repository.UserRepository;
import com.jr.todo.util.UserSearchMethods;
import com.jr.todo.util.UserValidationHelper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService implements IAuthService {

  private final UserRepository userRepository;
  private final IJwtService jwtService;
  private final PasswordEncoder passwordEncoder;
  private final TokenBlacklistService tokenBlacklistService;
  private final UserValidationHelper userValidation;
  private final UserSearchMethods userSearchMethods;
  private final AccountActivationTokenRepository accountActivationTokenRepository;
  private final AccountRecoveryTokenRepository accountRecoveryTokenRepository;
  private final SendActivationEmail sendActivationEmail;
  private final SendRecoveryAccount sendRecoveryAccount;

  public AuthService(UserRepository userRepository, IJwtService jwtService, PasswordEncoder passwordEncoder,
      TokenBlacklistService tokenBlacklistService, UserValidationHelper userValidation,
      UserSearchMethods userSearchMethods, AccountActivationTokenRepository accountActivationTokenRepository,
      AccountRecoveryTokenRepository accountRecoveryTokenRepository, SendActivationEmail sendActivationEmail,
      SendRecoveryAccount sendRecoveryAccount) {
    this.userRepository = userRepository;
    this.jwtService = jwtService;
    this.passwordEncoder = passwordEncoder;
    this.tokenBlacklistService = tokenBlacklistService;
    this.userValidation = userValidation;
    this.userSearchMethods = userSearchMethods;
    this.accountActivationTokenRepository = accountActivationTokenRepository;
    this.accountRecoveryTokenRepository = accountRecoveryTokenRepository;
    this.sendActivationEmail = sendActivationEmail;
    this.sendRecoveryAccount = sendRecoveryAccount;
  }

  public AuthResponse login(AuthRequest request) {
    User user = userSearchMethods.findByEmail(request.email());
    validatePassword(request.password(), user.getPassword());

    userValidation.isEnabled(request.email());
    return new AuthResponse(jwtService.getToken(user));
  }

  @Override
  public String register(UserCreateDto request) {

    String username = request.username().strip();
    String email = request.email().strip();

    userValidation.validateUsername(username);
    userValidation.validateEmail(email);

    User user = request.toEntity();
    user.setUsername(username);
    user.setPassword(passwordEncoder.encode(request.password()));
    user.setRole(Role.USER);
    user.setRegistrationDte(LocalDate.now());
    userRepository.save(user);
    sendActivationEmail.sendActivationEmail(user);
    return "Registro exitoso, revisa tu email para activar tu cuenta";
  }

  @Override
  public void logout(String authHeader) {
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);
      tokenBlacklistService.blackListToken(token);
    }
  }

  @Override
  public void activateAccount(String token) {
    AccountActivationToken activationToken = accountActivationTokenRepository.findByToken(token)
        .orElseThrow(() -> new EntityNotFoundException("token de activacion no valido"));

    if (activationToken.isUsed()) {
      throw new IllegalStateException("El token ya fue utilizado");
    }

    if (LocalDateTime.now().isAfter(activationToken.getExpiresAt())) {
      throw new IllegalStateException("El token ha expirado, solicita uno nuevo");
    }

    User user = activationToken.getUser();
    user.setEnabled(true);
    userRepository.save(user);

    activationToken.setUsed(true);
    accountActivationTokenRepository.save(activationToken);
  }

  @Override
  @Transactional
  public String resendActivationEmail(UserCreateDto userRequest) {
    User user = userSearchMethods.findByEmail(userRequest.email());

    if (userRepository.isUserEnabled(user.getEmail())) {
      throw new IllegalStateException("La cuenta ya esta activada");
    }
    accountActivationTokenRepository.findByUser(user).ifPresent(
        accountActivationTokenRepository::delete);
    sendActivationEmail.sendActivationEmail(user);

    return "Email enviado con exito";
  }

  @Override
  public String initiateRecovery(String email) {
    User user = userSearchMethods.findByEmail(email);
    accountRecoveryTokenRepository.findByUser(user).ifPresent(accountRecoveryTokenRepository::delete);
    sendRecoveryAccount.sendEmailActivation(user);

    return "Email de recuperación enviado";
  }

  // helpers
  private void validatePassword(String password, String encodedPassword) {
    if (!passwordEncoder.matches(password, encodedPassword)) {
      throw new BadCredentialsException("Contraseña incorrecta");
    }
  }
}

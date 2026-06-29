package com.jr.todo.modules.auth.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.jr.todo.enums.Role;
import com.jr.todo.modules.auth.entity.AccountActivationToken;
import com.jr.todo.modules.auth.repository.AccountActivationTokenRepository;
import com.jr.todo.modules.sendEmails.dto.EmailActivationDto;
import com.jr.todo.modules.sendEmails.service.IEmailService;
import com.jr.todo.modules.user.dto.AuthRequest;
import com.jr.todo.modules.user.dto.AuthResponse;
import com.jr.todo.modules.user.dto.UserCreateDto;
import com.jr.todo.modules.user.entity.User;
import com.jr.todo.modules.user.repository.UserRepository;
import com.jr.todo.util.UserSearchMethods;
import com.jr.todo.util.UserValidationHelper;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;

@Service
public class AuthService implements IAuthService {

  private final UserRepository userRepository;
  private final IJwtService jwtService;
  private final PasswordEncoder passwordEncoder;
  private final TokenBlacklistService tokenBlacklistService;
  private final UserValidationHelper userValidation;
  private final UserSearchMethods userSearchMethods;
  private final AccountActivationTokenRepository activationTokenRepository;
  private final IEmailService emailService;

  public AuthService(UserRepository userRepository, IJwtService jwtService, PasswordEncoder passwordEncoder,
      TokenBlacklistService tokenBlacklistService, UserValidationHelper userValidation,
      UserSearchMethods userSearchMethods, AccountActivationTokenRepository activationTokenRepository,
      IEmailService emailService) {
    this.userRepository = userRepository;
    this.jwtService = jwtService;
    this.passwordEncoder = passwordEncoder;
    this.tokenBlacklistService = tokenBlacklistService;
    this.userValidation = userValidation;
    this.userSearchMethods = userSearchMethods;
    this.activationTokenRepository = activationTokenRepository;
    this.emailService = emailService;
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
    sendActivationEmail(user);
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
    AccountActivationToken activationToken = activationTokenRepository.findByToken(token)
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
    activationTokenRepository.save(activationToken);
  }

  // helpers
  private void sendActivationEmail(User user) {
    String token = generateActivationToken(user);
    EmailActivationDto emailDto = buildEmail(user, token);
    try {
      emailService.sendEmail(emailDto);
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
    return token;
  }

  private EmailActivationDto buildEmail(User user, String token) {
    EmailActivationDto emailDto = new EmailActivationDto();
    emailDto.setRecipient(user.getEmail());
    emailDto.setSubject("Activacion de cuenta");
    emailDto.setUserName(user.getUsername());
    emailDto.setActivationUrl("http://localhost:8081/auth/activation?token=" + token);
    emailDto.setExpirationHours("24");
    emailDto.setCurrentYear(String.valueOf(LocalDate.now().getYear()));
    return emailDto;
  }

  private void validatePassword(String password, String encodedPassword) {
    if (!passwordEncoder.matches(password, encodedPassword)) {
      throw new BadCredentialsException("Contraseña incorrecta");
    }
  }
}

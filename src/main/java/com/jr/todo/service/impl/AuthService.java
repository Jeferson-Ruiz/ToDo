package com.jr.todo.service.impl;

import java.time.LocalDate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.jr.todo.dto.user.AuthRequest;
import com.jr.todo.dto.user.AuthResponse;
import com.jr.todo.dto.user.UserCreateDto;
import com.jr.todo.entity.User;
import com.jr.todo.entity.enums.Role;
import com.jr.todo.repository.UserRepository;
import com.jr.todo.service.IAuthService;
import com.jr.todo.service.IJwtService;
import com.jr.todo.util.UserValidationHelper;
import jakarta.persistence.EntityNotFoundException;

@Service
public class AuthService implements IAuthService {

  private final UserRepository userRepository;
  private final IJwtService jwtService;
  private final PasswordEncoder passwordEncoder;
  private final TokenBlacklistService tokenBlacklistService;
  private final UserValidationHelper userValidation;

  public AuthService(UserRepository userRepository, IJwtService jwtService, PasswordEncoder passwordEncoder,
      TokenBlacklistService tokenBlacklistService, UserValidationHelper userValidation) {
    this.userRepository = userRepository;
    this.jwtService = jwtService;
    this.passwordEncoder = passwordEncoder;
    this.tokenBlacklistService = tokenBlacklistService;
    this.userValidation = userValidation;
  }

  public AuthResponse login(AuthRequest request) {
    User user = findByEmail(request.email());
    validatePassword(request.password(), user.getPassword());

    return new AuthResponse(jwtService.getToken(user));
  }

  public AuthResponse register(UserCreateDto request) {
    String username = request.username().strip();
    String email = request.email().strip();

    userValidation.validateUsername(username);
    userValidation.validateEmail(email);

    User user = request.toEntity();
    user.setUsername(username);
    user.setPassword(passwordEncoder.encode(request.password()));
    user.setRole(Role.USER);
    user.setEnabled(false);
    user.setRegistrationDte(LocalDate.now());
    userRepository.save(user);

    return new AuthResponse(jwtService.getToken(user));
  }

  public void logout(String authHeader) {
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);
      tokenBlacklistService.blackListToken(token);
    }
  }

  // helpers
  private User findByEmail(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
    return user;
  }

  private void validatePassword(String password, String encodedPassword) {
    if (!passwordEncoder.matches(password, encodedPassword)) {
      throw new BadCredentialsException("Contraseña incorrecta");
    }
  }
}

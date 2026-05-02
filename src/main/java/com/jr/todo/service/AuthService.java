package com.jr.todo.service;

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

import jakarta.persistence.EntityNotFoundException;

@Service
public class AuthService implements IAuthService {

  private final UserRepository userRepository;
  private final IJwtService jwtService;
  private final PasswordEncoder passwordEncoder;

  public AuthService(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      JwtService jwtService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  public AuthResponse login(AuthRequest request) {
    User user = findByEmail(request.email());
    validatePassword(request.password(), user.getPassword());

    return new AuthResponse(jwtService.getToken(user));
  }

  public AuthResponse register(UserCreateDto request) {

    String username = request.username().strip();
    String email = request.email().strip();
    validateInformation(username, email);
    User user = request.toEntity();
    user.setUsername(username);
    user.setPassword(passwordEncoder.encode(request.password()));
    user.setRole(Role.USER);
    user.setRegistrationDte(LocalDate.now());
    userRepository.save(user);

    return new AuthResponse(jwtService.getToken(user));
  }

  // helpers
  private void validateInformation(String name, String email) {
    if (userRepository.existsByName(name)) {
      throw new IllegalArgumentException("username ya registrado");
    }
    if (userRepository.existByEmail(email)) {
      throw new IllegalArgumentException("El Email ya esta registrado");
    }
  }

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

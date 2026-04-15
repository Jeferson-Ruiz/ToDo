package com.jr.todo.service;

import java.time.LocalDate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.jr.todo.dto.auth.AuthResponse;
import com.jr.todo.dto.auth.LoginRequestDto;
import com.jr.todo.dto.user.UserCreateDto;
import com.jr.todo.entity.User;
import com.jr.todo.entity.enums.Role;
import com.jr.todo.repository.UserRepository;

@Service
public class AuthService implements IAuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public AuthService(UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      JwtService jwtService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  public AuthResponse login(LoginRequestDto request) {
    return null;
  }

  public AuthResponse registration(UserCreateDto request) {
    String username = request.username().strip();
    String email = request.email().strip();
    validateInformation(username, email);
    User user = request.toEntity();
    user.setUsername(username);
    user.setPassword(passwordEncoder.encode(request.password()));
    user.setRole(Role.USER);
    user.setRegistrationDte(LocalDate.now());

    userRepository.save(user);

    return new AuthResponse(jwtService.getToke(user));
  }

  private void validateInformation(String name, String email) {
    if (userRepository.existsByName(name)) {
      throw new IllegalArgumentException("username ya registrado");
    }
    if (userRepository.existByEmail(email)) {
      throw new IllegalArgumentException("El Email ya esta registrado");
    }
  }
}

package com.jr.todo.service;

import java.time.LocalDate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.jr.todo.dto.user.UserCreateDto;
import com.jr.todo.dto.user.UserResponseDto;
import com.jr.todo.entity.User;
import com.jr.todo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService implements IUserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public UserResponseDto createUser(UserCreateDto userDto) {
    String username = userDto.username().strip();
    String email = userDto.email().strip();
    validateInformation(username, email);
    User user = userDto.toEntity();
    user.setUsername(username);
    user.setPassword(passwordEncoder.encode(userDto.password()));
    user.setRegistrationDte(LocalDate.now());
    return UserResponseDto.toDto(userRepository.save(user));
  }

  public void updatePasswod(Long id, String oldPassword, String newPassword) {
    User user = findUserById(id);

    if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
      throw new IllegalArgumentException("Error de validacion de contraseña");
    }
    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);
  }

  public void updateEnable(Long id, boolean enable) {
    User user = findUserById(id);

    if (user.isEnable() == enable) {
      throw new IllegalArgumentException(" El usuario ya se encuentra desactivado");
    }
    user.setEnable(enable);
    userRepository.save(user);
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

  private User findUserById(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
    return user;
  }
}

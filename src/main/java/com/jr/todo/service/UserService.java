package com.jr.todo.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.jr.todo.dto.UserDto;
import com.jr.todo.entity.User;
import com.jr.todo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public UserDto createUser(UserDto userDto) {
    String username = userDto.username().strip();
    existUser(username);
    User user = userDto.toEntity();
    user.setUsername(username);
    user.setPassword(passwordEncoder.encode(userDto.password()));
    return UserDto.toDto(userRepository.save(user));
  }

  public void updatePasswod(Long id, String oldPassword, String newPassword) {
    User user = findUserById(id);

    if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
      throw new IllegalArgumentException("Error de validacion de contraseña");
    }
    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);
  }

  // helpers
  private void existUser(String name) {
    if (userRepository.existsByName(name)) {
      throw new IllegalArgumentException("username ya registrado");
    }
  }

  private User findUserById(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
    return user;
  }
}

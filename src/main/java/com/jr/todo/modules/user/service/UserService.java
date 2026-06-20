package com.jr.todo.modules.user.service;

import java.time.LocalDate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.jr.todo.modules.user.dto.UserCreateDto;
import com.jr.todo.modules.user.dto.UserResponseDto;
import com.jr.todo.modules.user.entity.User;
import com.jr.todo.modules.user.repository.UserRepository;
import com.jr.todo.util.UserValidationHelper;

@Service
public class UserService implements IUserService {

  private final UserValidationHelper userValidation;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserValidationHelper userValidation, UserRepository userRepository,
      PasswordEncoder passwordEncoder) {
    this.userValidation = userValidation;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public UserResponseDto createUser(UserCreateDto userDto) {
    String username = userDto.username().strip();
    String email = userDto.email().strip();

    userValidation.validateUsername(username);
    userValidation.validateEmail(email);

    User user = userDto.toEntity();
    user.setUsername(username);
    user.setPassword(passwordEncoder.encode(userDto.password()));
    user.setEnabled(false);
    user.setRegistrationDte(LocalDate.now());

    return UserResponseDto.toDto(userRepository.save(user));
  }

  public void updatePasswod(Long id, String oldPassword, String newPassword) {
    User user = userValidation.findUserById(id);

    if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
      throw new IllegalArgumentException("Error de validacion de contraseña");
    }
    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);
  }

  public void updateEnable(Long id, boolean enable) {
    User user = userValidation.findUserById(id);

    if (user.isEnabled() == enable) {
      throw new IllegalArgumentException(" El usuario ya se encuentra desactivado");
    }
    user.setEnabled(enable);
    userRepository.save(user);
  }

}

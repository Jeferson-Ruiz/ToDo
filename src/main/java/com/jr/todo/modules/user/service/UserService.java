package com.jr.todo.modules.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.jr.todo.modules.user.entity.User;
import com.jr.todo.modules.user.repository.UserRepository;
import com.jr.todo.util.UserSearchMethods;

@Service
public class UserService implements IUserService {

  private final UserSearchMethods userSearchMethods;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserSearchMethods userSearchMethods,
      UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userSearchMethods = userSearchMethods;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void updatePasswod(String email, String oldPassword, String newPassword) {
    User user = userSearchMethods.findByEmail(email);

    if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
      throw new IllegalArgumentException("Error de validacion de contraseña");
    }
    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);
  }

}

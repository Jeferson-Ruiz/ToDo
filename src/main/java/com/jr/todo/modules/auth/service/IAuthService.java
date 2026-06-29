package com.jr.todo.modules.auth.service;

import com.jr.todo.dto.AuthRequest;
import com.jr.todo.dto.AuthResponse;
import com.jr.todo.dto.UserCreateDto;

public interface IAuthService {
  AuthResponse login(AuthRequest request);

  String register(UserCreateDto request);

  void logout(String authHeader);

  void activateAccount(String token);
}

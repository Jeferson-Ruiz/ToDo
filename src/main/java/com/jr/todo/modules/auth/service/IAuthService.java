package com.jr.todo.modules.auth.service;

import com.jr.todo.modules.user.dto.AuthRequest;
import com.jr.todo.modules.user.dto.AuthResponse;
import com.jr.todo.modules.user.dto.UserCreateDto;

public interface IAuthService {
  AuthResponse login(AuthRequest request);

  String register(UserCreateDto request);

  public void logout(String authHeader);

}

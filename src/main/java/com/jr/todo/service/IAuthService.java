package com.jr.todo.service;

import com.jr.todo.dto.user.AuthRequest;
import com.jr.todo.dto.user.AuthResponse;
import com.jr.todo.dto.user.UserCreateDto;

public interface IAuthService {
  AuthResponse login(AuthRequest request);

  AuthResponse register(UserCreateDto request);
}

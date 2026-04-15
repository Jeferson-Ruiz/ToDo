package com.jr.todo.service;

import com.jr.todo.dto.auth.AuthResponse;
import com.jr.todo.dto.auth.LoginRequestDto;
import com.jr.todo.dto.user.UserCreateDto;

public interface IAuthService {
  AuthResponse login(LoginRequestDto request);

  AuthResponse registration(UserCreateDto request);
}

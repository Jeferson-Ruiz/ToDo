package com.jr.todo.modules.user.service;

import com.jr.todo.modules.user.dto.UserCreateDto;
import com.jr.todo.modules.user.dto.UserResponseDto;

public interface IUserService {

  UserResponseDto createUser(UserCreateDto userDto);

  void updatePasswod(String email, String oldPassword, String newPassword);

  void updateEnable(String email, boolean enable);

}

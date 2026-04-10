package com.jr.todo.service;

import com.jr.todo.dto.user.UserCreateDto;
import com.jr.todo.dto.user.UserResponseDto;

public interface IUserService {

  UserResponseDto createUser(UserCreateDto userDto);

  void updatePasswod(Long id, String oldPassword, String newPassword);

  void updateEnable(Long id, boolean enable);

}

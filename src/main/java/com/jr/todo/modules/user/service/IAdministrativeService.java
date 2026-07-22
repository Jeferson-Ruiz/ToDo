package com.jr.todo.modules.user.service;

import com.jr.todo.dto.UserCreateDto;
import com.jr.todo.enums.Role;
import com.jr.todo.modules.user.dto.UserResponseDto;

public interface IAdministrativeService {
    public UserResponseDto createUser(UserCreateDto userDto);

    public void updateEnable(String email, boolean enable);

    public void updateRole(String email, Role role);

    public void updatePassword(String email, String newPassword);

}

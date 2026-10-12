package com.jr.todo.modules.user.service;

import java.time.LocalDate;
import java.util.List;
import com.jr.todo.dto.UserCreateDto;
import com.jr.todo.enums.Role;
import com.jr.todo.modules.user.dto.UserResponseDto;

public interface IAdministrativeService {
    public UserResponseDto createUser(UserCreateDto userDto);

    public void updateEnable(long id, boolean enable);

    public void updateRole(long id, Role role);

    public void updatePassword(long id, String newPassword);

    List<UserResponseDto> findAllUsers();

    List<UserResponseDto> findAllByDate(LocalDate dateInit, LocalDate dateFinish);

    List<UserResponseDto> findAllByRole(Role role);

}

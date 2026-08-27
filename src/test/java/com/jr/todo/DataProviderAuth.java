package com.jr.todo;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.jr.todo.dto.AuthRequest;
import com.jr.todo.dto.UserCreateDto;
import com.jr.todo.enums.Role;
import com.jr.todo.modules.auth.entity.AccountActivationToken;
import com.jr.todo.modules.user.entity.User;

public class DataProviderAuth {

    public static AuthRequest AuthRequesDtoMock() {
        return new AuthRequest("correo@correo.com", "password");
    }

    public static User userMock() {
        return new User(1L, "usuario", "usuario", "correo@correo.com", "usuario", "password", false, Role.USER,
                LocalDate.now(), null, null);
    }

    public static UserCreateDto userCreateDtoMock() {
        return new UserCreateDto("usuario", "usuario", "corre@correo.com", "usuario", "password");
    }

    public static AccountActivationToken activationToken() {
        return new AccountActivationToken(1L, "prueba.token", userMock(), LocalDateTime.now().plusHours(24), false);

    }
}

package com.jr.todo;

import java.time.LocalDate;
import com.jr.todo.dto.UserCreateDto;
import com.jr.todo.enums.Role;
import com.jr.todo.modules.user.entity.User;

public class DataProviderAdministrative {

    public static UserCreateDto userDtoMock() {
        return new UserCreateDto("admin", "admin", "admin@correo.com", "admin", "adminpassword");
    }

    public static User userMock() {
        return new User(1L, "user", "user", "user@correo.com", "user", "userPassword", true, Role.USER, LocalDate.now(),
                null, null);
    }
}

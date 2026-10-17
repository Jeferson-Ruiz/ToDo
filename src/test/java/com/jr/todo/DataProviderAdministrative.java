package com.jr.todo;

import java.time.LocalDateTime;
import com.jr.todo.enums.Role;
import com.jr.todo.modules.user.dto.AdminCreateDto;
import com.jr.todo.modules.user.entity.User;

public class DataProviderAdministrative {

    public static AdminCreateDto userDtoMock() {
        return new AdminCreateDto("admin", "admin", "admin@correo.com", "admin", "adminpassword", Role.ADMIN);
    }

    public static User userMock() {
        return new User(1L,
                "user", "user", "user@correo.com", "user", "userPassword", true, Role.USER, LocalDateTime.now(),
                null, null);
    }
}

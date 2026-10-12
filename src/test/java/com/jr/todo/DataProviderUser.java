package com.jr.todo;

import java.time.LocalDateTime;
import com.jr.todo.enums.Role;
import com.jr.todo.modules.user.entity.User;

public class DataProviderUser {

    public static User UserMock() {
        return new User(1L, "usuario", "usuario", "usuarioprueba@correo.com", "usuario",
                "password1", true, Role.USER,
                LocalDateTime.now(), null, null);
    }
}

package com.jr.todo;

import java.time.LocalDate;
import com.jr.todo.enums.Role;
import com.jr.todo.modules.user.entity.User;

public class DataProvider {

    public static User userMock() {
        return new User(1L, "Pedro", "Pascal", "pedro@correo.com", "pedro",
                "empanada1", true, Role.USER, LocalDate.now(), null, null);
    }
}

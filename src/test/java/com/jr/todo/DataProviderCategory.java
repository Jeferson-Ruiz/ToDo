package com.jr.todo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.jr.todo.enums.Role;
import com.jr.todo.modules.category.entity.Category;
import com.jr.todo.modules.user.entity.User;

public class DataProviderCategory {

    public static User userMock() {
        return new User(1L, "Pedro", "Pascal", "pedro@correo.com", "pedro",
                "empanada1", true, Role.USER, LocalDate.now(), null, null);
    }

    public static List<Category> listCategoryDtosMock() {
        return List.of(
                new Category(1L, "Compras", "despensa", LocalDateTime.now(), null),
                new Category(2L, "Universidad", "parciales", LocalDateTime.now(), null));
    }

    public static Category categoryMock() {
        return new Category(1L, "Compras", "despensa", LocalDateTime.now(), null);
    }

    
}
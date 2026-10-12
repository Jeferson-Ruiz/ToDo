package com.jr.todo.modules.user.dto;

import com.jr.todo.enums.Role;
import jakarta.validation.constraints.NotNull;

public record UpdateRoleDto(
        @NotNull Long id,
        @NotNull(message = "El rol es obligatorio") Role role) {

}

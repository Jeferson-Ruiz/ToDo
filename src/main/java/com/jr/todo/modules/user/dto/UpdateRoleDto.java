package com.jr.todo.modules.user.dto;

import com.jr.todo.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateRoleDto(
        @NotBlank(message = "El email es obligatorio") @Email(message = "Formato de email inválido") String email,
        @NotNull(message = "El rol es obligatorio") Role role) {

}

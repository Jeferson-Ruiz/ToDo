package com.jr.todo.modules.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PasswordUpdateDto(
        @NotNull Long id,
        @NotBlank(message = "La contraseña actual es obligatoria") String oldPassword,
        @NotBlank(message = "La nueva contraseña es obligatoria") String newPassword) {
}

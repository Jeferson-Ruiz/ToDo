package com.jr.todo.modules.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PasswordUpdateDto(
        @NotBlank(message = "El email es obligatorio") @Email(message = "Formato de email inválido") String email,
        @NotBlank(message = "La contraseña actual es obligatoria") String oldPassword,
        @NotBlank(message = "La nueva contraseña es obligatoria") String newPassword) {
}

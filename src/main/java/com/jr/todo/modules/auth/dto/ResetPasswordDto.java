package com.jr.todo.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordDto(
                @NotBlank(message = "La nueva contraseña es obligatoria") @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres") String newPassword,
                @NotBlank(message = "La confirmación es obligatoria") String repeatPassword) {
}

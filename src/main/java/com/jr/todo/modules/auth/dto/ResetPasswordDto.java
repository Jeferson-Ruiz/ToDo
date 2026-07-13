package com.jr.todo.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordDto(
                @NotBlank String newPassword,
                @NotBlank String repeatPassword) {
}

package com.jr.todo.modules.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PasswordUpdateDto(
        @NotNull String email,
        @NotBlank String oldPassword,
        @NotBlank String newPassword) {
}

package com.jr.todo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PasswordUpdateDto(
    @NotNull Long id,
    @NotBlank String oldPassword,
    @NotBlank String newPassword) {
}

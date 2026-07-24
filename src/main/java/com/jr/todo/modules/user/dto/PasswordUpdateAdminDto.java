package com.jr.todo.modules.user.dto;

import jakarta.validation.constraints.NotBlank;

public record PasswordUpdateAdminDto(
                @NotBlank String email,
                @NotBlank String password) {
}

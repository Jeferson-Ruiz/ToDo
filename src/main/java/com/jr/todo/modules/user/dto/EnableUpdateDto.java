package com.jr.todo.modules.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EnableUpdateDto(
                @NotBlank(message = "El email es obligatorio") @Email(message = "Formato de email inválido") String email,
                boolean enable) {
}

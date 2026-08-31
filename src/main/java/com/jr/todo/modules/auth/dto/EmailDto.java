package com.jr.todo.modules.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailDto(
        @NotBlank(message = "El email es obligatorio") @Email(message = "Formato de email inválido") String email) {
}

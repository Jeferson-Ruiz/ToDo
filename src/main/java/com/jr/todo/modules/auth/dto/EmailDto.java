package com.jr.todo.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record EmailDto(
        @NotBlank String email) {
}

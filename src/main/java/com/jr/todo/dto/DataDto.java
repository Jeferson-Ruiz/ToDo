package com.jr.todo.dto;

import jakarta.validation.constraints.NotBlank;

public record DataDto(
    @NotBlank String data) {
}

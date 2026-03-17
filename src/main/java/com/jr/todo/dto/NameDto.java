package com.jr.todo.dto;

import jakarta.validation.constraints.NotBlank;

public record NameDto(
    @NotBlank String name) {
}

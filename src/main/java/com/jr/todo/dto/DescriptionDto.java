package com.jr.todo.dto;

import jakarta.validation.constraints.NotBlank;

public record DescriptionDto(
    @NotBlank String description) {
}

package com.jr.todo.modules.task.dto;

import jakarta.validation.constraints.Size;

public record DescriptionDto(
                @Size(max = 100, message = "La descripcion no puede exceder 50 caracteres") String description) {
}

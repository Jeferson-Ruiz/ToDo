package com.jr.todo.modules.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DescriptionDto(
        @NotBlank(message = "La descripcion es obligatoria") @Size(max = 200, message = "La descripcion no puede exceder 200 caracteres") String description) {
}

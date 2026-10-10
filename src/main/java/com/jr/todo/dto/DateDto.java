package com.jr.todo.dto;

import java.time.LocalDateTime;
import jakarta.validation.constraints.NotNull;

public record DateDto(
        @NotNull(message = "La fecha es obligatoria") LocalDateTime date) {
}

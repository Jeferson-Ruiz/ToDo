package com.jr.todo.dto;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

public record DateDto(
        @NotNull(message = "La fecha es obligatoria") @JsonFormat(pattern = "dd/MM/yyyy HH:mm") LocalDateTime date) {
}

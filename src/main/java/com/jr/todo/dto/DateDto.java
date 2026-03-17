package com.jr.todo.dto;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

public record DateDto(
    @NotNull @JsonFormat(pattern = "dd-MM-yyyy HH:mm") LocalDateTime date) {
}

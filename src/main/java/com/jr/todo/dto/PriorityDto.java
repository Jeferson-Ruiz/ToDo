package com.jr.todo.dto;

import com.jr.todo.entity.Priority;
import jakarta.validation.constraints.NotNull;

public record PriorityDto(
    @NotNull Priority priority) {
}

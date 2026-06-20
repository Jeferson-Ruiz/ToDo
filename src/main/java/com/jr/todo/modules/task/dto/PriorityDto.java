package com.jr.todo.modules.task.dto;

import com.jr.todo.modules.task.enums.Priority;

import jakarta.validation.constraints.NotNull;

public record PriorityDto(
        @NotNull Priority priority) {
}

package com.jr.todo.modules.user.dto;

import jakarta.validation.constraints.NotNull;

public record EnableUpdateDto(
                @NotNull Long id,
                boolean enable) {
}

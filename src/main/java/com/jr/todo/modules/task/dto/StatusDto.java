package com.jr.todo.modules.task.dto;

import com.jr.todo.modules.task.enums.Status;
import jakarta.validation.constraints.NotNull;

public record StatusDto(
@NotNull(message = "El estado es obligatorio") Status status) {

}

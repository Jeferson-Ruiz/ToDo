package com.jr.todo.modules.task.dto;

import com.jr.todo.modules.task.enums.Status;

import jakarta.validation.constraints.NotEmpty;

public record StatusDto(
                @NotEmpty Status status) {

}

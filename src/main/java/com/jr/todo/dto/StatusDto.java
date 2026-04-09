package com.jr.todo.dto;

import com.jr.todo.entity.enums.Status;

import jakarta.validation.constraints.NotEmpty;

public record StatusDto(
    @NotEmpty Status status) {

}

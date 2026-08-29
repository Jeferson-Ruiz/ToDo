package com.jr.todo.modules.task.dto;

import java.time.LocalDateTime;

import com.jr.todo.modules.task.entity.Task;
import com.jr.todo.modules.task.enums.Priority;
import com.jr.todo.modules.task.enums.Status;

public record TaskCreateDto(
    String name,
    String description,
    Status status,
    LocalDateTime deadline,
    Priority priority,
    String category) {

  public Task toEntity() {
    return new Task(
        null,
        this.name,
        this.description,
        null,
        this.status,
        this.deadline,
        this.priority,
        null);
  }
}

package com.jr.todo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

import com.jr.todo.entity.Task;
import com.jr.todo.entity.enums.Priority;
import com.jr.todo.entity.enums.Status;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TaskDto(
    String name,
    String description,
    Status status,
    LocalDateTime deadline,
    Priority priority,
    String category) {

  public static TaskDto toDto(Task task) {
    return new TaskDto(
        task.getName(),
        task.getDescription(),
        task.getStatus(),
        task.getDeadline(),
        task.getPriority(),
        task.getCategory() != null ? task.getCategory().getName() : null);
  }

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
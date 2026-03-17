package com.jr.todo.dto;

import java.time.LocalDateTime;
import com.jr.todo.entity.Priority;
import com.jr.todo.entity.Status;
import com.jr.todo.entity.Task;

public record TaskDto(
    String name,
    String description,
    Status status,
    LocalDateTime deadline,
    Priority priority) {

  public static TaskDto toDto(Task task) {
    return new TaskDto(
        task.getName(),
        task.getDescription(),
        task.getStatus(),
        task.getDeadline(),
        task.getPriority());
  }

  public Task toEntity() {
    return new Task(
        null,
        this.name,
        this.description,
        LocalDateTime.now(),
        this.status,
        this.deadline,
        this.priority);
  }
}
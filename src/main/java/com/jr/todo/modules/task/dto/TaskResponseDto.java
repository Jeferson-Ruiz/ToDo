package com.jr.todo.modules.task.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import com.jr.todo.modules.task.entity.Task;
import com.jr.todo.modules.task.enums.Priority;
import com.jr.todo.modules.task.enums.Status;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TaskResponseDto(
    Long id,
    String name,
    String description,
    Status status,
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm") LocalDateTime deadline,
    Priority priority,
    String category,
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm") LocalDateTime dateCreation) {

  public static TaskResponseDto toDto(Task task) {
    return new TaskResponseDto(
        task.getTaskId(),
        task.getName(),
        task.getDescription(),
        task.getStatus(),
        task.getDeadline(),
        task.getPriority(),
        task.getCategory() != null ? task.getCategory().getName() : null,
        task.getDateCreation());
  }
}

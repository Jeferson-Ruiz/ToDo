package com.jr.todo.modules.task.dto;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.jr.todo.modules.task.entity.Task;
import com.jr.todo.modules.task.enums.Priority;
import com.jr.todo.modules.task.enums.Status;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskCreateDto(
    @NotBlank(message = "El nombre es obligatorio") @Size(max = 50, message = "El nombre no puede exceder 50 caracteres") String name,

    @NotBlank(message = "La descripcion es obligatoria") @Size(max = 200, message = "La descripcion no puede exceder 200 caracteres") String description,

    Status status,

    @Future(message = "La fecha limite debe ser futura") @JsonFormat(pattern = "dd/MM/yyyy HH:mm") LocalDateTime deadline,

    Priority priority,

    @Size(max = 40, message = "La categoria no puede exceder 40 caracteres") String category) {

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

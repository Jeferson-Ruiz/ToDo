package com.jr.todo.modules.category.dto;

import java.time.LocalDateTime;
import com.jr.todo.modules.category.entity.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryCreateDto(
    @NotBlank(message = "El nombre es obligatorio") @Size(max = 40, message = "El nombre no puede exceder 40 caracteres") String name,
    @Size(max = 255, message = "La descripcion no puede exceder 255 caracteres") String description) {

  public Category toEntity() {
    return new Category(
        null,
        this.name,
        this.description,
        LocalDateTime.now(),
        null,
        null);
  }
}

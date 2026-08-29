package com.jr.todo.modules.category.dto;

import java.time.LocalDateTime;
import com.jr.todo.modules.category.entity.Category;

public record CategoryResponseDto(
    Long id,
    String name,
    String description,
    LocalDateTime dateCreation) {

  public static CategoryResponseDto toDto(Category category) {
    return new CategoryResponseDto(
        category.getCategoryId(),
        category.getName(),
        category.getDescription(),
        category.getDateCreation());
  }
}

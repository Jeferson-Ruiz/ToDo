package com.jr.todo.modules.category.dto;

import java.time.LocalDateTime;
import com.jr.todo.modules.category.entity.Category;

public record CategoryDto(
    String name,
    String description) {

  public static CategoryDto toDto(Category category) {
    return new CategoryDto(
        category.getName(),
        category.getDescription());
  }

  public Category toEntity() {
    return new Category(
        null,
        this.name,
        this.description,
        LocalDateTime.now(),
        null);
  }
}

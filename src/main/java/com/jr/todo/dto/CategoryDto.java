package com.jr.todo.dto;

import java.time.LocalDateTime;
import com.jr.todo.entity.Category;

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

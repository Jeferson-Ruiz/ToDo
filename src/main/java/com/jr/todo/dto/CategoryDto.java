package com.jr.todo.dto;

import java.time.LocalDateTime;
import com.jr.todo.entity.Category;

public record CategoryDto(
    String name,
    String description,
    LocalDateTime dateCreation) {

  public static CategoryDto toDto(Category category) {
    return new CategoryDto(
        category.getName(),
        category.getDescription(),
        category.getDateCreation());
  }

  public Category toEntity() {
    return new Category(
        null,
        this.name,
        this.description,
        this.dateCreation,
        null);
  }
}

package com.jr.todo.modules.category.dto;

import java.time.LocalDateTime;
import com.jr.todo.modules.category.entity.Category;

public record CategoryCreateDto(
    String name,
    String description) {

  public Category toEntity() {
    return new Category(
        null,
        this.name,
        this.description,
        LocalDateTime.now(),
        null);
  }
}

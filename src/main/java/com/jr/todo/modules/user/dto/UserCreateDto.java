package com.jr.todo.modules.user.dto;

import com.jr.todo.modules.user.entity.User;

public record UserCreateDto(
    String name,
    String lastName,
    String email,
    String username,
    String password) {

  public User toEntity() {
    return new User(
        null,
        this.name,
        this.lastName,
        this.email,
        this.username,
        this.password,
        false,
        null,
        null,
        null);
  }
}

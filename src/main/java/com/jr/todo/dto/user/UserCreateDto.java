package com.jr.todo.dto.user;

import com.jr.todo.entity.User;

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
        true,
        null);
  }
}

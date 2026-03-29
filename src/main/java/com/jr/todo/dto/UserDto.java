package com.jr.todo.dto;

import com.jr.todo.entity.User;

public record UserDto(
    String name,
    String username,
    String email,
    String password) {

  public static UserDto toDto(User user) {
    return new UserDto(
        user.getName(),
        user.getUsername(),
        user.getEmail(),
        null);
  }

  public User toEntity() {
    return new User(
        null,
        this.name,
        this.username,
        this.email,
        this.password);
  }

}

package com.jr.todo.dto.user;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.jr.todo.entity.User;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserResponseDto(
    String name,
    String lastName,
    String email,
    String username,
    String password,
    boolean enable,
    LocalDate registrationDte) {

  public static UserResponseDto toDto(User user) {
    return new UserResponseDto(
        user.getName(),
        user.getLastName(),
        user.getEmail(),
        user.getUsername(),
        null,
        user.isEnable(),
        user.getRegistrationDte());
  }
}

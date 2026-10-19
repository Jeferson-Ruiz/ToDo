package com.jr.todo.modules.user.dto;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.jr.todo.enums.Role;
import com.jr.todo.modules.user.entity.User;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserResponseDto(
    Long id,
    String name,
    String lastName,
    String email,
    String username,
    String password,
    Role role,
    boolean enable,
    LocalDateTime registrationDte) {

  public static UserResponseDto toDto(User user) {
    return new UserResponseDto(
        user.getId(),
        user.getName(),
        user.getLastName(),
        user.getEmail(),
        user.getUsername(),
        null,
        user.getRole(),
        user.isEnabled(),
        user.getRegistrationDte());
  }
}

package com.jr.todo.dto;

import com.jr.todo.modules.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateDto(
    @NotBlank(message = "El nombre es obligatorio") @Size(max = 50, message = "El nombre no puede exceder 50 caracteres") String name,
    @NotBlank(message = "El apellido es obligatorio") @Size(max = 50, message = "El apellido no puede exceder 50 caracteres") String lastName,
    @NotBlank(message = "El email es obligatorio") @Email(message = "Formato de email inválido") String email,
    @NotBlank(message = "El username es obligatorio") @Size(min = 3, max = 20, message = "El username debe tener entre 3 y 20 caracteres") String username,
    @NotBlank(message = "La contraseña es obligatoria") @Size(min = 8, max = 100, message = "La contraseña debe tener al menos 8 caracteres") String password) {

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
        null,
        null);
  }
}

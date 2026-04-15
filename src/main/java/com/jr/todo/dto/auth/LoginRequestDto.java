package com.jr.todo.dto.auth;

public record LoginRequestDto(
        String email,
        String password) {
}

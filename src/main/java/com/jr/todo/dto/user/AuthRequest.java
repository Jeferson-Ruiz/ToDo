package com.jr.todo.dto.user;

public record AuthRequest(
    String email,
    String password) {
}

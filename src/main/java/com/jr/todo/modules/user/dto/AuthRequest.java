package com.jr.todo.modules.user.dto;

public record AuthRequest(
        String email,
        String password) {
}

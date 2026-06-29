package com.jr.todo.dto;

public record AuthRequest(
        String email,
        String password) {
}

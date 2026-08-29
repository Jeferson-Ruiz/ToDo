package com.jr.todo.dto;

public record AuthResponse(
                String token,
                String email,
                String role,
                Long expiresIn) {
}

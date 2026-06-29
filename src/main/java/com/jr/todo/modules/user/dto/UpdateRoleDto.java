package com.jr.todo.modules.user.dto;

import com.jr.todo.enums.Role;

public record UpdateRoleDto(
        String email,
        Role role) {

}

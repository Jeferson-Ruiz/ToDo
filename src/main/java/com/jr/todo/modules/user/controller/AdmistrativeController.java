package com.jr.todo.modules.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.jr.todo.dto.UserCreateDto;
import com.jr.todo.modules.user.dto.EnableUpdateDto;
import com.jr.todo.modules.user.dto.PasswordUpdateAdminDto;
import com.jr.todo.modules.user.dto.UpdateRoleDto;
import com.jr.todo.modules.user.dto.UserResponseDto;
import com.jr.todo.modules.user.service.IAdministrativeService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin")
public class AdmistrativeController {

    private final IAdministrativeService administrativeService;

    public AdmistrativeController(IAdministrativeService administrativeService) {
        this.administrativeService = administrativeService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> userCreate(@Valid @RequestBody UserCreateDto userRequest) {
        UserResponseDto userDto = administrativeService.createUser(userRequest);
        return ResponseEntity.ok(userDto);
    }

    @PatchMapping("/update-enable")
    public ResponseEntity<?> updateEnable(@Valid @RequestBody EnableUpdateDto data) {
        administrativeService.updateEnable(data.email(), data.enable());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/update-role")
    public ResponseEntity<?> updateRole(@Valid @RequestBody UpdateRoleDto data) {
        administrativeService.updateRole(data.email(), data.role());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/update-password")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody PasswordUpdateAdminDto request) {
        administrativeService.updatePassword(request.email(), request.password());
        return ResponseEntity.noContent().build();
    }

}

package com.jr.todo.modules.user.controller;

import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.jr.todo.dto.PagedResponse;
import com.jr.todo.enums.Role;
import com.jr.todo.modules.user.dto.AdminCreateDto;
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
    public ResponseEntity<?> userCreate(@Valid @RequestBody AdminCreateDto userRequest) {
        UserResponseDto userDto = administrativeService.createUser(userRequest);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/date")
    public ResponseEntity<?> getAllUsersByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate init,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate finish) {
        List<UserResponseDto> users = administrativeService.findAllByDate(init, finish);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers(@RequestParam(defaultValue = "0") int page) {
        PagedResponse<UserResponseDto> users = administrativeService.findAllUsers(page);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/name")
    public ResponseEntity<?> getUserByName(@RequestParam String name) {
        UserResponseDto userDto = administrativeService.findByName(name);
        return ResponseEntity.ok(userDto);
    }


    @GetMapping("/role/{role}")
    public ResponseEntity<?> getAllUsersByRole(@RequestParam Role role) {
        List<UserResponseDto> users = administrativeService.findAllByRole(role);
        return ResponseEntity.ok(users);
    }

    @PatchMapping("/update-enable")
    public ResponseEntity<?> updateEnable(@Valid @RequestBody EnableUpdateDto data) {
        administrativeService.updateEnable(data.id(), data.enable());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/update-role")
    public ResponseEntity<?> updateRole(@Valid @RequestBody UpdateRoleDto data) {
        administrativeService.updateRole(data.id(), data.role());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/update-password")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody PasswordUpdateAdminDto request) {
        administrativeService.updatePassword(request.id(), request.password());
        return ResponseEntity.noContent().build();
    }

}

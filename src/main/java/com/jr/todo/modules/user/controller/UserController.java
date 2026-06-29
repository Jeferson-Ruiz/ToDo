package com.jr.todo.modules.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.jr.todo.dto.UserCreateDto;
import com.jr.todo.modules.task.dto.EnableUpdateDto;
import com.jr.todo.modules.user.dto.PasswordUpdateDto;
import com.jr.todo.modules.user.dto.UpdateRoleDto;
import com.jr.todo.modules.user.dto.UserResponseDto;
import com.jr.todo.modules.user.service.IUserService;

@RestController
@RequestMapping("/user")
public class UserController {

  private final IUserService userService;

  public UserController(IUserService userService) {
    this.userService = userService;
  }

  @PostMapping("/create")
  public ResponseEntity<?> userCreate(@RequestBody UserCreateDto userRequest) {
    UserResponseDto userDto = userService.createUser(userRequest);
    return ResponseEntity.ok(userDto);
  }

  @PatchMapping("/update-password")
  public ResponseEntity<?> updatePassword(@RequestBody PasswordUpdateDto data) {
    userService.updatePasswod(data.email(), data.oldPassword(), data.newPassword());
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/admin/update-enable")
  public ResponseEntity<?> updateEnable(@RequestBody EnableUpdateDto data) {
    userService.updateEnable(data.email(), data.enable());
    return ResponseEntity.noContent().build();
  }

  public ResponseEntity<?> updateRole(@RequestBody UpdateRoleDto data) {
    userService.updateRole(data.email(), data.role());
    return ResponseEntity.noContent().build();
  }

}

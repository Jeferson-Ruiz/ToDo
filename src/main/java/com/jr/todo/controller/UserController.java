package com.jr.todo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.jr.todo.dto.EnableUpdateDto;
import com.jr.todo.dto.PasswordUpdateDto;
import com.jr.todo.dto.user.UserCreateDto;
import com.jr.todo.dto.user.UserResponseDto;
import com.jr.todo.service.IUserService;

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
    userService.updatePasswod(data.id(), data.oldPassword(), data.newPassword());
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/admin/update-enable")
  public ResponseEntity<?> updateEnable(@RequestBody EnableUpdateDto data) {
    userService.updateEnable(data.id(), data.enable());
    return ResponseEntity.noContent().build();
  }
}

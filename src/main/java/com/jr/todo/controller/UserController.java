package com.jr.todo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.jr.todo.dto.PasswordUpdateDto;
import com.jr.todo.dto.UserDto;
import com.jr.todo.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/create")
  public ResponseEntity<?> userCreate(@RequestBody UserDto userRequest) {
    UserDto userDto = userService.createUser(userRequest);
    return ResponseEntity.ok(userDto);
  }

  @PatchMapping("/update-password")
  public ResponseEntity<?> updatePassword(@RequestBody PasswordUpdateDto data) {
    userService.updatePasswod(data.id(), data.oldPassword(), data.newPassword());
    return ResponseEntity.noContent().build();
  }

}

package com.jr.todo.modules.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.jr.todo.modules.user.dto.PasswordUpdateDto;
import com.jr.todo.modules.user.service.IUserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {

  private final IUserService userService;

  public UserController(IUserService userService) {
    this.userService = userService;
  }

  @PatchMapping("/update-password")
  public ResponseEntity<?> updatePassword(@Valid @RequestBody PasswordUpdateDto data) {
    userService.updatePasswod(data.email(), data.oldPassword(), data.newPassword());
    return ResponseEntity.noContent().build();
  }

}

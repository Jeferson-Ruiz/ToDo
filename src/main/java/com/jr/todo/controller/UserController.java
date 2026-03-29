package com.jr.todo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.jr.todo.dto.DataDto;
import com.jr.todo.dto.UserDto;
import com.jr.todo.service.UserService;

@RequestMapping
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/creare")
  public ResponseEntity<?> userCreate(@RequestBody UserDto userRequest) {
    UserDto userDto = userService.createUser(userRequest);
    return ResponseEntity.ok(userDto);
  }

  @PatchMapping("/update-password/{id}")
  public ResponseEntity<?> updatePassword(@PathVariable long id, @RequestBody DataDto oldPasword,
      @RequestBody DataDto newPassword) {
    userService.updatePasswod(id, oldPasword.data(), newPassword.data());
    return ResponseEntity.noContent().build();
  }

}

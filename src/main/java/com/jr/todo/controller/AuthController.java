package com.jr.todo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.jr.todo.dto.auth.LoginRequestDto;
import com.jr.todo.dto.user.UserCreateDto;
import com.jr.todo.service.IAuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final IAuthService authService;

  public AuthController(IAuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequestDto request) {
    return ResponseEntity.ok(authService.login(request));
  }

  @PostMapping("/registration")
  public ResponseEntity<?> userCreate(@RequestBody UserCreateDto userRequest) {
    return ResponseEntity.ok(authService.registration(userRequest));
  }

}

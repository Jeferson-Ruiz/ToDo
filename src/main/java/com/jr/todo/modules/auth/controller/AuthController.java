package com.jr.todo.modules.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.jr.todo.modules.auth.service.IAuthService;
import com.jr.todo.modules.user.dto.AuthRequest;
import com.jr.todo.modules.user.dto.UserCreateDto;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final IAuthService authService;

  public AuthController(IAuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody AuthRequest request) {
    return ResponseEntity.ok(authService.login(request));
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody UserCreateDto request) {
    return ResponseEntity.ok(authService.register(request));
  }

  @PostMapping("/logouth")
  public ResponseEntity<String> logout(HttpServletRequest request) {
    authService.logout(request.getHeader("AUTHORIZATION"));
    return ResponseEntity.ok("Sesión cerrada con exito");
  }

}

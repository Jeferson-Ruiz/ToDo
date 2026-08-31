package com.jr.todo.modules.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.jr.todo.dto.AuthRequest;
import com.jr.todo.dto.UserCreateDto;
import com.jr.todo.modules.auth.service.IAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final IAuthService authService;

  public AuthController(IAuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
    return ResponseEntity.ok(authService.login(request));
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody UserCreateDto request) {
    return ResponseEntity.ok(authService.register(request));
  }

  @PostMapping("/resendemail")
  public ResponseEntity<?> resendEmail(@Valid @RequestBody UserCreateDto request) {
    return ResponseEntity.ok(authService.resendActivationEmail(request));
  }

  @PostMapping("/logout")
  public ResponseEntity<String> logout(HttpServletRequest request) {
    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    authService.logout(authHeader);
    return ResponseEntity.ok("Sesion cerrada");
  }

  @GetMapping("/activation")
  public ResponseEntity<?> activationCount(@RequestParam String token) {
    authService.activateAccount(token);
    return ResponseEntity.ok("Cuenta Activada");
  }

}

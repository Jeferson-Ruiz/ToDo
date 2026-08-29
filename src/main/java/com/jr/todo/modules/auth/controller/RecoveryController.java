package com.jr.todo.modules.auth.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.jr.todo.modules.auth.dto.EmailDto;
import com.jr.todo.modules.auth.dto.ResetPasswordDto;
import com.jr.todo.modules.auth.service.IRecoveryService;

@RestController
@RequestMapping("/recovery")
public class RecoveryController {

    private final IRecoveryService recoveryService;

    public RecoveryController(IRecoveryService recoveryService) {
        this.recoveryService = recoveryService;
    }

    @PostMapping("/request")
    public ResponseEntity<?> initiateRecovery(@RequestBody EmailDto reques) {
        recoveryService.initiateRecovery(reques.email());
        return ResponseEntity.ok(Map.of("message", "Email de recuperación enviado"));
    }

    @PostMapping("/api/reset-password")
    public ResponseEntity<?> resetPasswordApi(@RequestParam String token, @RequestBody ResetPasswordDto request) {
        recoveryService.updatePassword(token, request);
        return ResponseEntity.ok(Map.of("message", "Contraseña actualizada correctamente"));
    }

}

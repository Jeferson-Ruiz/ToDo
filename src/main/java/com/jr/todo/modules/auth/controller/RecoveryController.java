package com.jr.todo.modules.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.jr.todo.modules.auth.dto.EmailDto;
import com.jr.todo.modules.auth.dto.ResetPasswordDto;
import com.jr.todo.modules.auth.service.IRecoveryService;

@Controller
@RequestMapping("/recovery")
public class RecoveryController {

    private final IRecoveryService recoveryService;

    public RecoveryController(IRecoveryService recoveryService) {
        this.recoveryService = recoveryService;
    }

    @PostMapping("/count")
    public ResponseEntity<?> initiateRecovery(@RequestBody EmailDto reques) {
        recoveryService.initiateRecovery(reques.email());
        return ResponseEntity.ok("Email de recuperación enviado");
    }

    @GetMapping("/recoveryform")
    public String showRecoveryForm(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "ResetPasswordForm";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token,
            @RequestParam String newPassword,
            @RequestParam String repeatPassword,
            Model model) {
        try {
            ResetPasswordDto dto = new ResetPasswordDto(newPassword, repeatPassword);
            recoveryService.updatePassword(token, dto);
            model.addAttribute("successMessage", "Tu contraseña ha sido cambiada correctamente.");
            return "ResetPasswordFormSuccess";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("token", token);
            return "ResetPasswordForm";
        }
    }

}

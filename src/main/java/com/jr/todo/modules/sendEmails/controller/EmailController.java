package com.jr.todo.modules.sendEmails.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import com.jr.todo.modules.sendEmails.dto.EmailActivationDto;
import com.jr.todo.modules.sendEmails.service.IEmailService;
import jakarta.mail.MessagingException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/todo")
public class EmailController {

    private final IEmailService emailService;

    public EmailController(IEmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/activation-account")
    public ResponseEntity<String> postMethodName(@RequestBody EmailActivationDto request) throws MessagingException {
        emailService.sendEmail(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
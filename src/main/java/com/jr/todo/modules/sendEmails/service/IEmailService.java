package com.jr.todo.modules.sendEmails.service;

import com.jr.todo.modules.sendEmails.dto.EmailActivationDto;
import jakarta.mail.MessagingException;

public interface IEmailService {
    void sendEmail(EmailActivationDto email) throws MessagingException;
}

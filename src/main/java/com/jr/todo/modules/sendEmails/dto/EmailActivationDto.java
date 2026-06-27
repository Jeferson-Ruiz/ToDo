package com.jr.todo.modules.sendEmails.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailActivationDto {
    private String recipient;
    private String subject;
    private String userName;
    private String activationUrl;
    private Long expirationHours;
    private String currentYear;
}

package com.jr.todo.modules.sendEmails.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecoveryAccountDto {
    private String recipient;
    private String resetUrl;
    private Long expirationHours;
    private String currentYear;
}

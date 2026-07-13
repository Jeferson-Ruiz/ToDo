package com.jr.todo.modules.auth.service;

import com.jr.todo.modules.auth.dto.ResetPasswordDto;

public interface IRecoveryService {
    String initiateRecovery(String email);

    String updatePassword(String token, ResetPasswordDto request);
}

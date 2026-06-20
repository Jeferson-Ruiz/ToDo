package com.jr.todo.modules.auth.service;

public interface ITokenBlacklistService {
    void blackListToken(String token);

    boolean isBlackListed(String token);
}

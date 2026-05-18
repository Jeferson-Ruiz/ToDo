package com.jr.todo.service;

public interface ITokenBlacklistService {
    void blackListToken(String token);

    boolean isBlackListed(String token);
}

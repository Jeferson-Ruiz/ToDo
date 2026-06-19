package com.jr.todo.service.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Service;

import com.jr.todo.service.ITokenBlacklistService;

@Service
public class TokenBlacklistService implements ITokenBlacklistService {

    private final Set<String> blackListedTokens = Collections.synchronizedSet(new HashSet<>());

    public void blackListToken(String token) {
        blackListedTokens.add(token);
    }

    public boolean isBlackListed(String token) {
        return blackListedTokens.contains(token);
    }

}

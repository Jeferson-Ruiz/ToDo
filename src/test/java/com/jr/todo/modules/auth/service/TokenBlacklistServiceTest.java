package com.jr.todo.modules.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.jr.todo.modules.auth.entity.RevokedToken;
import com.jr.todo.modules.auth.repository.RevokedTokenRepository;

@ExtendWith(MockitoExtension.class)
class TokenBlacklistServiceTest {

    private static final String TOKEN = "prueba.prueba.prueba";

    @Mock
    private RevokedTokenRepository revokedTokenRepository;

    @Mock
    private IJwtService jwtService;

    @InjectMocks
    private TokenBlacklistService tokenBlacklistService;

    @Test
    void blackListTokenSavesJtiAndExpiration() {
        when(jwtService.getJtiFromToken(anyString())).thenReturn("abc-123");
        when(jwtService.getClaim((TOKEN), any()))
                .thenReturn(new Date(System.currentTimeMillis() + 3_600_000));

        tokenBlacklistService.blackListToken(TOKEN);

        verify(revokedTokenRepository).save(any(RevokedToken.class));
    }

    @Test
    void blackListTokenWithoutJtiDoesNothing() {
        when(jwtService.getJtiFromToken(anyString())).thenReturn(null);

        tokenBlacklistService.blackListToken(TOKEN);

        verify(revokedTokenRepository, never()).save(any());
    }

    @Test
    void blackListTokenExpiredIsNotSaved() {
        when(jwtService.getJtiFromToken(anyString())).thenReturn("abc-123");
        when(jwtService.getClaim(eq(TOKEN), any()))
                .thenReturn(new Date(System.currentTimeMillis() - 3_600_000));

        tokenBlacklistService.blackListToken(TOKEN);

        verify(revokedTokenRepository, never()).save(any());
    }

    @Test
    void isBlackListedReturnsTrueWhenTokenExistsInDb() {
        when(jwtService.getJtiFromToken(anyString())).thenReturn("abc-123");
        when(revokedTokenRepository.existsByJti("abc-123")).thenReturn(true);

        assertTrue(tokenBlacklistService.isBlackListed(TOKEN));
    }

    @Test
    void isBlackListedReturnsFalseWhenTokenNotInDb() {
        when(jwtService.getJtiFromToken(TOKEN)).thenReturn("abc-123");
        when(revokedTokenRepository.existsByJti("abc-123")).thenReturn(false);

        assertFalse(tokenBlacklistService.isBlackListed(TOKEN));
    }

    @Test
    void isBlackListedReturnsFalseWhenTokenIsMalformed() {
        when(jwtService.getJtiFromToken(TOKEN)).thenThrow(new RuntimeException("jwt invalido"));

        assertFalse(tokenBlacklistService.isBlackListed(TOKEN));
    }
}

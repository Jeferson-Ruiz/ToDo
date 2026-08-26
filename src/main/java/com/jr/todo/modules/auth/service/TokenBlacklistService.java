package com.jr.todo.modules.auth.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.jr.todo.modules.auth.entity.RevokedToken;
import com.jr.todo.modules.auth.repository.RevokedTokenRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistService implements ITokenBlacklistService {

    private final RevokedTokenRepository revokedTokenRepository;
    private final IJwtService jwtService;

    @Override
    @Transactional
    public void blackListToken(String token) {
        try {
            String jti = jwtService.getJtiFromToken(token);
            if (jti == null) {
                return; 
            }
            Date exp = jwtService.getClaim(token, Claims::getExpiration);
            LocalDateTime expiresAt = LocalDateTime.ofInstant(exp.toInstant(), ZoneId.systemDefault());

            if (!expiresAt.isAfter(LocalDateTime.now())) {
                return; 
            }
            revokedTokenRepository.save(new RevokedToken(jti, expiresAt));
        } catch (Exception e) {
            log.debug("Token no revocable: {}", e.getMessage());
        }
    }

    @Override
    public boolean isBlackListed(String token) {
        try {
            String jti = jwtService.getJtiFromToken(token);
            if (jti == null) {
                return false;
            }
            return revokedTokenRepository.existsByJti(jti);
        } catch (Exception e) {
            return false;
        }
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void purgeExpiredTokens() {
        long deleted = revokedTokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());
        if (deleted > 0) {
            log.info("Limpieza de tokens revocados: {} eliminados", deleted);
        }
    }
}

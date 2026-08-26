package com.jr.todo.modules.auth.repository;

import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import com.jr.todo.modules.auth.entity.RevokedToken;

public interface RevokedTokenRepository extends JpaRepository<RevokedToken, Long> {

    boolean existsByJti(String jti);

    long deleteByExpiresAtBefore(LocalDateTime date);
}

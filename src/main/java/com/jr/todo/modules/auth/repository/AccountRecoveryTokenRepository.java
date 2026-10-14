package com.jr.todo.modules.auth.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import com.jr.todo.modules.auth.entity.AccountRecoveryToken;
import com.jr.todo.modules.user.entity.User;

public interface AccountRecoveryTokenRepository extends JpaRepository<AccountRecoveryToken, Long> {

    Optional<AccountRecoveryToken> findByToken(String token);

    Optional<AccountRecoveryToken> findByUser(User user);

    @Modifying
    @Transactional
    @Query("DELETE FROM AccountRecoveryToken t WHERE t.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}

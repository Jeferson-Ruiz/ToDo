package com.jr.todo.modules.auth.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import com.jr.todo.modules.auth.entity.AccountActivationToken;
import com.jr.todo.modules.user.entity.User;

public interface AccountActivationTokenRepository extends JpaRepository<AccountActivationToken, Long> {

    Optional<AccountActivationToken> findByToken(String token);

    Optional<AccountActivationToken> findByUser(User user);

    @Modifying
    @Transactional
    @Query("DELETE FROM AccountActivationToken t WHERE t.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

}

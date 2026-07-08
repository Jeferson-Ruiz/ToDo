package com.jr.todo.modules.auth.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.jr.todo.modules.auth.entity.AccountRecoveryToken;
import com.jr.todo.modules.user.entity.User;

public interface AccountRecoveryTokenRepository extends JpaRepository<AccountRecoveryToken, Long> {

    Optional<AccountRecoveryToken> findByToken(String token);

    Optional<AccountRecoveryToken> findByUser(User user);
}

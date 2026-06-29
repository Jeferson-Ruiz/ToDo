package com.jr.todo.modules.auth.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.jr.todo.modules.auth.entity.AccountActivationToken;
import com.jr.todo.modules.user.entity.User;

public interface AccountActivationTokenRepository extends JpaRepository<AccountActivationToken, Long> {

    Optional<AccountActivationToken> findByToken(String token);

    Optional<AccountActivationToken> findByUser(User user);

}

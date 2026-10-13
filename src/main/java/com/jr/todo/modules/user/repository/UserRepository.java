package com.jr.todo.modules.user.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;

import com.jr.todo.enums.Role;
import com.jr.todo.modules.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
  @Query("SELECT COUNT(u)>0 FROM User u WHERE u.username =:username")
  boolean existsByName(@Param("username") String username);

  @Query("SELECT COUNT(u)>0 FROM User u WHERE u.email =:email")
  boolean existByEmail(@Param("email") String email);

  @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email AND u.enabled = true")
  boolean isUserEnabled(@Param("email") String email);

  @Query("SELECT u FROM User u WHERE u.role = :role")
  List<User> findAllByRole(@RequestParam("role") Role role);

  @Query("SELECT u FROM User u WHERE u.registrationDte BETWEEN :init AND :end")
  List<User> findAllByDate(
      @Param("init") LocalDateTime init,
      @Param("end") LocalDateTime end);

  @Query("SELECT u FROM User u WHERE u.email =:email")
  Optional<User> findByEmail(@Param("email") String email);

  // temporal, luego mudar a correm a co
  Optional<User> findByUsername(String username);
}

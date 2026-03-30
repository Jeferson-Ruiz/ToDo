package com.jr.todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.jr.todo.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
  @Query("SELECT COUNT(u)>0 FROM User u WHERE u.username =:username")
  boolean existsByName(@Param("username") String username);
}
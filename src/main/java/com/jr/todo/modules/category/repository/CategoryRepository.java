package com.jr.todo.modules.category.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.jr.todo.modules.category.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
  Page<Category> findAllByUserId(Long userId, Pageable pageable);

  @Query("SELECT c FROM Category c WHERE c.dateCreation BETWEEN :init AND :end AND c.user.id = :userId")
  List<Category> findByDateAndUserId(@Param("userId") long userId,
      @Param("init") LocalDateTime init,
      @Param("end") LocalDateTime end);

  @Query("SELECT c FROM Category c WHERE c.categoryId = :id AND c.user.id = :userId")
  Optional<Category> findByCategoryIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

  @Query("SELECT c FROM Category c WHERE c.name = :name AND c.user.id = :userId")
  Optional<Category> findByNameAndUserId(@Param("name") String name, @Param("userId") Long userId);

  @Query("SELECT c FROM Category c WHERE c.name LIKE CONCAT(:name, '%') AND c.user.id = :userId")
  List<Category> findByNameStartingWithAndUserId(@Param("name") String name, @Param("userId") Long userId);

  @Query("SELECT COUNT(c)>0 FROM Category c WHERE c.name = :name AND c.user.id = :userId")
  boolean existsByNameAndUserId(@Param("name") String name, @Param("userId") Long userId);
}

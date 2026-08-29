package com.jr.todo.modules.category.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.jr.todo.modules.category.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
  List<Category> findAllByUserId(Long userId);

  @Query("SELECT c FROM Category c WHERE c.categoryId = :id AND c.user.id = :userId")
  Optional<Category> findByCategoryIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

  @Query("SELECT c FROM Category c WHERE c.name = :name AND c.user.id = :userId")
  Optional<Category> findByNameAndUserId(@Param("name") String name, @Param("userId") Long userId);

  @Query("SELECT COUNT(c)>0 FROM Category c WHERE c.name = :name AND c.user.id = :userId")
  boolean existsByNameAndUserId(@Param("name") String name, @Param("userId") Long userId);
}

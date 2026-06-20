package com.jr.todo.modules.category.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.jr.todo.modules.category.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
  @Query("SELECT c FROM Category c WHERE c.name = :name")
  Optional<Category> findByName(@Param("name") String name);

  @Query("SELECT COUNT(c)>0 FROM Category c WHERE c.name =:name")
  boolean existByName(@Param("name") String name);
}

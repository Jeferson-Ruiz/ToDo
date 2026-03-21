package com.jr.todo.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.jr.todo.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
  @Query("SELECT c FROM Category c WHERE c.name = :name")
  Optional<Category> findByName(@Param("name") String name);

  @Query("SELECT COUNT(c)>0 FROM Category c WHERE c.name =:name")
  boolean existByName(@Param("name") String name);
}

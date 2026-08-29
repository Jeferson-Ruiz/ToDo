package com.jr.todo.modules.task.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.jr.todo.modules.task.entity.Task;
import com.jr.todo.modules.task.enums.Priority;
import com.jr.todo.modules.task.enums.Status;

public interface TaskRepository extends JpaRepository<Task, Long> {

  // Busqueda por usuario
  List<Task> findAllByUserId(Long userId);

  @Query("SELECT t FROM Task t WHERE t.name = :name AND t.user.id = :userId")
  Optional<Task> findTaskByNameAndUserId(@Param("name") String name, @Param("userId") Long userId);

  @Query("SELECT t FROM Task t WHERE t.taskId = :id AND t.user.id = :userId")
  Optional<Task> findByTaskIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

  @Query("SELECT t FROM Task t WHERE t.dateCreation = :dateCreation AND t.user.id = :userId")
  List<Task> findTasksByDateAndUserId(@Param("dateCreation") LocalDateTime dateCreation, @Param("userId") Long userId);

  @Query("SELECT t FROM Task t WHERE t.status = :status AND t.user.id = :userId")
  List<Task> findTasksByStatusAndUserId(@Param("status") Status status, @Param("userId") Long userId);

  @Query("SELECT t FROM Task t WHERE t.priority = :priority AND t.user.id = :userId")
  List<Task> findTasksByPriorityAndUserId(@Param("priority") Priority priority, @Param("userId") Long userId);

  @Query("SELECT t FROM Task t WHERE t.category.name = :name AND t.user.id = :userId")
  List<Task> findAllByCategoryAndUserId(@Param("name") String name, @Param("userId") Long userId);

  // Validar por usuario
  @Query("SELECT COUNT(t)>0 FROM Task t WHERE t.name = :name AND t.user.id = :userId")
  boolean existsByNameAndUserId(@Param("name") String name, @Param("userId") Long userId);

  @Modifying
  @Query("UPDATE Task t SET t.category = null WHERE t.category.categoryId = :categoryId AND t.user.id = :userId")
  void disassociateTasksByCategoryAndUserId(@Param("categoryId") Long categoryId, @Param("userId") Long userId);
}

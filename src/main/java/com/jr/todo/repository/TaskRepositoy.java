package com.jr.todo.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jr.todo.entity.Task;
import com.jr.todo.entity.enums.Priority;
import com.jr.todo.entity.enums.Status;

@Repository
public interface TaskRepositoy extends JpaRepository<Task, Long> {

  // Busqueda
  @Query("SELECT t FROM Task t WHERE t.name = :name")
  Optional<Task> findTaskByName(@Param("name") String name);

  @Query("SELECT t FROM Task t WHERE t.dateCreation = :dateCreation")
  List<Task> findTasksByDate(@Param("dateCreation") LocalDateTime dateCreation);

  @Query("SELECT t FROM Task t WHERE t.status = :status")
  List<Task> findTasksByStatus(@Param("status") Status status);

  @Query("SELECT t FROM Task t WHERE t.priority = :priority")
  List<Task> findTasksByPriority(@Param("priority") Priority priority);

  @Query("SELECT t FROM Task t WHERE t.category.name =:name")
  List<Task> findAllByCategory(@Param("name") String name);

  // Eliminar
  @Query("DELETE FROM Task t WHERE t.taskId = :id")
  public void deleteTask(@Param("taskId") Long id);

  // Validar
  @Query("SELECT COUNT(t)>0 FROM Task t WHERE t.name = :name")
  boolean existByName(@Param("name") String name);
}

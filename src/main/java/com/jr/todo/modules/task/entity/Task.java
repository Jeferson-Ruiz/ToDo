package com.jr.todo.modules.task.entity;

import java.time.LocalDateTime;
import com.jr.todo.modules.category.entity.Category;
import com.jr.todo.modules.task.enums.Priority;
import com.jr.todo.modules.task.enums.Status;
import com.jr.todo.modules.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tareas", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "name"}))
public class Task {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "task_id", unique = true, nullable = false)
  private Long taskId;

  @Column(nullable = false, length = 50)
  private String name;

  @Column(nullable = false, length = 200)
  private String description;

  @Column(name = "date_creation")
  private LocalDateTime dateCreation;

  private Status status;

  private LocalDateTime deadline;

  private Priority priority;

  @ManyToOne(optional = true)
  @JoinColumn(name = "category_id")
  private Category category;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
}

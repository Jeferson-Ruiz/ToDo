package com.jr.todo.modules.category.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.jr.todo.modules.task.entity.Task;
import com.jr.todo.modules.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "categoria", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "cat_nombre"}))
public class Category {

  @Id
  @Column(name = "cat_id", unique = true, nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long categoryId;

  @Column(name = "cat_nombre", length = 40)
  private String name;

  private String description;

  private LocalDateTime dateCreation;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @OneToMany(mappedBy = "category", cascade = CascadeType.PERSIST)
  private List<Task> task = new ArrayList<>();
}

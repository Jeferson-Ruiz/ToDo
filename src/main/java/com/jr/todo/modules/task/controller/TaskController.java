package com.jr.todo.modules.task.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;
import com.jr.todo.dto.DateDto;
import com.jr.todo.dto.DataDto;
import com.jr.todo.modules.task.dto.DescriptionDto;
import com.jr.todo.modules.task.dto.PriorityDto;
import com.jr.todo.modules.task.dto.StatusDto;
import com.jr.todo.modules.task.dto.TaskCreateDto;
import com.jr.todo.modules.task.dto.TaskResponseDto;
import com.jr.todo.modules.task.enums.Priority;
import com.jr.todo.modules.task.enums.Status;
import com.jr.todo.modules.task.service.ITaskService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/task")
public class TaskController {

  private final ITaskService taskService;

  public TaskController(ITaskService taskService) {
    this.taskService = taskService;
  }

  @PostMapping("/create")
  public ResponseEntity<?> createTask(@Valid @RequestBody TaskCreateDto taskDto) {
    TaskResponseDto taskSave = taskService.createTask(taskDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(taskSave);
  }

  @GetMapping("/all")
  public ResponseEntity<?> getAllTasks() {
    List<TaskResponseDto> taskDtos = taskService.getAllTaks();
    return ResponseEntity.ok(taskDtos);
  }

  @GetMapping("/category")
  public ResponseEntity<?> getAllByCategory(@RequestParam String category) {
    List<TaskResponseDto> taskDtos = taskService.getAllByCategory(category);
    return ResponseEntity.ok(taskDtos);
  }

  @GetMapping("/name")
  public ResponseEntity<?> getTaskByName(@RequestParam String name) {
    TaskResponseDto taskDto = taskService.getTaskByName(name);
    return ResponseEntity.ok(taskDto);
  }

  @GetMapping("/date")
  public ResponseEntity<?> getAllByDate(
      @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm") java.time.LocalDateTime date) {
    List<TaskResponseDto> taskDtos = taskService.getAllTaskByDate(date);
    return ResponseEntity.ok(taskDtos);
  }

  @GetMapping("/status/{status}")
  public ResponseEntity<?> getTaskByName(@PathVariable Status status) {
    List<TaskResponseDto> taskDto = taskService.getAllTaskByStatus(status);
    return ResponseEntity.ok(taskDto);
  }

  @GetMapping("/priority/{priority}")
  public ResponseEntity<?> getTaskByPriority(@PathVariable Priority priority) {
    List<TaskResponseDto> taskDto = taskService.getAllTaskByPriority(priority);
    return ResponseEntity.ok(taskDto);
  }

  // Update
  @PatchMapping("/update/name/{id}")
  public ResponseEntity<?> updateTaskName(@PathVariable Long id, @Valid @RequestBody DataDto newName) {
    taskService.updateTaskName(id, newName.data());
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/update/description/{id}")
  public ResponseEntity<?> updateTaskDescription(@PathVariable Long id, @Valid @RequestBody DescriptionDto newDescription) {
    taskService.updateTaskDescription(id, newDescription.description());
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/update/status/{id}")
  public ResponseEntity<?> updateTaskStatus(@PathVariable Long id, @Valid @RequestBody StatusDto newStatus) {
    taskService.updateTaskStatus(id, newStatus.status());
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/update/deadline/{id}")
  public ResponseEntity<?> updateDeadline(@PathVariable Long id, @Valid @RequestBody DateDto newDate) {
    taskService.updateDeadline(id, newDate.date());
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/update/priority/{id}")
  public ResponseEntity<?> updatePriority(@PathVariable Long id, @Valid @RequestBody PriorityDto priority) {
    taskService.updatePriority(id, priority.priority());
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/update/category-name/{id}")
  public ResponseEntity<?> updateCategoty(@PathVariable Long id, @Valid @RequestBody DataDto category) {
    taskService.updateCategory(id, category.data());
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<?> deleteTask(@PathVariable Long id) {
    taskService.deleteTask(id);
    return ResponseEntity.noContent().build();
  }
}
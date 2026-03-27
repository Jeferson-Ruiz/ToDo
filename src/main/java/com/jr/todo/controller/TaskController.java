package com.jr.todo.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.jr.todo.dto.DateDto;
import com.jr.todo.dto.DescriptionDto;
import com.jr.todo.dto.NameDto;
import com.jr.todo.dto.PriorityDto;
import com.jr.todo.dto.StatusDto;
import com.jr.todo.dto.TaskDto;
import com.jr.todo.entity.Priority;
import com.jr.todo.entity.Status;
import com.jr.todo.service.TaskService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/task")
public class TaskController {

  private final TaskService taskService;

  public TaskController(TaskService taskService) {
    this.taskService = taskService;
  }

  @PostMapping("/create")
  public ResponseEntity<?> createTask(@RequestBody TaskDto taskDto) {
    TaskDto taskSave = taskService.createTask(taskDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(taskSave);
  }

  @GetMapping("/all")
  public ResponseEntity<?> getAllTasks() {
    List<TaskDto> taskDtos = taskService.getAllTaks();
    return ResponseEntity.ok(taskDtos);
  }

  @GetMapping("/category")
  public ResponseEntity<?> getAllByCategory(@RequestBody NameDto request) {
    List<TaskDto> taskDtos = taskService.getAllByCategory(request.name());
    return ResponseEntity.ok(taskDtos);
  }

  @GetMapping("/name")
  public ResponseEntity<?> getTaskByName(@RequestBody NameDto name) {
    TaskDto taskDto = taskService.getTaskByName(name.name());
    return ResponseEntity.ok(taskDto);
  }

  @GetMapping("/date")
  public ResponseEntity<?> getAllByDate(@RequestBody DateDto date) {
    List<TaskDto> taskDtos = taskService.getAllTaskByDate(date.date());
    return ResponseEntity.ok(taskDtos);
  }

  @GetMapping("/status/{status}")
  public ResponseEntity<?> getTaskByName(@PathVariable Status status) {
    List<TaskDto> taskDto = taskService.getAllTaskByStatus(status);
    return ResponseEntity.ok(taskDto);
  }

  @GetMapping("/priority/{priority}")
  public ResponseEntity<?> getTaskByPriority(@PathVariable Priority priority) {
    List<TaskDto> taskDto = taskService.getAllTaskByPriority(priority);
    return ResponseEntity.ok(taskDto);
  }

  // Update
  @PatchMapping("/update/name/{id}")
  public ResponseEntity<?> updateTaskName(@PathVariable Long id, @RequestBody NameDto newName) {
    taskService.updateTaskName(id, newName.name());
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/update/description/{id}")
  public ResponseEntity<?> updateTaskDescription(@PathVariable Long id, @RequestBody DescriptionDto newDescription) {
    taskService.updateTaskDescription(id, newDescription.description());
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/update/status/{id}")
  public ResponseEntity<?> updateTaskStatus(@PathVariable Long id, @RequestBody StatusDto newStatus) {
    taskService.updateTaskStatus(id, newStatus.status());
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/update/deadline/{id}")
  public ResponseEntity<?> updateDeadline(@PathVariable Long id, @RequestBody DateDto newDate) {
    taskService.updateDeadline(id, newDate.date());
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/update/priority/{id}")
  public ResponseEntity<?> updatePriority(@PathVariable Long id, @RequestBody PriorityDto priority) {
    taskService.updatePriority(id, priority.priority());
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/update/category-name/{id}")
  public ResponseEntity<?> updateCategoty(@PathVariable Long id, @RequestBody NameDto request) {
    taskService.updateCategory(id, request.name());
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<?> deleteTask(@PathVariable Long id) {
    taskService.deleteTask(id);
    return ResponseEntity.noContent().build();
  }
}
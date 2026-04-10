package com.jr.todo.service;

import java.time.LocalDateTime;
import java.util.List;
import com.jr.todo.dto.TaskDto;
import com.jr.todo.entity.enums.Priority;
import com.jr.todo.entity.enums.Status;

public interface ITaskService {

  TaskDto createTask(TaskDto taskDto);

  List<TaskDto> getAllTaks();

  List<TaskDto> getAllByCategory(String name);

  TaskDto getTaskByName(String name);

  List<TaskDto> getAllTaskByDate(LocalDateTime date);

  List<TaskDto> getAllTaskByStatus(Status status);

  List<TaskDto> getAllTaskByPriority(Priority priority);

  void updateTaskName(Long id, String newName);

  void updateTaskDescription(Long id, String newDescription);

  void updateTaskStatus(Long id, Status status);

  void updateDeadline(Long id, LocalDateTime newDate);

  void updatePriority(Long id, Priority priority);

  void updateCategory(Long id, String name);

  void deleteTask(Long id);
}

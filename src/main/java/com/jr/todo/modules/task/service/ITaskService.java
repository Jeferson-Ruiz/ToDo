package com.jr.todo.modules.task.service;

import java.time.LocalDateTime;
import java.util.List;

import com.jr.todo.modules.task.dto.TaskCreateDto;
import com.jr.todo.modules.task.dto.TaskResponseDto;
import com.jr.todo.modules.task.enums.Priority;
import com.jr.todo.modules.task.enums.Status;

public interface ITaskService {

  TaskResponseDto createTask(TaskCreateDto taskDto);

  List<TaskResponseDto> getAllTaks();

  List<TaskResponseDto> getAllByCategory(String name);

  TaskResponseDto getTaskByName(String name);

  List<TaskResponseDto> getAllTaskByDate(LocalDateTime date);

  List<TaskResponseDto> getAllTaskByStatus(Status status);

  List<TaskResponseDto> getAllTaskByPriority(Priority priority);

  void updateTaskName(Long id, String newName);

  void updateTaskDescription(Long id, String newDescription);

  void updateTaskStatus(Long id, Status status);

  void updateDeadline(Long id, LocalDateTime newDate);

  void updatePriority(Long id, Priority priority);

  void updateCategory(Long id, String name);

  void deleteTask(Long id);
}

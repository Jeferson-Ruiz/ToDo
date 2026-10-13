package com.jr.todo.modules.task.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.jr.todo.dto.PagedResponse;
import com.jr.todo.modules.task.dto.TaskCreateDto;
import com.jr.todo.modules.task.dto.TaskResponseDto;
import com.jr.todo.modules.task.enums.Priority;
import com.jr.todo.modules.task.enums.Status;

public interface ITaskService {

  TaskResponseDto createTask(TaskCreateDto taskDto);

  PagedResponse<TaskResponseDto> getAllTaks(int page);

  List<TaskResponseDto> getAllByCategory(String name);

  TaskResponseDto getTaskByName(String name);

  List<TaskResponseDto> getAllTaskByDate(LocalDate dateInit, LocalDate dateFinish);

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

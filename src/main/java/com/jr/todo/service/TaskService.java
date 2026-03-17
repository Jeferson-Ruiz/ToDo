package com.jr.todo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.jr.todo.dto.TaskDto;
import com.jr.todo.entity.Priority;
import com.jr.todo.entity.Status;
import com.jr.todo.entity.Task;
import com.jr.todo.repository.TaskRepositoy;
import jakarta.persistence.EntityNotFoundException;

@Service
public class TaskService {
  private final TaskRepositoy taskRepositoy;

  private TaskService(TaskRepositoy taskRepositoy) {
    this.taskRepositoy = taskRepositoy;
  }

  // Crear
  public TaskDto createTask(TaskDto taskDto) {
    if (taskRepositoy.existByName(taskDto.name())) {
      throw new IllegalArgumentException("Tarea repetida");
    }
    Task task = taskDto.toEntity();
    Task saveTask = taskRepositoy.save(task);
    saveTask.setDateCreation(LocalDateTime.now());

    validarFachas(saveTask.getDateCreation(), saveTask.getDeadline());
    return TaskDto.toDto(saveTask);
  }

  // Buscar
  public List<TaskDto> getAllTaks() {
    List<Task> tasks = taskRepositoy.findAll();
    return mapToDto(tasks);
  }

  public TaskDto getTaskByName(String name) {
    Task task = findTaskByName(name);
    return TaskDto.toDto(task);
  }

  public List<TaskDto> getAllTaskByDate(LocalDateTime date) {
    List<Task> tasks = taskRepositoy.findTasksByDate(date);
    return mapToDto(tasks);
  }

  public List<TaskDto> getAllTaskByStatus(Status status) {
    List<Task> tasks = taskRepositoy.findTasksByStatus(status);
    return mapToDto(tasks);
  }

  public List<TaskDto> getAllTaskByPriority(Priority priority) {
    List<Task> tasks = taskRepositoy.findTasksByPriority(priority);
    return mapToDto(tasks);
  }

  // Actualizar
  public void updateTaskName(Long id, String newName) {
    Task task = findTaskById(id);
    task.setName(newName);
    taskRepositoy.save(task);
  }

  public void updateTaskDescription(Long id, String newDescription) {
    Task task = findTaskById(id);
    task.setDescription(newDescription);
    taskRepositoy.save(task);
  }

  public void updateTaskStatus(Long id, Status status) {
    Task task = findTaskById(id);
    task.setStatus(status);
    taskRepositoy.save(task);
  }

  public void updateDeadline(Long id, LocalDateTime newDate) {
    Task task = findTaskById(id);
    task.setDeadline(newDate);
    taskRepositoy.save(task);
  }

  public void updatePriority(Long id, Priority priority) {
    Task task = findTaskById(id);
    task.setPriority(priority);
    taskRepositoy.save(task);
  }

  // eliminar
  public void deleteTask(Long id) {
    Task task = taskRepositoy.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("No se encontro tarea"));
    taskRepositoy.delete(task);
  }

  // helper
  private Task findTaskByName(String name) {
    Task task = taskRepositoy.findTaskByName(name)
        .orElseThrow(() -> new EntityNotFoundException("No se encontro tarea"));
    return task;
  }

  private Task findTaskById(Long id) {
    Task task = taskRepositoy.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Tarea no Encontrada"));
    return task;
  }

  private List<TaskDto> mapToDto(List<Task> tasks) {
    return tasks.stream()
        .map(TaskDto::toDto)
        .collect(Collectors.toList());
  }

  private void validarFachas(LocalDateTime dateCreation, LocalDateTime deadline) {
    if (deadline.isAfter(dateCreation)) {
      throw new IllegalArgumentException("La fecha limite no debe ser menor a la de creacion");
    }

  }
}

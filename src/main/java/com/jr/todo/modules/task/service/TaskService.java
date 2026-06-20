package com.jr.todo.modules.task.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

import com.jr.todo.modules.category.entity.Category;
import com.jr.todo.modules.category.repository.CategoryRepository;
import com.jr.todo.modules.task.dto.TaskDto;
import com.jr.todo.modules.task.entity.Task;
import com.jr.todo.modules.task.enums.Priority;
import com.jr.todo.modules.task.enums.Status;
import com.jr.todo.modules.task.repository.TaskRepositoy;
import com.jr.todo.util.TextFormat;
import jakarta.persistence.EntityNotFoundException;

@Service
public class TaskService implements ITaskService {
  private final TaskRepositoy taskRepositoy;
  private final CategoryRepository categoryRepository;

  private TaskService(TaskRepositoy taskRepositoy, CategoryRepository categoryRepository) {
    this.taskRepositoy = taskRepositoy;
    this.categoryRepository = categoryRepository;
  }

  /*----------------------
  Crear
  ----------------------*/
  public TaskDto createTask(TaskDto taskDto) {

    // Tarea unica
    if (taskRepositoy.existByName(taskDto.name())) {
      throw new IllegalArgumentException("Tarea repetida");
    }

    // Validar fechas
    validarFachas(taskDto.deadline());

    // Buscar la categoría por nombre (solo si existe)
    Category category = taskDto.category() != null ? findCategoryByName(taskDto.category()) : null;

    Task task = taskDto.toEntity();
    task.setName(TextFormat.nameFormat(task.getName()));

    task.setCategory(category);
    Task saveTask = taskRepositoy.save(task);
    saveTask.setDateCreation(LocalDateTime.now());
    return TaskDto.toDto(saveTask);
  }

  /*----------------------
  Find
  ----------------------*/
  public List<TaskDto> getAllTaks() {
    List<Task> tasks = taskRepositoy.findAll();
    return mapToDto(tasks);
  }

  public List<TaskDto> getAllByCategory(String name) {
    String findName = TextFormat.nameFormat(name);
    List<Task> tasks = taskRepositoy.findAllByCategory(findName);
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

  /*----------------------
  Update
  ----------------------*/
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
    validarFachas(newDate);
    task.setDeadline(newDate);
    taskRepositoy.save(task);
  }

  public void updatePriority(Long id, Priority priority) {
    Task task = findTaskById(id);
    task.setPriority(priority);
    taskRepositoy.save(task);
  }

  public void updateCategory(Long id, String name) {
    Task task = findTaskById(id);
    Category category = findCategoryByName(name);
    task.setCategory(category);
    taskRepositoy.save(task);
  }

  /*----------------------
  Delete
  ----------------------*/
  public void deleteTask(Long id) {
    Task task = findTaskById(id);
    taskRepositoy.delete(task);
  }

  /*----------------------
  Helpers
  ----------------------*/
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

  private Category findCategoryByName(String name) {
    String newname = TextFormat.nameFormat(name);
    Category category = categoryRepository.findByName(newname)
        .orElseThrow(() -> new EntityNotFoundException("Categoría inexistente: " + newname));
    return category;
  }

  private List<TaskDto> mapToDto(List<Task> tasks) {
    return tasks.stream()
        .map(TaskDto::toDto)
        .collect(Collectors.toList());
  }

  private void validarFachas(LocalDateTime deadline) {
    if (!deadline.isAfter(LocalDateTime.now())) {
      throw new IllegalArgumentException("La fecha límite debe ser posterior a la fecha de creación");
    }
  }
}

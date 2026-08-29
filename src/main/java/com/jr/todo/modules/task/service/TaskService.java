package com.jr.todo.modules.task.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.jr.todo.modules.category.entity.Category;
import com.jr.todo.modules.category.repository.CategoryRepository;
import com.jr.todo.modules.task.dto.TaskCreateDto;
import com.jr.todo.modules.task.dto.TaskResponseDto;
import com.jr.todo.modules.task.entity.Task;
import com.jr.todo.modules.task.enums.Priority;
import com.jr.todo.modules.task.enums.Status;
import com.jr.todo.modules.task.repository.TaskRepository;
import com.jr.todo.modules.user.entity.User;
import com.jr.todo.modules.user.repository.UserRepository;
import com.jr.todo.util.TextFormat;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
@Transactional
public class TaskService implements ITaskService {
  private final TaskRepository taskRepository;
  private final CategoryRepository categoryRepository;
  private final UserRepository userRepository;

  public TaskService(TaskRepository taskRepository, CategoryRepository categoryRepository, UserRepository userRepository) {
    this.taskRepository = taskRepository;
    this.categoryRepository = categoryRepository;
    this.userRepository = userRepository;
  }

  private User currentUser() {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    return userRepository.findByEmail(email)
        .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
  }

  /*----------------------
  Crear
  ----------------------*/
  @Override
  public TaskResponseDto createTask(TaskCreateDto taskDto) {
    User user = currentUser();
    String taskName = TextFormat.nameFormat(taskDto.name());

    // Tarea unica por usuario
    if (taskRepository.existsByNameAndUserId(taskName, user.getId())) {
      throw new IllegalArgumentException("Tarea repetida");
    }

    // Buscar la categoría por nombre del usuario (solo si existe)
    Category category = taskDto.category() != null ? findCategoryByName(taskDto.category()) : null;

    Task task = taskDto.toEntity();
    task.setName(taskName);
    task.setCategory(category);
    task.setUser(user);
    task.setDateCreation(LocalDateTime.now());

    // validar fecha
    validarFachas(task.getDateCreation(), taskDto.deadline());
    Task saveTask = taskRepository.save(task);
    return TaskResponseDto.toDto(saveTask);
  }

  /*----------------------
  Find
  ----------------------*/
  @Override
  public List<TaskResponseDto> getAllTaks() {
    List<Task> tasks = taskRepository.findAllByUserId(currentUser().getId());
    return mapToDto(tasks);
  }

  @Override
  public List<TaskResponseDto> getAllByCategory(String name) {
    String findName = TextFormat.nameFormat(name);
    List<Task> tasks = taskRepository.findAllByCategoryAndUserId(findName, currentUser().getId());
    return mapToDto(tasks);
  }

  @Override
  public TaskResponseDto getTaskByName(String name) {
    String nameNorma = TextFormat.nameFormat(name);
    Task task = findTaskByName(nameNorma);
    return TaskResponseDto.toDto(task);
  }

  @Override
  public List<TaskResponseDto> getAllTaskByDate(LocalDateTime date) {
    List<Task> tasks = taskRepository.findTasksByDateAndUserId(date, currentUser().getId());
    return mapToDto(tasks);
  }

  @Override
  public List<TaskResponseDto> getAllTaskByStatus(Status status) {
    List<Task> tasks = taskRepository.findTasksByStatusAndUserId(status, currentUser().getId());
    return mapToDto(tasks);
  }

  @Override
  public List<TaskResponseDto> getAllTaskByPriority(Priority priority) {
    List<Task> tasks = taskRepository.findTasksByPriorityAndUserId(priority, currentUser().getId());
    return mapToDto(tasks);
  }

  /*----------------------
  Update
  ----------------------*/
  @Override
  public void updateTaskName(Long id, String newName) {
    String newNameNorma = TextFormat.nameFormat(newName);
    Task task = findTaskById(id);
    task.setName(newNameNorma);
    taskRepository.save(task);
  }

  @Override
  public void updateTaskDescription(Long id, String newDescription) {
    Task task = findTaskById(id);
    task.setDescription(newDescription);
    taskRepository.save(task);
  }

  @Override
  public void updateTaskStatus(Long id, Status status) {
    Task task = findTaskById(id);
    task.setStatus(status);
    taskRepository.save(task);
  }

  @Override
  public void updateDeadline(Long id, LocalDateTime newDate) {
    Task task = findTaskById(id);
    validarFachas(task.getDateCreation(), newDate);
    task.setDeadline(newDate);
    taskRepository.save(task);
  }

  @Override
  public void updatePriority(Long id, Priority priority) {
    Task task = findTaskById(id);
    task.setPriority(priority);
    taskRepository.save(task);
  }

  @Override
  public void updateCategory(Long id, String name) {
    Task task = findTaskById(id);
    Category category = findCategoryByName(name);
    task.setCategory(category);
    taskRepository.save(task);
  }

  /*----------------------
  Delete
  ----------------------*/
  @Override
  public void deleteTask(Long id) {
    Task task = findTaskById(id);
    taskRepository.delete(task);
  }

  /*----------------------
  Helpers
  ----------------------*/
  private Task findTaskByName(String name) {
    Long userId = currentUser().getId();
    Task task = taskRepository.findTaskByNameAndUserId(name, userId)
        .orElseThrow(() -> new EntityNotFoundException("No se encontro tarea"));
    return task;
  }

  private Task findTaskById(Long id) {
    Long userId = currentUser().getId();
    Task task = taskRepository.findByTaskIdAndUserId(id, userId)
        .orElseThrow(() -> new EntityNotFoundException("Tarea no Encontrada"));
    return task;
  }

  private Category findCategoryByName(String name) {
    String newname = TextFormat.nameFormat(name);
    Long userId = currentUser().getId();
    Category category = categoryRepository.findByNameAndUserId(newname, userId)
        .orElseThrow(() -> new EntityNotFoundException("Categoría inexistente: " + newname));
    return category;
  }

  private List<TaskResponseDto> mapToDto(List<Task> tasks) {
    return tasks.stream()
        .map(TaskResponseDto::toDto)
        .collect(Collectors.toList());
  }

  private void validarFachas(LocalDateTime dateCreation, LocalDateTime deadline) {

    if (deadline == null) {
      return;
    }
    if (dateCreation.isAfter(deadline)) {
      throw new IllegalArgumentException("La fecha de finalizacion no puede ser anterior a la de finalizacion");
    }
  }
}

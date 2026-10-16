package com.jr.todo.modules.task.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.jr.todo.dto.PagedResponse;
import com.jr.todo.modules.category.entity.Category;
import com.jr.todo.modules.category.repository.CategoryRepository;
import com.jr.todo.modules.task.dto.TaskCreateDto;
import com.jr.todo.modules.task.dto.TaskResponseDto;
import com.jr.todo.modules.task.entity.Task;
import com.jr.todo.modules.task.enums.Priority;
import com.jr.todo.modules.task.enums.Status;
import com.jr.todo.modules.task.repository.TaskRepository;
import com.jr.todo.modules.user.entity.User;
import com.jr.todo.util.CurrentUser;
import com.jr.todo.util.DateValidationsHelper;
import com.jr.todo.util.PaginationUtil;
import com.jr.todo.util.TextFormat;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class TaskService implements ITaskService {
  private final TaskRepository taskRepository;
  private final CategoryRepository categoryRepository;
  private final CurrentUser currentUser;

  public TaskService(TaskRepository taskRepository,
      CategoryRepository categoryRepository,
      CurrentUser currentUser) {
    this.taskRepository = taskRepository;
    this.categoryRepository = categoryRepository;
    this.currentUser = currentUser;
  }

  /*----------------------
  Crear
  ----------------------*/
  @Override
  public TaskResponseDto createTask(TaskCreateDto taskDto) {
    User user = currentUser.currentUser();
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
  public PagedResponse<TaskResponseDto> getAllTaks(int page) {
    Long id = currentUser.currentUser().getId();
    Pageable pageable = PaginationUtil.of(page);

    Page<Task> taskPage = taskRepository.findAllByUserId(id, pageable);
    Page<TaskResponseDto> dtoPage = taskPage.map(TaskResponseDto :: toDto);

    return PaginationUtil.toResponse(dtoPage);
  }

  @Override
  public List<TaskResponseDto> getAllByCategory(String name) {
    String findName = TextFormat.nameFormat(name);
    List<Task> tasks = taskRepository.findAllByCategoryAndUserId(findName, currentUser.currentUser().getId());
    return mapToDto(tasks);
  }

  @Override
  public List<TaskResponseDto> getTaskByName(String name) {
    String nameNorma = TextFormat.nameFormat(name.strip());
    List<Task> tasks = taskRepository.findByNameStartingWithAndUserId(nameNorma,
        currentUser.currentUser().getId());
    return mapToDto(tasks);
  }

  @Override
  public List<TaskResponseDto> getAllTaskByDate(LocalDate dateInit, LocalDate dateFinish) {
    DateValidationsHelper.dateValidation(dateInit, dateFinish);
    long userId = currentUser.currentUser().getId();
    LocalDateTime start = dateInit.atStartOfDay();
    LocalDateTime end = dateFinish.atTime(LocalTime.MAX);

    List<Task> tasks = taskRepository.findTasksByDateAndUserId(start, end, userId);
    return mapToDto(tasks);
  }

  @Override
  public List<TaskResponseDto> getAllTaskByStatus(Status status) {
    List<Task> tasks = taskRepository.findTasksByStatusAndUserId(status, currentUser.currentUser().getId());
    return mapToDto(tasks);
  }

  @Override
  public List<TaskResponseDto> getAllTaskByPriority(Priority priority) {
    List<Task> tasks = taskRepository.findTasksByPriorityAndUserId(priority, currentUser.currentUser().getId());
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
    Long userId = currentUser.currentUser().getId();
    Task task = taskRepository.findTaskByNameAndUserId(name, userId)
        .orElseThrow(() -> new EntityNotFoundException("No se encontro tarea"));
    return task;
  }

  private Task findTaskById(Long id) {
    Long userId = currentUser.currentUser().getId();
    Task task = taskRepository.findByTaskIdAndUserId(id, userId)
        .orElseThrow(() -> new EntityNotFoundException("Tarea no Encontrada"));
    return task;
  }

  private Category findCategoryByName(String name) {
    String newname = TextFormat.nameFormat(name);
    Long userId = currentUser.currentUser().getId();
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

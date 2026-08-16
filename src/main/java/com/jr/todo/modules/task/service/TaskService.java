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
import com.jr.todo.modules.task.repository.TaskRepository;
import com.jr.todo.util.TextFormat;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class TaskService implements ITaskService {
  private final TaskRepository taskRepository;
  private final CategoryRepository categoryRepository;

  public TaskService(TaskRepository taskRepository, CategoryRepository categoryRepository) {
    this.taskRepository = taskRepository;
    this.categoryRepository = categoryRepository;
  }

  /*----------------------
  Crear
  ----------------------*/
  @Override
  public TaskDto createTask(TaskDto taskDto) {
    String taskName = TextFormat.nameFormat(taskDto.name());

    // Tarea unica
    if (taskRepository.existByName(taskName)) {
      throw new IllegalArgumentException("Tarea repetida");
    }

    // Buscar la categoría por nombre (solo si existe)
    Category category = taskDto.category() != null ? findCategoryByName(taskDto.category()) : null;

    Task task = taskDto.toEntity();
    task.setName(taskName);
    task.setCategory(category);
    task.setDateCreation(LocalDateTime.now());

    // validar fecha
    validarFachas(task.getDateCreation(), taskDto.deadline());
    Task saveTask = taskRepository.save(task);
    return TaskDto.toDto(saveTask);
  }

  /*----------------------
  Find
  ----------------------*/
  @Override
  public List<TaskDto> getAllTaks() {
    List<Task> tasks = taskRepository.findAll();
    return mapToDto(tasks);
  }

  @Override
  public List<TaskDto> getAllByCategory(String name) {
    String findName = TextFormat.nameFormat(name);
    List<Task> tasks = taskRepository.findAllByCategory(findName);
    return mapToDto(tasks);
  }

  @Override
  public TaskDto getTaskByName(String name) {
    String nameNorma = TextFormat.nameFormat(name);
    Task task = findTaskByName(nameNorma);
    return TaskDto.toDto(task);
  }

  @Override
  public List<TaskDto> getAllTaskByDate(LocalDateTime date) {
    List<Task> tasks = taskRepository.findTasksByDate(date);
    return mapToDto(tasks);
  }

  @Override
  public List<TaskDto> getAllTaskByStatus(Status status) {
    List<Task> tasks = taskRepository.findTasksByStatus(status);
    return mapToDto(tasks);
  }

  @Override
  public List<TaskDto> getAllTaskByPriority(Priority priority) {
    List<Task> tasks = taskRepository.findTasksByPriority(priority);
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
    Task task = taskRepository.findTaskByName(name)
        .orElseThrow(() -> new EntityNotFoundException("No se encontro tarea"));
    return task;
  }

  private Task findTaskById(Long id) {
    Task task = taskRepository.findById(id)
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

  private void validarFachas(LocalDateTime dateCreation, LocalDateTime deadline) {

    if (deadline == null) {
      return;
    }
    if (dateCreation.isAfter(deadline)) {
      throw new IllegalArgumentException("La fecha de finalizacion no puede ser anterior a la de finalizacion");
    }
  }
}

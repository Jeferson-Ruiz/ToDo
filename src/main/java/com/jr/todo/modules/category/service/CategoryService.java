package com.jr.todo.modules.category.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.jr.todo.modules.category.dto.CategoryCreateDto;
import com.jr.todo.modules.category.dto.CategoryResponseDto;
import com.jr.todo.modules.category.entity.Category;
import com.jr.todo.modules.category.repository.CategoryRepository;
import com.jr.todo.modules.task.repository.TaskRepository;
import com.jr.todo.modules.user.entity.User;
import com.jr.todo.modules.user.repository.UserRepository;
import com.jr.todo.util.TextFormat;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class CategoryService implements ICategoryService {

  private final CategoryRepository categoryRepository;
  private final TaskRepository taskRepository;
  private final UserRepository userRepository;

  public CategoryService(CategoryRepository categoryRepository, TaskRepository taskRepository, UserRepository userRepository) {
    this.categoryRepository = categoryRepository;
    this.taskRepository = taskRepository;
    this.userRepository = userRepository;
  }

  private User currentUser() {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    return userRepository.findByEmail(email)
        .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
  }

  @Override
  public CategoryResponseDto createCategory(CategoryCreateDto categoryDto) {
    User user = currentUser();
    validateName(categoryDto.name(), user.getId());

    Category category = categoryDto.toEntity();
    String newName = TextFormat.nameFormat(category.getName());
    category.setName(newName);
    category.setUser(user);
    return CategoryResponseDto.toDto(categoryRepository.save(category));
  }

  @Override
  public List<CategoryResponseDto> findAll() {
    List<Category> categories = categoryRepository.findAllByUserId(currentUser().getId());
    return categories.stream()
        .map(CategoryResponseDto::toDto)
        .collect(Collectors.toList());
  }

  @Override
  public CategoryResponseDto findByName(String name) {
    Long userId = currentUser().getId();
    String normalized = TextFormat.nameFormat(name);
    Category category = categoryRepository.findByNameAndUserId(normalized, userId)
        .orElseThrow(() -> new EntityNotFoundException("Categoria no encontrada"));
    return CategoryResponseDto.toDto(category);
  }

  @Override
  public void updateName(Long id, String newName) {
    Category category = findById(id);
    category.setName(TextFormat.nameFormat(newName));
    categoryRepository.save(category);
  }

  @Override
  public void updateDescription(Long id, String description) {
    Category category = findById(id);
    String newDescription = TextFormat.validaTextNull(description);
    category.setDescription(newDescription);
    categoryRepository.save(category);
  }

  @Transactional
  @Override
  public void delete(Long id) {
    User user = currentUser();
    findById(id);
    taskRepository.disassociateTasksByCategoryAndUserId(id, user.getId());
    categoryRepository.deleteById(id);
  }

  // helpers
  private void validateName(String name, Long userId) {
    String normalized = TextFormat.nameFormat(name);
    if (categoryRepository.existsByNameAndUserId(normalized, userId)) {
      throw new IllegalArgumentException("categoria ya creada");
    }
  }

  private Category findById(Long id) {
    Long userId = currentUser().getId();
    Category category = categoryRepository.findByCategoryIdAndUserId(id, userId)
        .orElseThrow(() -> new EntityNotFoundException("Categoria no encontrada"));
    return category;
  }

}

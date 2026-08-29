package com.jr.todo.modules.category.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.jr.todo.modules.category.dto.CategoryCreateDto;
import com.jr.todo.modules.category.dto.CategoryResponseDto;
import com.jr.todo.modules.category.entity.Category;
import com.jr.todo.modules.category.repository.CategoryRepository;
import com.jr.todo.modules.task.repository.TaskRepository;
import com.jr.todo.util.TextFormat;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class CategoryService implements ICategoryService {

  private final CategoryRepository categoryRepository;
  private final TaskRepository taskRepository;

  public CategoryService(CategoryRepository categoryRepository, TaskRepository taskRepository) {
    this.categoryRepository = categoryRepository;
    this.taskRepository = taskRepository;
  }

  @Override
  public CategoryResponseDto createCategory(CategoryCreateDto categoryDto) {
    validateName(categoryDto.name());

    Category category = categoryDto.toEntity();
    String newName = TextFormat.nameFormat(category.getName());
    category.setName(newName);
    return CategoryResponseDto.toDto(categoryRepository.save(category));
  }

  @Override
  public List<CategoryResponseDto> findAll() {
    List<Category> categories = categoryRepository.findAll();
    return categories.stream()
        .map(CategoryResponseDto::toDto)
        .collect(Collectors.toList());
  }

  @Override
  public CategoryResponseDto findByName(String name) {
    Category category = categoryRepository.findByName(name)
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
    findById(id);
    taskRepository.disassociateTasksByCategory(id);
    categoryRepository.deleteById(id);
  }

  // helpers
  private void validateName(String name) {
    if (categoryRepository.existByName(name)) {
      throw new IllegalArgumentException("categoria ya creada");
    }
  }

  private Category findById(Long id) {
    Category category = categoryRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Categoria no encontrada"));
    return category;
  }

}

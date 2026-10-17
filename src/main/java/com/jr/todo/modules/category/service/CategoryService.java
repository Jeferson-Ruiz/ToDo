package com.jr.todo.modules.category.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.jr.todo.dto.PagedResponse;
import com.jr.todo.modules.category.dto.CategoryCreateDto;
import com.jr.todo.modules.category.dto.CategoryResponseDto;
import com.jr.todo.modules.category.entity.Category;
import com.jr.todo.modules.category.repository.CategoryRepository;
import com.jr.todo.modules.task.repository.TaskRepository;
import com.jr.todo.modules.user.entity.User;
import com.jr.todo.util.CurrentUser;
import com.jr.todo.util.DateValidationsHelper;
import com.jr.todo.util.PaginationUtil;
import com.jr.todo.util.TextFormat;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class CategoryService implements ICategoryService {

  private final CategoryRepository categoryRepository;
  private final TaskRepository taskRepository;
  private final CurrentUser currentUser;

  public CategoryService(CategoryRepository categoryRepository,
      TaskRepository taskRepository,
      CurrentUser currentUser) {
    this.categoryRepository = categoryRepository;
    this.taskRepository = taskRepository;
    this.currentUser = currentUser;
  }

  @Override
  public CategoryResponseDto createCategory(CategoryCreateDto categoryDto) {
    User user = currentUser.currentUser();
    validateName(categoryDto.name(), user.getId());

    Category category = categoryDto.toEntity();
    String newName = TextFormat.nameFormat(category.getName());
    category.setName(newName);
    category.setUser(user);
    return CategoryResponseDto.toDto(categoryRepository.save(category));
  }

  @Override
  public PagedResponse<CategoryResponseDto> findAll(int page) {
    long id = currentUser.currentUser().getId();
    Pageable pageable = PaginationUtil.of(page);

    Page<Category> categoryPage = categoryRepository.findAllByUserId(id, pageable);
    Page<CategoryResponseDto> dtoPage = categoryPage.map(CategoryResponseDto::toDto);

    return PaginationUtil.toResponse(dtoPage);
  }

  @Override
  public List<CategoryResponseDto> findAllCategoriesByDates(LocalDate dateInit, LocalDate dateFinish) {
    DateValidationsHelper.dateValidation(dateInit, dateFinish);

    long userId = currentUser.currentUser().getId();
    LocalDateTime start = dateInit.atStartOfDay();
    LocalDateTime end = dateFinish.atTime(LocalTime.MAX);

    List<Category> categoriesList = categoryRepository.findByDateAndUserId(userId, start, end);
    return toDto(categoriesList);
  }

  @Override
  public List<CategoryResponseDto> findByName(String name) {
    Long userId = currentUser.currentUser().getId();
    String normalized = TextFormat.nameFormat(name.strip());
    List<Category> categories = categoryRepository.findByNameStartingWithAndUserId(normalized, userId);
    return toDto(categories);
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
    User user = currentUser.currentUser();
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
    Long userId = currentUser.currentUser().getId();
    Category category = categoryRepository.findByCategoryIdAndUserId(id, userId)
        .orElseThrow(() -> new EntityNotFoundException("Categoria no encontrada"));
    return category;
  }

  private List<CategoryResponseDto> toDto(List<Category> categories) {
    return categories.stream()
        .map(CategoryResponseDto::toDto)
        .collect(Collectors.toList());
  }

}

package com.jr.todo.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.jr.todo.dto.CategoryDto;
import com.jr.todo.entity.Category;
import com.jr.todo.repository.CategoryRepository;
import com.jr.todo.util.NameFormat;
import jakarta.persistence.EntityNotFoundException;

@Service
public class CategoryService {

  private final CategoryRepository categoryRepository;

  public CategoryService(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  public CategoryDto createCategory(CategoryDto categoryDto) {
    validateName(categoryDto.name());

    Category category = categoryDto.toEntity();
    String newName = NameFormat.format(category.getName());
    category.setName(newName);
    return CategoryDto.toDto(categoryRepository.save(category));
  }

  public List<CategoryDto> findAll() {
    List<Category> categories = categoryRepository.findAll();
    return categories.stream()
        .map(CategoryDto::toDto)
        .collect(Collectors.toList());
  }

  public CategoryDto findByName(String name) {
    Category category = categoryRepository.findByName(name)
        .orElseThrow(() -> new EntityNotFoundException("Categoria no encontrada"));
    return CategoryDto.toDto(category);
  }

  public void updateName(Long id, String newName) {
    Category category = findById(id);
    category.setName(NameFormat.format(newName));
    categoryRepository.save(category);
  }

  public void delete(Long id) {
    Category category = findById(id);
    categoryRepository.delete(category);
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

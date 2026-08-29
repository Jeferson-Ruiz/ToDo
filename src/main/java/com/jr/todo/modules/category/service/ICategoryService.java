package com.jr.todo.modules.category.service;

import java.util.List;
import com.jr.todo.modules.category.dto.CategoryCreateDto;
import com.jr.todo.modules.category.dto.CategoryResponseDto;

public interface ICategoryService {

  CategoryResponseDto createCategory(CategoryCreateDto categoryDto);

  List<CategoryResponseDto> findAll();

  CategoryResponseDto findByName(String name);

  void updateName(Long id, String newName);

  void updateDescription(Long id, String description);

  void delete(Long id);

}

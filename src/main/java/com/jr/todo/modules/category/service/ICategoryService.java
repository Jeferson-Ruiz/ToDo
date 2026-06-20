package com.jr.todo.modules.category.service;

import java.util.List;
import com.jr.todo.modules.category.dto.CategoryDto;

public interface ICategoryService {

  CategoryDto createCategory(CategoryDto categoryDto);

  List<CategoryDto> findAll();

  CategoryDto findByName(String name);

  void updateName(Long id, String newName);

  void updateDescription(Long id, String description);

  void delete(Long id);

}

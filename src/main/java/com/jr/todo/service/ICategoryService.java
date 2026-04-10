package com.jr.todo.service;

import java.util.List;
import com.jr.todo.dto.CategoryDto;

public interface ICategoryService {

  CategoryDto createCategory(CategoryDto categoryDto);

  List<CategoryDto> findAll();

  CategoryDto findByName(String name);

  void updateName(Long id, String newName);

  void delete(Long id);

}

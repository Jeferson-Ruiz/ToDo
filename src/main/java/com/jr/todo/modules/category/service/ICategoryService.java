package com.jr.todo.modules.category.service;

import java.time.LocalDate;
import java.util.List;
import com.jr.todo.dto.PagedResponse;
import com.jr.todo.modules.category.dto.CategoryCreateDto;
import com.jr.todo.modules.category.dto.CategoryResponseDto;

public interface ICategoryService {

  CategoryResponseDto createCategory(CategoryCreateDto categoryDto);

  PagedResponse<CategoryResponseDto> findAll(int page);

  List<CategoryResponseDto> findByName(String name);

  void updateName(Long id, String newName);

  void updateDescription(Long id, String description);

  void delete(Long id);

  List<CategoryResponseDto> findAllCategoriesByDates(LocalDate dateInit, LocalDate dateFinish);

}

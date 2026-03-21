package com.jr.todo.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.jr.todo.dto.CategoryDto;
import com.jr.todo.dto.NameDto;
import com.jr.todo.service.CategoryService;

@Controller
@RequestMapping("/category")
public class CategoryController {

  private final CategoryService categoryService;

  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  @PostMapping("/create")
  public ResponseEntity<?> create(@RequestBody CategoryDto newcategoryDto) {
    CategoryDto categoryDto = categoryService.createCategory(newcategoryDto);
    return ResponseEntity.ok(categoryDto);
  }

  @GetMapping("/all")
  public ResponseEntity<?> getAll() {
    List<CategoryDto> categoryDto = categoryService.findAll();
    return ResponseEntity.ok(categoryDto);
  }

  @GetMapping("/name")
  public ResponseEntity<?> getByName(@RequestBody NameDto request) {
    CategoryDto category = categoryService.findByName(request.name());
    return ResponseEntity.ok(category);
  }

  @PatchMapping("/update/{id}")
  public ResponseEntity<?> updateName(@PathVariable Long id, @RequestBody NameDto request) {
    categoryService.updateName(id, request.name());
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<?> delete(@PathVariable Long id) {
    categoryService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
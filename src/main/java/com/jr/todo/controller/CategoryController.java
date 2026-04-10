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
import com.jr.todo.dto.DataDto;
import com.jr.todo.service.ICategoryService;

@Controller
@RequestMapping("/category")
public class CategoryController {

  private final ICategoryService categoryService;

  public CategoryController(ICategoryService categoryService) {
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
  public ResponseEntity<?> getByName(@RequestBody DataDto name) {
    CategoryDto category = categoryService.findByName(name.data());
    return ResponseEntity.ok(category);
  }

  @PatchMapping("/update/{id}")
  public ResponseEntity<?> updateName(@PathVariable Long id, @RequestBody DataDto name) {
    categoryService.updateName(id, name.data());
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<?> delete(@PathVariable Long id) {
    categoryService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
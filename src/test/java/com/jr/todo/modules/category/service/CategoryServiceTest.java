package com.jr.todo.modules.category.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.jr.todo.DataProvider;
import com.jr.todo.modules.category.dto.CategoryDto;
import com.jr.todo.modules.category.entity.Category;
import com.jr.todo.modules.category.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void testCreateCategoryExistName() {
        CategoryDto category = new CategoryDto("Estudiar", "tematica estudio");
        when(categoryRepository.existByName("Estudiar")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            categoryService.createCategory(category);
        });
    }

    @Test
    void testCreateCategory() {
        CategoryDto categoryDto = new CategoryDto("estudiar", "tematica");
        Category savedCategory = new Category(1L, "Estudiar", "tematica", LocalDateTime.now(), null);

        when(categoryRepository.existByName("estudiar")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        CategoryDto result = categoryService.createCategory(categoryDto);

        assertNotNull(result);
        assertEquals("Estudiar", result.name());
        assertEquals("tematica", result.description());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void testCreateCategoryFormatsName() {
        CategoryDto categoryDto = new CategoryDto("  eSTuDiAr  ", "tematica");

        when(categoryRepository.existByName(anyString())).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        categoryService.createCategory(categoryDto);

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(captor.capture());
        assertEquals("Estudiar", captor.getValue().getName());
    }

    @Test
    void testFindAll() {
        when(categoryRepository.findAll()).thenReturn(DataProvider.listCategoryDtosMock());
        List<CategoryDto> result = categoryService.findAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("Compras", result.get(0).name());
    }

    @Test
    void testFindByName() {
        when(categoryRepository.findByName(anyString())).thenReturn(Optional.of(DataProvider.categoryMock()));
        CategoryDto result = categoryService.findByName(anyString());

        assertEquals("Compras", result.name());
        assertNotNull(result);
    }

    @Test
    void testFindByNameError() {
        when(categoryRepository.findByName(anyString())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> {
            categoryService.findByName(anyString());
        });
    }

    @Test
    void testUpdateName() {
        Long id = 1L;
        when(categoryRepository.findById(id)).thenReturn(Optional.of(DataProvider.categoryMock()));

        categoryService.updateName(id, " eStuDiar ");

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(captor.capture());
        assertEquals("Estudiar", captor.getValue().getName());
    }

    @Test
    void testUpdateDescription() {
        Long id = 1L;
        String description = "Descripcion";
        when(categoryRepository.findById(id)).thenReturn(Optional.of(DataProvider.categoryMock()));

        categoryService.updateDescription(id, description);

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(captor.capture());
        assertEquals("descripcion", captor.getValue().getDescription());
    }

}

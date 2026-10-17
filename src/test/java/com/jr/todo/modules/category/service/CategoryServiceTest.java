 package com.jr.todo.modules.category.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.jr.todo.DataProviderCategory;
import com.jr.todo.dto.PagedResponse;
import com.jr.todo.modules.category.dto.CategoryCreateDto;
import com.jr.todo.modules.category.dto.CategoryResponseDto;
import com.jr.todo.modules.category.entity.Category;
import com.jr.todo.modules.category.repository.CategoryRepository;
import com.jr.todo.modules.task.repository.TaskRepository;
import com.jr.todo.util.CurrentUser;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CurrentUser currentUser;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    void setupSecurity() {
        lenient().when(currentUser.currentUser()).thenReturn(DataProviderCategory.userMock());
    }

    @Test
    void testCreateCategoryExistName() {
        CategoryCreateDto category = new CategoryCreateDto("Estudiar", "tematica estudio");
        when(categoryRepository.existsByNameAndUserId(eq("Estudiar"), anyLong())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            categoryService.createCategory(category);
        });
    }

    @Test
    void testCreateCategory() {
        CategoryCreateDto categoryDto = new CategoryCreateDto("estudiar", "tematica");
        Category savedCategory = new Category(1L, "Estudiar", "tematica", LocalDateTime.now(), DataProviderCategory.userMock(), null);

        when(categoryRepository.existsByNameAndUserId(eq("Estudiar"), anyLong())).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        CategoryResponseDto result = categoryService.createCategory(categoryDto);

        assertNotNull(result);
        assertEquals("Estudiar", result.name());
        assertEquals("tematica", result.description());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void testCreateCategoryFormatsName() {
        CategoryCreateDto categoryDto = new CategoryCreateDto("  eSTuDiAr  ", "tematica");

        when(categoryRepository.existsByNameAndUserId(anyString(), anyLong())).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        categoryService.createCategory(categoryDto);

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(captor.capture());
        assertEquals("Estudiar", captor.getValue().getName());
    }

    @Test
    void testFindAll() {
        List<Category> categories = DataProviderCategory.listCategoryDtosMock();
        Page<Category> categoryPage = new PageImpl<>(categories, PageRequest.of(0, 5), categories.size());
        when(categoryRepository.findAllByUserId(anyLong(), any(Pageable.class))).thenReturn(categoryPage);
        PagedResponse<CategoryResponseDto> result = categoryService.findAll(0);

        assertNotNull(result);
        assertFalse(result.content().isEmpty());
        assertEquals("Compras", result.content().get(0).name());
        verify(categoryRepository).findAllByUserId(anyLong(), any(Pageable.class));
    }

    @Test
    void testFindByName() {
        when(categoryRepository.findByNameStartingWithAndUserId(anyString(), anyLong()))
                .thenReturn(DataProviderCategory.listCategoryDtosMock());
        List<CategoryResponseDto> result = categoryService.findByName("Comp");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("Compras", result.get(0).name());
    }

    @Test
    void testFindByNameEmpty() {
        when(categoryRepository.findByNameStartingWithAndUserId(anyString(), anyLong())).thenReturn(List.of());
        List<CategoryResponseDto> result = categoryService.findByName("Inexistente");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testUpdateName() {
        Long id = 1L;
        when(categoryRepository.findByCategoryIdAndUserId(eq(id), anyLong())).thenReturn(Optional.of(DataProviderCategory.categoryMock()));

        categoryService.updateName(id, " eStuDiar ");

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(captor.capture());
        assertEquals("Estudiar", captor.getValue().getName());
    }

    @Test
    void testUpdateNameNotFound() {
        Long id = 1L;
        when(categoryRepository.findByCategoryIdAndUserId(eq(id), anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> categoryService.updateName(id, "nuevo"));
    }

    @Test
    void testUpdateDescription() {
        Long id = 1L;
        String description = "Descripcion";
        when(categoryRepository.findByCategoryIdAndUserId(eq(id), anyLong())).thenReturn(Optional.of(DataProviderCategory.categoryMock()));

        categoryService.updateDescription(id, description);

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(captor.capture());
        assertEquals("descripcion", captor.getValue().getDescription());
    }

    @Test
    void testUpdateDescriptionNotFound() {
        Long id = 1L;
        when(categoryRepository.findByCategoryIdAndUserId(eq(id), anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> categoryService.updateDescription(id, "descripcion"));
    }

    @Test
    void testDelete() {
        Long id = 1L;
        when(categoryRepository.findByCategoryIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(DataProviderCategory.categoryMock()));
        categoryService.delete(id);
        verify(categoryRepository).deleteById(id);
        verify(taskRepository).disassociateTasksByCategoryAndUserId(eq(id), anyLong());
    }

    @Test
    void testDeleteNotFound() {
        Long id = 1L;
        when(categoryRepository.findByCategoryIdAndUserId(eq(id), anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> categoryService.delete(id));
        verify(categoryRepository, never()).deleteById(anyLong());
    }

    @Test
    void testValidateName() {
        when(categoryRepository.existsByNameAndUserId(anyString(), anyLong())).thenReturn(true);
        assertThrows(IllegalArgumentException.class,
                () -> categoryService.createCategory(new CategoryCreateDto("prueba", "descripcion")));
    }
}

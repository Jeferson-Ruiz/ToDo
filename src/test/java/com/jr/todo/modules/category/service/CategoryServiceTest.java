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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import com.jr.todo.DataProviderCategory;
import com.jr.todo.modules.category.dto.CategoryCreateDto;
import com.jr.todo.modules.category.dto.CategoryResponseDto;
import com.jr.todo.modules.category.entity.Category;
import com.jr.todo.modules.category.repository.CategoryRepository;
import com.jr.todo.modules.task.repository.TaskRepository;
import com.jr.todo.modules.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    void setupSecurity() {
        var auth = new UsernamePasswordAuthenticationToken("pedro@correo.com", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
        lenient().when(userRepository.findByEmail("pedro@correo.com")).thenReturn(Optional.of(DataProviderCategory.userMock()));
    }

    @AfterEach
    void clearSecurity() {
        SecurityContextHolder.clearContext();
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
        when(categoryRepository.findAllByUserId(anyLong())).thenReturn(DataProviderCategory.listCategoryDtosMock());
        List<CategoryResponseDto> result = categoryService.findAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("Compras", result.get(0).name());
    }

    @Test
    void testFindByName() {
        when(categoryRepository.findByNameAndUserId(anyString(), anyLong())).thenReturn(Optional.of(DataProviderCategory.categoryMock()));
        CategoryResponseDto result = categoryService.findByName("Compras");

        assertEquals("Compras", result.name());
        assertNotNull(result);
    }

    @Test
    void testFindByNameError() {
        when(categoryRepository.findByNameAndUserId(anyString(), anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> {
            categoryService.findByName("Inexistente");
        });
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

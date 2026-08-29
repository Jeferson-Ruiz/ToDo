package com.jr.todo.modules.task.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.jr.todo.DataProviderCategory;
import com.jr.todo.DataProviderTask;
import com.jr.todo.modules.category.repository.CategoryRepository;
import com.jr.todo.modules.task.dto.TaskCreateDto;
import com.jr.todo.modules.task.dto.TaskResponseDto;
import com.jr.todo.modules.task.entity.Task;
import com.jr.todo.modules.task.enums.Priority;
import com.jr.todo.modules.task.enums.Status;
import com.jr.todo.modules.task.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void testCreateTaskErrorName() {
        TaskCreateDto taskDto = new TaskCreateDto("prueba", "prueba", Status.FINALIZADA, null, Priority.ALTA, null);
        when(taskRepository.existByName(anyString())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            taskService.createTask(taskDto);
        });
    }

    @Test
    void testCreateTaskErrorCategory() {
        TaskCreateDto taskDto = new TaskCreateDto("prueba", "prueba", Status.FINALIZADA, null, Priority.ALTA, "Inexistente");
        when(taskRepository.existByName(anyString())).thenReturn(false);
        when(categoryRepository.findByName("Inexistente")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            taskService.createTask(taskDto);
        });
    }

    @Test
    void testCreateTaskSuccess() {
        TaskCreateDto taskDto = new TaskCreateDto("prueba", "prueba", Status.FINALIZADA, null, Priority.ALTA, null);

        when(taskRepository.existByName(anyString())).thenReturn(false);
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        TaskResponseDto result = taskService.createTask(taskDto);

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());
        assertEquals("Prueba", captor.getValue().getName());
        assertEquals("Prueba", result.name());
    }

    @Test
    void testCreateTaskNameBlank() {
        TaskCreateDto taskDto = new TaskCreateDto(" ", "prueba", Status.FINALIZADA, null, Priority.ALTA, null);

        assertThrows(IllegalArgumentException.class, () -> {
            taskService.createTask(taskDto);
        });

    }

    @Test
    void testCreateTaskInvalidDeadLine() {
        TaskCreateDto taskDto = new TaskCreateDto("prueba", null, Status.FINALIZADA, LocalDateTime.now().minusDays(1),
                Priority.ALTA, null);

        assertThrows(IllegalArgumentException.class, () -> {
            taskService.createTask(taskDto);
        });
    }

    @Test
    void testCreateTaskSuccessWithCategory() {
        TaskCreateDto taskDto = new TaskCreateDto("prueba", "prueba", Status.FINALIZADA, null, Priority.ALTA, "Trabajo");

        when(taskRepository.existByName(anyString())).thenReturn(false);
        when(categoryRepository.findByName("Trabajo")).thenReturn(Optional.of(DataProviderCategory.categoryMock()));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        TaskResponseDto result = taskService.createTask(taskDto);

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());
        assertEquals("Compras", captor.getValue().getCategory().getName());
        assertEquals("Compras", result.category());
    }

    @Test
    void testGetAllTask() {
        when(taskRepository.findAll()).thenReturn(DataProviderTask.listTaskMock());
        List<TaskResponseDto> result = taskService.getAllTaks();

        assertNotNull(result);
        assertEquals("Prueba", result.get(0).name());
    }

    @Test
    void testGetAllByCategory() {
        String categoryName = "Trabajo";
        when(taskRepository.findAllByCategory(anyString())).thenReturn(DataProviderTask.listTaskMock());
        List<TaskResponseDto> result = taskService.getAllByCategory(categoryName);
        assertNotNull(result);
        verify(taskRepository).findAllByCategory(categoryName);
        assertEquals("Prueba", result.get(0).name());
    }

    @Test
    void testGetAllByCategoryNameBlank() {
        String categoryName = " ";
        assertThrows(IllegalArgumentException.class, () -> {
            taskService.getAllByCategory(categoryName);
        });
    }

    @Test
    void testGetTaskByName() {
        String taskName = "prueba";
        when(taskRepository.findTaskByName(anyString())).thenReturn(Optional.of(DataProviderTask.taskMock()));
        TaskResponseDto taskDto = taskService.getTaskByName(taskName);

        assertEquals("Prueba", taskDto.name());
        assertNotNull(taskDto);
        verify(taskRepository).findTaskByName("Prueba");
    }

    @Test
    void testGetTaskByNameError() {
        String taskName = "";
        assertThrows(IllegalArgumentException.class, () -> {
            taskService.getTaskByName(taskName);
        });
    }

    @Test
    void testGetTaskByNameEntityNotFound() {
        when(taskRepository.findTaskByName(anyString())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> {
            taskService.getTaskByName("prueba");
        });
    }

    @Test
    void testGetAllTaskByDate() {
        LocalDateTime date = LocalDateTime.now();
        when(taskRepository.findTasksByDate(any())).thenReturn(DataProviderTask.listTaskMock());
        List<TaskResponseDto> result = taskService.getAllTaskByDate(date);

        assertNotNull(result);
        verify(taskRepository).findTasksByDate(date);
        assertEquals("Prueba", result.get(0).name());
    }

    @Test
    void testGetAllTaskByStatus() {
        Status status = Status.FINALIZADA;
        when(taskRepository.findTasksByStatus(any())).thenReturn(DataProviderTask.listTaskMock());
        List<TaskResponseDto> result = taskService.getAllTaskByStatus(status);
        assertNotNull(result);
        verify(taskRepository).findTasksByStatus(status);
        assertEquals("Prueba", result.get(0).name());
    }

    @Test
    void testGetAllTaskByPriority() {
        Priority priority = Priority.ALTA;
        when(taskRepository.findTasksByPriority(any())).thenReturn(DataProviderTask.listTaskMock());
        List<TaskResponseDto> result = taskService.getAllTaskByPriority(priority);

        assertNotNull(result);
        verify(taskRepository).findTasksByPriority(priority);
        assertEquals("Prueba", result.get(0).name());
    }

    @Test
    void testUpdateTaskName() {
        Long id = 1L;
        String newName = "update";
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(DataProviderTask.taskMock()));
        taskService.updateTaskName(id, newName);

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).findById(id);
        verify(taskRepository).save(captor.capture());
        assertEquals("Update", captor.getValue().getName());
    }

    @Test
    void testUpdateTaskNameErro() {
        Long id = 1L;
        String newName = "prueba";
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> {
            taskService.updateTaskName(id, newName);
        });
    }

    @Test
    void testUpdateTaskNameBlank() {
        Long id = 1L;
        String newName = " ";

        assertThrows(IllegalArgumentException.class, () -> {
            taskService.updateTaskName(id, newName);
        });
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testUpdateTaskDescription() {
        Long id = 1L;
        String description = "prueba";
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(DataProviderTask.taskMock()));
        taskService.updateTaskDescription(id, description);

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).findById(id);
        verify(taskRepository).save(captor.capture());
        assertEquals(description, captor.getValue().getDescription());
    }

    @Test
    void testUpdateTaskDescriptionError() {
        Long id = 1L;
        String description = "prueba";
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> {
            taskService.updateTaskDescription(id, description);
        });
    }

    @Test
    void testUpdateTaskStatus() {
        Long id = 1L;
        Status status = Status.PENDIENTE;
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(DataProviderTask.taskMock()));
        taskService.updateTaskStatus(id, status);
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).findById(id);
        verify(taskRepository).save(captor.capture());
        assertEquals(status, captor.getValue().getStatus());
    }

    @Test
    void testUpdateTaskStatusError() {
        Long id = 1L;
        Status status = Status.FINALIZADA;
        assertThrows(EntityNotFoundException.class, () -> {
            taskService.updateTaskStatus(id, status);
        });
    }

    @Test
    void testUpdateDeadline() {
        Long id = 1L;
        LocalDateTime newDate = LocalDateTime.now().plusHours(2);
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(DataProviderTask.taskMock()));
        taskService.updateDeadline(id, newDate);

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).findById(id);
        verify(taskRepository).save(captor.capture());

        assertEquals(newDate, captor.getValue().getDeadline());
    }

    @Test
    void testUpdateDeadlineNotFound() {
        Long id = 1L;
        LocalDateTime newDate = LocalDateTime.now().plusHours(1);
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> {
            taskService.updateDeadline(id, newDate);
        });
    }

    @Test
    void testUpdateDeadlineBadRequest() {
        Long id = 1L;
        LocalDateTime newDate = LocalDateTime.now().minusHours(2);
        when(taskRepository.findById(id)).thenReturn(Optional.of(DataProviderTask.taskMock()));
        assertThrows(IllegalArgumentException.class, () -> {
            taskService.updateDeadline(id, newDate);
        });
    }

    @Test
    void testUpdatePriority() {
        Long id = 1L;
        Priority newPriority = Priority.ALTA;
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(DataProviderTask.taskMock()));
        taskService.updatePriority(id, newPriority);

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).findById(id);
        verify(taskRepository).save(captor.capture());
        assertEquals(newPriority, captor.getValue().getPriority());
    }

    @Test
    void testUpdatePriorityNotFound() {
        Long id = 1L;
        Priority priority = Priority.ALTA;
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> {
            taskService.updatePriority(id, priority);
        });
    }

    @Test
    void testUpdateCategory() {
        Long id = 1L;
        String category = "Trabajo";

        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(DataProviderTask.taskMock()));
        when(categoryRepository.findByName(category)).thenReturn(Optional.of(DataProviderCategory.categoryMock()));

        taskService.updateCategory(id, category);

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).findById(id);
        verify(taskRepository).save(captor.capture());
        verify(categoryRepository).findByName(category);

        assertEquals(DataProviderCategory.categoryMock().getName(), captor.getValue().getCategory().getName());
    }

    @Test
    void testUpdateCategoryNotFound() {
        Long id = 1L;
        String category = "Trabajo";

        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(DataProviderTask.taskMock()));
        when(categoryRepository.findByName(anyString())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> {
            taskService.updateCategory(id, category);
        });
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testDeleteTask() {
        Long id = 1L;
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(DataProviderTask.taskMock()));
        taskService.deleteTask(id);
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).delete(captor.capture());
    }

    @Test
    void testDeleteTaskNotFound() {
        Long id = 1L;
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            taskService.deleteTask(id);
        });
        verify(taskRepository, never()).delete(any(Task.class));
    }
}
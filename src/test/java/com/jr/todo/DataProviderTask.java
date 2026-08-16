package com.jr.todo;

import java.time.LocalDateTime;
import java.util.List;

import com.jr.todo.modules.task.entity.Task;
import com.jr.todo.modules.task.enums.Priority;
import com.jr.todo.modules.task.enums.Status;

public class DataProviderTask {

    public static List<Task> listTaskMock() {
        return List.of(
                new Task(1L, "Prueba", "texto prueba", LocalDateTime.now(), Status.FINALIZADA, LocalDateTime.now(),
                        Priority.ALTA, null),
                new Task(2L, "Pruena2", "texto prueba2", LocalDateTime.now(), Status.PENDIENTE, null, Priority.BAJA,
                        null));
    }

    public static Task taskMock() {
        return new Task(1L, "Prueba", "texto prueba", LocalDateTime.now(), Status.FINALIZADA, LocalDateTime.now(),
                Priority.ALTA, null);
    }

}

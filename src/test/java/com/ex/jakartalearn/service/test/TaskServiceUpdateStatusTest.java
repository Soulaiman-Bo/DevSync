package com.ex.jakartalearn.service.test;

import com.ex.jakartalearn.entity.Task;
import com.ex.jakartalearn.entity.User;
import com.ex.jakartalearn.enumeration.TaskStatus;
import com.ex.jakartalearn.enumeration.UserRole;
import com.ex.jakartalearn.exceptions.UserNotFoundException;
import com.ex.jakartalearn.repository.TaskRepository;
import com.ex.jakartalearn.service.TaskService;
import com.ex.jakartalearn.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class TaskServiceUpdateStatusTest {
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private User managerUser;
    private User regularUser;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        managerUser = new User();
        managerUser.setId(1L);
        managerUser.setRole(UserRole.MANAGER);

        regularUser = new User();
        regularUser.setId(2L);
        regularUser.setRole(UserRole.USER);

        task = new Task();
        task.setId(1L);
        task.setUser(regularUser);
        task.setDueDate(now.plusDays(5));
        task.setStatus(TaskStatus.NEW);
    }

    @Test
    void shouldThrowExceptionWhenTaskNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> taskService.updateTaskStatus(1L, TaskStatus.INPROGRESS, 2L));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userService.getUserById(2L)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class,
                () -> taskService.updateTaskStatus(1L, TaskStatus.INPROGRESS, 2L));
    }

    @Test
    void shouldThrowSecurityExceptionWhenUnauthorizedUser() {
        User anotherUser = new User();
        anotherUser.setId(3L);
        anotherUser.setRole(UserRole.USER);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userService.getUserById(3L)).thenReturn(Optional.of(anotherUser));

        Assertions.assertThrows(SecurityException.class,
                () -> taskService.updateTaskStatus(1L, TaskStatus.INPROGRESS, 3L));
    }

    @Test
    void shouldThrowExceptionWhenTaskPastDueDate() {
        task.setDueDate(now.minusDays(1));

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userService.getUserById(2L)).thenReturn(Optional.of(regularUser));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> taskService.updateTaskStatus(1L, TaskStatus.INPROGRESS, 2L));
    }

    @Test
    void shouldAllowManagerToUpdateTaskStatus() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userService.getUserById(1L)).thenReturn(Optional.of(managerUser));
        when(taskRepository.update(any())).thenReturn(task);

        Task updatedTask = taskService.updateTaskStatus(1L, TaskStatus.INPROGRESS, 1L);

        Assertions.assertEquals(TaskStatus.INPROGRESS, updatedTask.getStatus());
        verify(taskRepository).update(task);
    }

    @Test
    void shouldAllowAssignedUserToUpdateTaskStatus() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userService.getUserById(2L)).thenReturn(Optional.of(regularUser));
        when(taskRepository.update(any())).thenReturn(task);

        Task updatedTask = taskService.updateTaskStatus(1L, TaskStatus.INPROGRESS, 2L);

        Assertions.assertEquals(TaskStatus.INPROGRESS, updatedTask.getStatus());
        verify(taskRepository).update(task);
    }

    @Test
    void shouldThrowExceptionWhenRepositoryUpdateFails() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userService.getUserById(2L)).thenReturn(Optional.of(regularUser));
        when(taskRepository.update(any())).thenThrow(new RuntimeException("Update failed"));

        Assertions.assertThrows(RuntimeException.class,
                () -> taskService.updateTaskStatus(1L, TaskStatus.INPROGRESS, 2L));
    }
}
package com.ex.jakartalearn.service.test;

import com.ex.jakartalearn.entity.Request;
import com.ex.jakartalearn.entity.Task;
import com.ex.jakartalearn.entity.Token;
import com.ex.jakartalearn.entity.User;
import com.ex.jakartalearn.enumeration.UserRole;
import com.ex.jakartalearn.exceptions.UserNotFoundException;
import com.ex.jakartalearn.repository.TaskRepository;
import com.ex.jakartalearn.service.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskServiceDeleteTest {
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private User managerUser;
    private User regularUser;
    private Token userToken;

    @BeforeEach
    void setUp() {
        managerUser = new User();
        managerUser.setId(1L);
        managerUser.setRole(UserRole.MANAGER);

        userToken = new Token();
        userToken.setDelete_token(1);

        regularUser = new User();
        regularUser.setId(2L);
        regularUser.setRole(UserRole.USER);
        regularUser.setToken(userToken);

        task = new Task();
        task.setId(1L);
        task.setUser(regularUser);
        task.setIsRefused(false);
    }

    @Test
    void shouldThrowExceptionWhenTaskNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(RuntimeException.class,
                () -> taskService.deleteTask(1L, 2L));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userService.getUserById(2L)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class,
                () -> taskService.deleteTask(1L, 2L));
    }

    @Test
    void shouldThrowSecurityExceptionWhenUnauthorizedUser() {
        User anotherUser = new User();
        anotherUser.setId(3L);
        anotherUser.setRole(UserRole.USER);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userService.getUserById(3L)).thenReturn(Optional.of(anotherUser));

        Assertions.assertThrows(SecurityException.class,
                () -> taskService.deleteTask(1L, 3L));
    }

    @Test
    void shouldThrowExceptionWhenTaskAlreadyRefused() {
        task.setIsRefused(true);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userService.getUserById(2L)).thenReturn(Optional.of(regularUser));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> taskService.deleteTask(1L, 2L));
    }

    @Test
    void shouldThrowExceptionWhenNoDeleteTokensLeft() {
        userToken.setDelete_token(0);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userService.getUserById(2L)).thenReturn(Optional.of(regularUser));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> taskService.deleteTask(1L, 2L));
    }

    @Test
    void shouldAllowManagerToDeleteTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userService.getUserById(1L)).thenReturn(Optional.of(managerUser));
        when(taskRepository.update(any())).thenReturn(task);
        when(tokenService.subtractDeleteToken(any())).thenReturn(userToken);

        Task deletedTask = taskService.deleteTask(1L, 1L);

        Assertions.assertTrue(deletedTask.getIsRefused());
        Assertions.assertNull(deletedTask.getUser());
        verify(taskRepository).update(task);
        verify(tokenService).subtractDeleteToken(any());
    }

    @Test
    void shouldAllowTaskOwnerToDeleteTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userService.getUserById(2L)).thenReturn(Optional.of(regularUser));
        when(taskRepository.update(any())).thenReturn(task);
        when(tokenService.subtractDeleteToken(any())).thenReturn(userToken);

        Task deletedTask = taskService.deleteTask(1L, 2L);

        Assertions.assertTrue(deletedTask.getIsRefused());
        Assertions.assertNull(deletedTask.getUser());
        verify(taskRepository).update(task);
        verify(tokenService).subtractDeleteToken(regularUser);
    }

    @Test
    void shouldThrowExceptionWhenTokenServiceFails() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userService.getUserById(2L)).thenReturn(Optional.of(regularUser));
        when(taskRepository.update(any())).thenReturn(task);
        when(tokenService.subtractDeleteToken(any())).thenThrow(new RuntimeException("Token service failed"));

        Assertions.assertThrows(RuntimeException.class,
                () -> taskService.deleteTask(1L, 2L));
    }
}
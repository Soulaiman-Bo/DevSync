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
public class TaskServiceRefuseTest {
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @Mock
    private TokenService tokenService;

    @Mock
    private RequestService requestService;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private User regularUser;
    private Token userToken;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        regularUser = new User();
        regularUser.setId(2L);
        regularUser.setRole(UserRole.USER);

        userToken = new Token();
        userToken.setRefuse_token(1);
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
                () -> taskService.refuseTask(1L, 2L));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userService.getUserById(2L)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class,
                () -> taskService.refuseTask(1L, 2L));
    }

    @Test
    void shouldThrowSecurityExceptionWhenUnauthorizedUser() {
        User anotherUser = new User();
        anotherUser.setId(3L);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userService.getUserById(3L)).thenReturn(Optional.of(anotherUser));

        Assertions.assertThrows(SecurityException.class,
                () -> taskService.refuseTask(1L, 3L));
    }

    @Test
    void shouldThrowExceptionWhenTaskAlreadyRefused() {
        task.setIsRefused(true);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userService.getUserById(2L)).thenReturn(Optional.of(regularUser));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> taskService.refuseTask(1L, 2L));
    }

    @Test
    void shouldSuccessfullyRefuseTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userService.getUserById(2L)).thenReturn(Optional.of(regularUser));
        when(taskRepository.update(any())).thenReturn(task);
        when(tokenService.subtractRefuseToken(any())).thenReturn(userToken);
        when(requestService.createRequest(any())).thenReturn(Optional.of(new Request()));

        Task refusedTask = taskService.refuseTask(1L, 2L);

        Assertions.assertTrue(refusedTask.getIsRefused());
        verify(taskRepository).update(task);
        verify(tokenService).subtractRefuseToken(regularUser);
        verify(requestService).createRequest(any());
    }

    @Test
    void shouldThrowExceptionWhenTokenServiceFails() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userService.getUserById(2L)).thenReturn(Optional.of(regularUser));
        when(taskRepository.update(any())).thenReturn(task);
        when(tokenService.subtractRefuseToken(any())).thenThrow(new RuntimeException("Token service failed"));

        Assertions.assertThrows(RuntimeException.class,
                () -> taskService.refuseTask(1L, 2L));
    }

    @Test
    void shouldThrowExceptionWhenRequestServiceFails() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userService.getUserById(2L)).thenReturn(Optional.of(regularUser));
        when(taskRepository.update(any())).thenReturn(task);
        when(tokenService.subtractRefuseToken(any())).thenReturn(userToken);
        when(requestService.createRequest(any())).thenThrow(new RuntimeException("Request service failed"));

        Assertions.assertThrows(RuntimeException.class,
                () -> taskService.refuseTask(1L, 2L));
    }
}
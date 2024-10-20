package com.ex.jakartalearn.service.test;

import com.ex.jakartalearn.entity.Tag;
import com.ex.jakartalearn.entity.Task;
import com.ex.jakartalearn.entity.User;
import com.ex.jakartalearn.enumeration.TaskStatus;
import com.ex.jakartalearn.enumeration.UserRole;
import com.ex.jakartalearn.exceptions.UserNotFoundException;
import com.ex.jakartalearn.repository.TaskRepository;
import com.ex.jakartalearn.service.TagService;
import com.ex.jakartalearn.service.TaskService;
import com.ex.jakartalearn.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.wildfly.common.Assert;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class TaskServiceCreateTest {
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @Mock
    private TagService tagService;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private User managerUser;
    private User regularUser;
    private Tag tag1;
    private Tag tag2;
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

        tag1 = new Tag();
        tag1.setName("Tag1");
        tag2 = new Tag();
        tag2.setName("Tag2");

        task = new Task();
        task.setDueDate(now.plusDays(5));
        task.setTags(Arrays.asList(tag1, tag2));
    }

    // Null Parameter Tests
    @Test
    void shouldThrowExceptionWhenTaskIsNull() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> taskService.createTask(null, 1L, 2L));
    }
    @Test
    void shouldThrowExceptionWhenAssignerIdIsNull() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> taskService.createTask(task, null, 2L));
    }

    @Test
    void shouldThrowExceptionWhenAssignedIdIsNull() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> taskService.createTask(task, 1L, null));
    }

    // User Not Found Tests
    @Test
    void shouldThrowExceptionWhenAssignerNotFound() {
        when(userService.getUserById(1L)).thenReturn(Optional.empty());
        when(userService.getUserById(2L)).thenReturn(Optional.of(regularUser));

        Assertions.assertThrows(UserNotFoundException.class,
                () -> taskService.createTask(task, 1L, 2L));
    }

    @Test
    void shouldThrowExceptionWhenAssignedUserNotFound() {
        when(userService.getUserById(1L)).thenReturn(Optional.of(managerUser));
        when(userService.getUserById(2L)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class,
                () -> taskService.createTask(task, 1L, 2L));
    }

    // Authorization Tests
    @Test
    void shouldThrowSecurityExceptionWhenRegularUserAssignsToOthers() {
        User anotherRegularUser = new User();
        anotherRegularUser.setId(3L);
        anotherRegularUser.setRole(UserRole.USER);

        when(userService.getUserById(2L)).thenReturn(Optional.of(regularUser));
        when(userService.getUserById(3L)).thenReturn(Optional.of(anotherRegularUser));

        Assertions.assertThrows(SecurityException.class,
                () -> taskService.createTask(task, 2L, 3L));
    }

    // Due Date Tests
    @Test
    void shouldThrowExceptionWhenDueDateIsInPast() {
        task.setDueDate(now.minusDays(1));

        when(userService.getUserById(1L)).thenReturn(Optional.of(managerUser));
        when(userService.getUserById(2L)).thenReturn(Optional.of(regularUser));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> taskService.createTask(task, 1L, 2L));
    }

    @Test
    void shouldThrowExceptionWhenManagerAssignsTaskWithInvalidDueDate() {
        task.setDueDate(now.plusDays(2));

        when(userService.getUserById(1L)).thenReturn(Optional.of(managerUser));
        when(userService.getUserById(2L)).thenReturn(Optional.of(regularUser));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> taskService.createTask(task, 1L, 2L));
    }

    @Test
    void shouldThrowExceptionWhenManagerAssignsTaskWithNullDueDate() {
        task.setDueDate(null);

        when(userService.getUserById(1L)).thenReturn(Optional.of(managerUser));
        when(userService.getUserById(2L)).thenReturn(Optional.of(regularUser));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> taskService.createTask(task, 1L, 2L));
    }

    @Test
    void shouldThrowExceptionWhenTagsAreNull() {
        task.setTags(null);

        when(userService.getUserById(1L)).thenReturn(Optional.of(managerUser));
        when(userService.getUserById(2L)).thenReturn(Optional.of(regularUser));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> taskService.createTask(task, 1L, 2L));
    }

    @Test
    void shouldThrowExceptionWhenLessThanTwoTags() {
        task.setTags(Collections.singletonList(tag1));

        when(userService.getUserById(1L)).thenReturn(Optional.of(managerUser));
        when(userService.getUserById(2L)).thenReturn(Optional.of(regularUser));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> taskService.createTask(task, 1L, 2L));
    }

    @Test
    void shouldThrowExceptionWhenTagServiceFails() {
        when(userService.getUserById(1L)).thenReturn(Optional.of(managerUser));
        when(userService.getUserById(2L)).thenReturn(Optional.of(regularUser));
        when(tagService.findOrCreateTag(any())).thenThrow(new RuntimeException("Tag service failed"));

        Assertions.assertThrows(RuntimeException.class,
                () -> taskService.createTask(task, 1L, 2L));
    }

    @Test
    void shouldThrowExceptionWhenRepositorySaveFails() {
        when(userService.getUserById(1L)).thenReturn(Optional.of(managerUser));
        when(userService.getUserById(2L)).thenReturn(Optional.of(regularUser));
        when(tagService.findOrCreateTag(any())).thenReturn(tag1);
        when(taskRepository.save(any())).thenThrow(new RuntimeException("Save failed"));

        Assertions.assertThrows(RuntimeException.class,
                () -> taskService.createTask(task, 1L, 2L));
    }

    @Test
    void shouldSuccessfullyCreateTaskWhenAllConditionsAreMet() {
        when(userService.getUserById(1L)).thenReturn(Optional.of(managerUser));
        when(userService.getUserById(2L)).thenReturn(Optional.of(regularUser));
        when(tagService.findOrCreateTag(any())).thenReturn(tag1);
        when(taskRepository.save(any())).thenReturn(task);

        Task result = taskService.createTask(task, 1L, 2L);

        Assert.assertNotNull(result);
        Assertions.assertEquals(TaskStatus.NEW, result.getStatus());
        Assertions.assertEquals(regularUser, result.getUser());
        Assertions.assertEquals(managerUser, result.getCreatedBy());
        verify(taskRepository).save(any(Task.class));
    }


}

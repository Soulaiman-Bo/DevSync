package com.ex.jakartalearn.service;

import com.ex.jakartalearn.entity.Request;
import com.ex.jakartalearn.entity.Task;
import com.ex.jakartalearn.entity.User;
import com.ex.jakartalearn.enumeration.UserRole;
import com.ex.jakartalearn.exceptions.UserNotFoundException;
import com.ex.jakartalearn.repository.RequestRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.Optional;

@Stateless
public class RequestService {

    @Inject
    private RequestRepository requestRepository;

    @Inject
    private UserService userService;

    @Inject
    private TaskService taskService;

    public Optional<Request> createRequest(Request request) {
        requestRepository.save(request);
        return Optional.of(request);
    }

    public Request acceptRequest(Long newAssignedUserId, Long taskId, User currentUser) {

        if (newAssignedUserId == null || taskId == null || currentUser == null) {
            throw new IllegalArgumentException("One of the Arguments is null");
        }

        if (!currentUser.getRole().equals(UserRole.MANAGER)) {
            throw new SecurityException("Only Managers have permission to accept Request");
        }

        User newAssignedUser = userService.getUserById(newAssignedUserId).orElseThrow(() -> new UserNotFoundException("Assigned user not found with ID: " + newAssignedUserId));

        if (newAssignedUser.getRole().equals(UserRole.MANAGER)) {
            throw new SecurityException("Only User have can be assigned to Tasks");
        }

        Task task = taskService.getTaskById(taskId);

        task.setUser(newAssignedUser);
        task.getRequest().setIsAccepted(Boolean.TRUE);

        taskService.updateTask(task);

        return null;
    }

}

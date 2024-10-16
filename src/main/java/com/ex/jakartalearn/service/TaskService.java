package com.ex.jakartalearn.service;

import com.ex.jakartalearn.entity.*;
import com.ex.jakartalearn.enumeration.RequestType;
import com.ex.jakartalearn.enumeration.TaskStatus;
import com.ex.jakartalearn.enumeration.UserRole;
import com.ex.jakartalearn.exceptions.TaskNotFoundException;
import com.ex.jakartalearn.exceptions.UserNotFoundException;
import com.ex.jakartalearn.repository.TaskRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Stateless
public class TaskService {
    @Inject
    private TaskRepository taskRepository;

    @Inject
    private UserService userService;

    @Inject
    private  RequestService requestService;

    @Inject
    private TagService tagService;

    @Inject
    private TokenService tokenService;


    public Task createTask(Task task, Long assignerId, Long assignedId) {
        if (task == null || assignerId == null || assignedId == null) {
            throw new IllegalArgumentException("Task, assignerId, and assignedId must not be null");
        }

        Optional<User> assigner = userService.getUserById(assignerId);
        Optional<User> assigned = userService.getUserById(assignedId);

        User assignerUser = assigner.orElseThrow(() -> new UserNotFoundException("Assigner not found with ID: " + assignerId));
        User assignedUser = assigned.orElseThrow(() -> new UserNotFoundException("User not found with ID: " + assignedId));

        if (!(assignerUser.getRole().equals(UserRole.MANAGER) || assignerUser.getId().equals(assignedUser.getId()))) {
            throw new SecurityException("Only managers or users assigning tasks to themselves can create tasks.");
        }

        LocalDateTime now = LocalDateTime.now();
        if (task.getDueDate() != null && task.getDueDate().isBefore(now)) {
            throw new IllegalArgumentException("Task due date cannot be in the past");
        }


        if (assignerUser.getRole().equals(UserRole.MANAGER)) {
            LocalDateTime minimumDueDate = now.plusDays(3);
            if (task.getDueDate() == null || task.getDueDate().isBefore(minimumDueDate)) {
                throw new IllegalArgumentException("For manager-assigned tasks, due date must be at least 3 days from now");
            }
        }

        if (task.getTags() == null || task.getTags().size() < 2) {
            throw new IllegalArgumentException("Task must have at least 2 tags");
        }

        task.setUser(assignedUser);
        task.setCreatedBy(assignerUser);
        task.setStatus(TaskStatus.NEW);

        // Handle tags
        List<Tag> managedTags = new ArrayList<>();
        for (Tag tag : task.getTags()) {
            Tag managedTag = tagService.findOrCreateTag(tag.getName());
            managedTags.add(managedTag);
        }
        task.setTags(managedTags);

        return taskRepository.save(task);
    }

    public List<Task> getTasksByManager(Long managerId) {
        Optional<User> manager = userService.getUserById(managerId);
        if (manager.isPresent()) {
            return taskRepository.findTasksByCreatedBy(manager.get());
        } else {
            throw new SecurityException("Manager or assigned user does not exist");
        }
    }

    public List<Task> getTasksByAssignedUser(Long userId) {
        Optional<User> user = userService.getUserById(userId);
        if (user.isPresent()) {
            return taskRepository.findTasksByAssignedUser(user.get());
        } else {
            throw new SecurityException("assigned user does not exist");
        }

    }

    public Task getTaskById(Long taskId) {
        return taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException("Task not found with ID: " + taskId));
    }

    public Task updateTaskStatus(Long taskId, TaskStatus newStatus, Long userId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new IllegalArgumentException("Task not found"));
        User user = userService.getUserById(userId).orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        if (!task.getUser().equals(user) && !user.getRole().equals(UserRole.MANAGER)) {
            throw new SecurityException("You don't have permission to update this task");
        }

        LocalDateTime now = LocalDateTime.now();
        if (task.getDueDate() != null && task.getDueDate().isBefore(now)) {
            throw new IllegalArgumentException("You can not modify Task Status after Deadline!");
        }

        task.setStatus(newStatus);
        return taskRepository.update(task);
    }

    public Task updateTask(Task task) {
        return taskRepository.update(task);
    }

    public Task refuseTask(Long taskId, Long userId){
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found"));
        User user = userService.getUserById(userId).orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        if (!task.getUser().equals(user)) {
            throw new SecurityException("You don't have permission to refuse this task");
        }

        if(task.getIsRefused()){
            throw new IllegalArgumentException("You can not refuse this task, because it is refused before");
        }

        // mark as is refused true
        task.setIsRefused(Boolean.TRUE);
        Task updatesTask = taskRepository.update(task);

        // SUBTRACT TOKEN
        Token token = tokenService.subtractRefuseToken(user);

        // CREATE REQUEST
        Request newRequest = new Request();
        newRequest.setTask(updatesTask);
        newRequest.setUser(user);
        LocalDateTime now = LocalDateTime.now();
        newRequest.setCreatedAt(now);
        newRequest.setIsAccepted(Boolean.FALSE);
        newRequest.setRequestType(RequestType.REFUSE);
        newRequest.setIsFulfilled(Boolean.FALSE);
        newRequest.setIsQueued(Boolean.FALSE);
        requestService.createRequest(newRequest);
        return task;
    }

    public Task deleteTask(Long taskId, Long userId){
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found"));
        User user = userService.getUserById(userId).orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));


        if (!(user.getRole().equals(UserRole.MANAGER) || task.getUser().equals(user))) {
            throw new SecurityException("Only managers or task owner users san delete this task.");
        }

        if(task.getIsRefused()){
            throw new IllegalArgumentException("You can not Delete this task, because it is refused before");
        }

        if(task.getUser().getToken().getDelete_token() <= 0){
            throw new IllegalArgumentException("You can not delete this task, You have no Tokens left");
        }

        //  mark as is refused true
        task.setIsRefused(Boolean.TRUE);
        task.setUser(null);
        Task updatesTask = taskRepository.update(task);

        // SUBTRACT TOKEN
        Token token = tokenService.subtractDeleteToken(user);

        return task;
    }

}

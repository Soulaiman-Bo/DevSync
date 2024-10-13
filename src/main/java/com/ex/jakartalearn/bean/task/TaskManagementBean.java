package com.ex.jakartalearn.bean.task;

import com.ex.jakartalearn.bean.auth.UserSessionBean;
import com.ex.jakartalearn.entity.Tag;
import com.ex.jakartalearn.entity.Task;
import com.ex.jakartalearn.entity.Token;
import com.ex.jakartalearn.entity.User;
import com.ex.jakartalearn.enumeration.TaskStatus;
import com.ex.jakartalearn.enumeration.UserRole;
import com.ex.jakartalearn.exceptions.TokenNotFoundException;
import com.ex.jakartalearn.service.TaskService;
import com.ex.jakartalearn.service.TokenService;
import com.ex.jakartalearn.service.UserService;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class TaskManagementBean implements Serializable {
    @Inject
    private TaskService taskService;

    @Inject
    private UserService userService;

    @Inject
    private TokenService tokenService;


    @Inject
    private UserSessionBean userSessionBean;

    @Getter
    @Setter
    private Long selectedTaskId;

    @Getter
    @Setter
    private Task newTask = new Task();
    @Setter
    @Getter
    private Long selectedUserId;

    @Setter
    @Getter
    private Token tokens;

    @Getter
    private String tagInput;
    @Getter
    private final List<String> tagList = new ArrayList<>();

    @Getter
    private List<User> availableUsers;
    @Getter
    private List<Task> managerTasks;

    @Getter
    private List<Task> refusedTasks;

    @Getter
    private List<Task> deletedTasks;

    @Getter
    private List<Task> tasksCreatedByUser;

    @Getter
    private List<Task> tasksCreatedByManager;

    public void setTagInput(String tagInput) {
        this.tagInput = tagInput;
        if (tagInput != null && !tagInput.trim().isEmpty()) {
            String[] tags = tagInput.split(",");
            for (String tag : tags) {
                String trimmedTag = tag.trim();
                if (!trimmedTag.isEmpty() && !tagList.contains(trimmedTag)) {
                    tagList.add(trimmedTag);
                }
            }
            this.tagInput = "";
        }
    }

    public void init() {
        availableUsers = userService.getAll();
        loadManagerTasks();
    }

    public void loadManagerTasks() {
        try {
            Long currentUserId = userSessionBean.getCurrentUser().getId();
            if (userSessionBean.getCurrentUser().getRole() == UserRole.MANAGER) {

                List<Task> allTasks  = taskService.getTasksByManager(currentUserId);

                managerTasks = allTasks.stream()
                        .filter(task -> task.getIsRefused().equals(Boolean.FALSE))
                        .collect(Collectors.toList());

                refusedTasks = allTasks.stream()
                        .filter(task -> (task.getIsRefused().equals(Boolean.TRUE) && task.getUser() != null))
                        .collect(Collectors.toList());

                deletedTasks = allTasks.stream()
                        .filter(task -> (task.getIsRefused().equals(Boolean.TRUE) && task.getUser() == null))
                        .collect(Collectors.toList());

            } else {
                List<Task> allTasks = taskService.getTasksByAssignedUser(currentUserId);

                tasksCreatedByUser = allTasks.stream()
                        .filter(task -> task.getCreatedBy().getId().equals(currentUserId))
                        .collect(Collectors.toList());

                tasksCreatedByManager = allTasks.stream()
                        .filter(task -> !task.getCreatedBy().getId().equals(currentUserId))
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "No task Available: " + e.getMessage(), null));
        }
    }

    public void loadTokens() {
        if (userSessionBean.getCurrentUser().getRole() != UserRole.MANAGER) {
            try {
                tokens = tokenService.getTokenByUser(userSessionBean.getCurrentUser()).orElseThrow(() -> new TokenNotFoundException("Token Not Found"));
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
            }
        }
    }

    public String createTask() {
        Task createdTask;
        try {
            Long currentUserId = userSessionBean.getCurrentUser().getId();

            // Clear existing tags and add new ones
            newTask.getTags().clear();
            for (String tagName : tagList) {
                Tag tag = new Tag();
                tag.setName(tagName);
                newTask.getTags().add(tag);
            }

            if (userSessionBean.getCurrentUser().getRole() == UserRole.MANAGER) {
                createdTask = taskService.createTask(newTask, currentUserId, selectedUserId);
            } else {
                createdTask = taskService.createTask(newTask, currentUserId, currentUserId);
            }

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Task created successfully - ID: " + createdTask.getId(), null));
            newTask = new Task();
            tagList.clear();
            selectedUserId = null;
            loadManagerTasks();
            return null;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
            return null;
        }
    }

    public String markTaskAsDone() {
        if (selectedTaskId != null) {
            try {
                taskService.updateTaskStatus(selectedTaskId, TaskStatus.COMPLETED, userSessionBean.getCurrentUser().getId());
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Task marked as done successfully", null));
                loadManagerTasks(); // Refresh the task lists
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error marking task as done: " + e.getMessage(), null));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "No task selected", null));
        }
        return null;
    }

    public void refuseTask() {
        if (selectedTaskId != null) {
            try {
                taskService.refuseTask(selectedTaskId, userSessionBean.getCurrentUser().getId());
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Task marked as Refused successfully", null));
                loadManagerTasks();
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error marking task as Refused: " + e.getMessage(), null));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "No task selected", null));
        }
    }

    public void deleteTask() {
        if (selectedTaskId != null) {
            try {
                taskService.deleteTask(selectedTaskId, userSessionBean.getCurrentUser().getId());

                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Task marked as Deleted successfully", null));

                loadManagerTasks();
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error marking task as Deleted: " + e.getMessage(), null));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "No task selected", null));
        }
    }


}

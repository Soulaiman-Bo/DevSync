package com.ex.jakartalearn.bean.dashboard;

import com.ex.jakartalearn.bean.auth.UserSessionBean;
import com.ex.jakartalearn.entity.Task;
import com.ex.jakartalearn.entity.Token;
import com.ex.jakartalearn.enumeration.TaskStatus;
import com.ex.jakartalearn.enumeration.UserRole;
import com.ex.jakartalearn.exceptions.TokenNotFoundException;
import com.ex.jakartalearn.service.RequestService;
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
import java.util.List;
import java.util.stream.Collectors;

@Named("userDashboardBean")
@ViewScoped
public class UserDashboardBean implements Serializable {

    @Inject
    private TaskService taskService;
    @Inject
    private UserService userService;
    @Inject
    private RequestService requestService;
    @Inject
    private TokenService tokenService;
    @Inject
    private UserSessionBean userSessionBean;

    @Getter
    private List<Task> tasksCreatedByUser;

    @Getter
    private List<Task> tasksCreatedByManager;

    @Getter
    @Setter
    private Long selectedTaskId;

    @Setter
    @Getter
    private Token tokens;

    public void init() {
        loadTasks();
    }

    public void loadTasks() {
        try {
            Long currentUserId = userSessionBean.getCurrentUser().getId();

            List<Task> allTasks = taskService.getTasksByAssignedUser(currentUserId);

            tasksCreatedByUser = allTasks.stream()
                    .filter(task -> task.getCreatedBy().getId().equals(currentUserId))
                    .collect(Collectors.toList());

            tasksCreatedByManager = allTasks.stream()
                    .filter(task -> !task.getCreatedBy().getId().equals(currentUserId))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "No task Available: " + e.getMessage(), null));
        }
    }

    public void markTaskAsDone() {
        if (selectedTaskId != null) {
            try {
                taskService.updateTaskStatus(selectedTaskId, TaskStatus.COMPLETED, userSessionBean.getCurrentUser().getId());
                loadTasks();
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Task marked as done successfully", null));
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error completing task: " + e.getMessage(), null));
            }
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

    public void refuseTask() {
        if (selectedTaskId != null) {
            try {
                // Check if user has enough tokens
                if (tokens.getRefuse_token() <= 0) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "No refuse tokens available", null));
                    return;
                }

                taskService.refuseTask(selectedTaskId, userSessionBean.getCurrentUser().getId());

                // Reload tokens after refusing task
                loadTokens();
                // Reload tasks to refresh the list
                loadTasks();

                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Task refused successfully", null));
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error refusing task: " + e.getMessage(), null));
            }
        }
    }

    public void deleteTask() {
        if (selectedTaskId != null) {
            try {
                // Check if user has enough tokens
                if (tokens.getDelete_token() <= 0) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "No delete tokens available", null));
                    return;
                }

                taskService.deleteTask(selectedTaskId, userSessionBean.getCurrentUser().getId());

                // Reload tokens after deleting task
                loadTokens();
                // Reload tasks to refresh the list
                loadTasks();

                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Task deleted successfully", null));
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error deleting task: " + e.getMessage(), null));
            }
        }
    }
}

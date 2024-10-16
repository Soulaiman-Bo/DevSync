package com.ex.jakartalearn.bean.dashboard;

import com.ex.jakartalearn.bean.auth.UserSessionBean;
import com.ex.jakartalearn.entity.Task;
import com.ex.jakartalearn.entity.User;
import com.ex.jakartalearn.service.RequestService;
import com.ex.jakartalearn.service.TaskService;
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

@Named("managerDashboardBean")
@ViewScoped
public class ManagerDashboardBean implements Serializable {

    @Inject
    private UserSessionBean userSessionBean;

    @Inject
    private TaskService taskService;

    @Inject
    private UserService userService;

    @Inject
    private RequestService requestService;

    @Getter
    private List<Task> refusedTasks;

    @Getter
    private List<Task> managerTasks;

    @Getter
    private List<Task> deletedTasks;

    @Getter
    private List<User> availableUsers;

    @Setter @Getter
    private Long selectedUserId;

    public void loadTasks() {
        try {
            Long currentUserId = userSessionBean.getCurrentUser().getId();

            List<Task> allTasks = taskService.getTasksByManager(currentUserId);

            managerTasks = allTasks.stream()
                    .filter(task -> task.getIsRefused().equals(Boolean.FALSE))
                    .collect(Collectors.toList());

            refusedTasks = allTasks.stream()
                    .filter(task -> (task.getIsRefused().equals(Boolean.TRUE) && task.getUser() != null))
                    .collect(Collectors.toList());

            deletedTasks = allTasks.stream()
                    .filter(task -> (task.getIsRefused().equals(Boolean.TRUE) && task.getUser() == null))
                    .collect(Collectors.toList());


        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "No task Available: " + e.getMessage(), null));
        }
    }

    public void loadUsers() {
        availableUsers = userService.getAll();
    }

    public void init() {
        loadUsers();
        loadTasks();
    }

    public void acceptRequest(Long taskId) {
        if (selectedUserId == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select a user to reassign the task", null));
            return;
        }

        try {
            requestService.acceptRequest(selectedUserId, taskId, userSessionBean.getCurrentUser());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Task reassigned successfully", null));
            loadTasks();
            selectedUserId = null;  // Reset the selected user after successful reassignment
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error reassigning task: " + e.getMessage(), null));
        }
    }


}

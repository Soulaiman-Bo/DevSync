package com.ex.jakartalearn.bean.dashboard;

import com.ex.jakartalearn.bean.auth.UserSessionBean;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.IOException;
import java.io.Serializable;

@Named
@ViewScoped
public class DashboardController implements Serializable {
    @Inject
    private UserSessionBean userSessionBean;

    @Inject
    private ManagerDashboardBean managerDashboardBean;

    @Inject
    private UserDashboardBean userDashboardBean;

    public void initializeDashboard() {
        if (!userSessionBean.isLoggedIn()) {
            redirectToLogin();
            return;
        }

        try {
            if (userSessionBean.isManager()) {
                managerDashboardBean.init();
            } else {
                userDashboardBean.init();
            }
        } catch (Exception e) {
            addErrorMessage("Failed to initialize dashboard");
        }
    }

    private void redirectToLogin() {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            String loginPage = "/login.xhtml?faces-redirect=true";
            context.getExternalContext().redirect(
                    context.getExternalContext().getRequestContextPath() + loginPage
            );
        } catch (IOException e) {
            addErrorMessage("Navigation failed");
        }
    }

    private void addErrorMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
    }
}

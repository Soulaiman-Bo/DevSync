package com.ex.jakartalearn.bean.auth;

import com.ex.jakartalearn.entity.User;
import com.ex.jakartalearn.enumeration.UserRole;
import com.ex.jakartalearn.service.UserService;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Named
@SessionScoped
public class UserSessionBean implements Serializable {
    @Inject
    private UserService userService;

    @Setter
    @Getter
    private User currentUser;

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isManager() {
        return isLoggedIn() && currentUser.getRole() == UserRole.MANAGER;
    }

    public String logout() {
        currentUser = null;
        return "/login.xhtml?faces-redirect=true";
    }

}

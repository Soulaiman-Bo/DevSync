package com.ex.jakartalearn.bean.auth;

import com.ex.jakartalearn.entity.User;
import com.ex.jakartalearn.service.UserService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Named
@RequestScoped
public class LoginBean {

    @Inject
    private UserService userService;

    @Inject
    private UserSessionBean userSessionBean;

    @Setter
    @Getter
    private String username;
    @Setter
    @Getter
    private String password;
    @Getter
    private String errorMessage;

    public String login() {
        try {
            Optional<User> userOptional = userService.authenticateUser(username, password);
            if (userOptional.isPresent()) {
                userSessionBean.setCurrentUser(userOptional.get());
                return "/dashboard.xhtml?faces-redirect=true";
            } else {
                errorMessage = "Invalid username or password";
                return null;
            }
        } catch (Exception e) {
            errorMessage = "An error occurred during login";
            return null;
        }
    }
}
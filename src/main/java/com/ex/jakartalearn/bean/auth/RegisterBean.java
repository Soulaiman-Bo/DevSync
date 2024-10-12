package com.ex.jakartalearn.bean.auth;

import com.ex.jakartalearn.entity.User;
import com.ex.jakartalearn.service.UserService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

@Named
@RequestScoped
public class RegisterBean {

    @Inject
    private UserService userService;

    // Getters and setters
    @Setter
    @Getter
    private User user = new User();
    @Setter
    @Getter
    private String confirmPassword;
    @Getter
    private String errorMessage;

    public String register() {
        try {
            if (!user.getPassword().equals(confirmPassword)) {
                errorMessage = "Passwords do not match";
                return null;
            }

            userService.registerUser(user);
            return "/auth/login.xhtml?faces-redirect=true";
        } catch (Exception e) {
            errorMessage = e.getMessage();
            return null;
        }
    }


}
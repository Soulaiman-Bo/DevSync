package com.ex.jakartalearn.service;

import com.ex.jakartalearn.entity.User;
import com.ex.jakartalearn.enumeration.UserRole;
import com.ex.jakartalearn.repository.UserRepository;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.List;

@Stateless
@RolesAllowed("MANAGER")
public class ManagerService {
    @Inject
    protected UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.getAll();
    }

    public void updateUserRole(Long userId, UserRole newRole) {
        User user = userRepository.getById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(newRole);
        userRepository.update(user);
    }

    public void deleteUser(Long userId) {
        userRepository.delete(userId);
    }

}

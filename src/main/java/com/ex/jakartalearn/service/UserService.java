package com.ex.jakartalearn.service;

import com.ex.jakartalearn.entity.Token;
import com.ex.jakartalearn.entity.User;
import com.ex.jakartalearn.enumeration.UserRole;
import com.ex.jakartalearn.repository.UserRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Stateless
public class UserService implements Serializable {
    @Inject
    private UserRepository userRepository;

    @Inject
    private Pbkdf2PasswordHash passwordHash;

    public User registerUser(User user) throws Exception {
        // Check if username already exists
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new Exception("Username already exists");
        }

        // Check if email already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new Exception("Email already exists");
        }

        // Hash the password before saving
        user.setPassword(passwordHash.generate(user.getPassword().toCharArray()));

        // Set default role if not specified
        if (user.getRole() == null) {
            user.setRole(UserRole.USER);
        }

        Token token = new Token(user);
        user.setToken(token);

        return userRepository.save(user);
    }

    public Optional<User> authenticateUser(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordHash.verify(password.toCharArray(), user.getPassword())) {
                return Optional.of(user);
            }
        }

        return Optional.empty();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.getById(id);
    }

    public List<User> getAll() {
        return userRepository.getAll();
    }


}

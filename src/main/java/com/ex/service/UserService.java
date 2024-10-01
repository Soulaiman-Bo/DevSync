package com.ex.service;

import com.ex.entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class UserService {

    @PersistenceContext(unitName = "myPU")
    private EntityManager entityManager;

    public void createUser(User user) {
        entityManager.persist(user);
    }
}

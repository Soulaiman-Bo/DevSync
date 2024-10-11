package com.ex.jakartalearn.repository;

import com.ex.jakartalearn.entity.Task;
import com.ex.jakartalearn.entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
public class TaskRepository extends BaseRepository<Task> {

    public TaskRepository() {
        super(Task.class);
    }

    public List<Task> findTasksByAssignedUser(User user) {
        return entityManager.createQuery(
                        "SELECT t FROM Task t WHERE t.user = :user ORDER BY t.dueDate ASC",
                        Task.class)
                .setParameter("user", user)
                .getResultList();
    }

    public List<Task> findTasksByCreatedBy(User manager) {
        return entityManager.createQuery(
                        "SELECT t FROM Task t WHERE t.createdBy = :manager",
                        Task.class)
                .setParameter("manager", manager)
                .getResultList();
    }

}

package com.ex.jakartalearn.repository;

import com.ex.jakartalearn.entity.Request;
import com.ex.jakartalearn.entity.Task;
import com.ex.jakartalearn.entity.Token;
import com.ex.jakartalearn.entity.User;
import jakarta.ejb.Stateless;

import java.util.List;

@Stateless
public class RequestRepository extends BaseRepository<Request> {

    public RequestRepository() {
        super(Request.class);
    }

    public Task findRequestByTask(Task task) {
        return entityManager.createQuery(
                        "SELECT t FROM Request t WHERE t.task = :task",
                        Task.class)
                .setParameter("task", task)
                .getSingleResult();
    }

}

package com.ex.jakartalearn.repository;

import com.ex.jakartalearn.entity.RequestQueue;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
public class RequestQueueRepository extends BaseRepository<RequestQueue> {

    @PersistenceContext
    protected EntityManager entityManager;

    public RequestQueueRepository() {
        super(RequestQueue.class);
    }


    public void deleteAll(List<RequestQueue> requestQueues) {
        for (RequestQueue rq : requestQueues) {
            entityManager.remove(entityManager.contains(rq) ? rq : entityManager.merge(rq));
        }
    }

}

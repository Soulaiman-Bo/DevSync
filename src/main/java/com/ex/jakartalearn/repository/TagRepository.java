package com.ex.jakartalearn.repository;

import com.ex.jakartalearn.entity.Tag;
import com.ex.jakartalearn.entity.Task;
import com.ex.jakartalearn.entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

@Stateless
public class TagRepository extends BaseRepository<Tag> {

    @PersistenceContext
    protected EntityManager entityManager;

    public TagRepository() {
        super(Tag.class);
    }

    public List<Tag> getTagsByTaskId(Long taskId) {
        return entityManager.createQuery(
                        "SELECT t.tags FROM Task t WHERE t.id = :taskId", Tag.class)
                .setParameter("taskId", taskId)
                .getResultList();
    }

    public Optional<Tag> findByName(String name) {
        return entityManager.createQuery("SELECT t FROM Tag t WHERE t.name = :name", Tag.class)
                .setParameter("name", name)
                .getResultStream()
                .findFirst();
    }

}

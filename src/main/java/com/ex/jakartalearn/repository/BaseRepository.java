package com.ex.jakartalearn.repository;

import com.ex.jakartalearn.entity.BaseEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

public abstract class BaseRepository <T extends BaseEntity> {
    @PersistenceContext
    protected EntityManager entityManager;

    private final Class<T> entityClass;

    protected BaseRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public T save(T entity) {
        entityManager.persist(entity);
        return entity;
    }

    public T merge(T entity) {
        entityManager.merge(entity);
        return entity;
    }

    public T update(T entity) {
        return entityManager.merge(entity);
    }

    public Optional<T> findById(Long id) {
        return Optional.ofNullable(entityManager.find(entityClass, id));
    }

    public List<T> findAll() {
        return entityManager.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass)
                .getResultList();
    }

    public void delete(T entity) {
        entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
    }

    public void deleteById(Long id) {
        findById(id).ifPresent(this::delete);
    }

}

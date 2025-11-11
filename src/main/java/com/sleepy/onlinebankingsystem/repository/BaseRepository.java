// src/main/java/com/sleepy/onlinebankingsystem/repository/BaseRepository.java
package com.sleepy.onlinebankingsystem.repository;

import com.sleepy.onlinebankingsystem.model.entity.Base;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

public abstract class BaseRepository<T extends Base> {
    @PersistenceContext(unitName = "sleepy")
    protected EntityManager em;

    // سازنده پیش‌فرض
    public BaseRepository() {}

    protected abstract EntityManager getEntityManager();

    protected abstract Class<T> getEntityClass();

    @Transactional
    public T save(T entity) {
        if (entity.getId() == null) {
            getEntityManager().persist(entity);
        } else {
            entity = getEntityManager().merge(entity);
        }
        return entity;
    }

    public Optional<T> findById(Long id) {
        T entity = getEntityManager().find(getEntityClass(), id);
        return (entity != null && !entity.isDeleted()) ? Optional.of(entity) : Optional.empty();
    }

    public List<T> findAll(int page, int size) {
        TypedQuery<T> query = getEntityManager()
                .createQuery("SELECT e FROM " + getEntityClass().getSimpleName() + " e", getEntityClass());
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        return query.getResultList(); // @Where خودکار فیلتر می‌کنه
    }

    @Transactional
    public void softDelete(Long id) {
        findById(id).ifPresent(entity -> {
            entity.setDeleted(true);
            getEntityManager().merge(entity);
        });
    }

    @Transactional
    public void hardDelete(Long id) {
        getEntityManager().createQuery("DELETE FROM " + getEntityClass().getSimpleName() + " e WHERE e.id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }

    public List<T> findAllIncludingDeleted() {
        return getEntityManager()
                .createQuery("SELECT e FROM " + getEntityClass().getSimpleName() + " e", getEntityClass())
                .getResultList();
    }

    public Optional<T> findByIdIncludingDeleted(Long id) {
        T entity = getEntityManager().find(getEntityClass(), id);
        return Optional.ofNullable(entity);
    }
}
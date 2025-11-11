// src/main/java/com/sleepy/onlinebankingsystem/repository/UserRepository.java
package com.sleepy.onlinebankingsystem.repository;

import com.sleepy.onlinebankingsystem.model.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;


@ApplicationScoped
public class UserRepository extends BaseRepository<User> {

    @PersistenceContext(unitName ="sleepy")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<User> getEntityClass() {
        return User.class;
    }

    public Optional<User> findByUsername(String username) {
        TypedQuery<User> query = em.createNamedQuery(User.FIND_BY_USERNAME, User.class);
        query.setParameter("username", username);
        return query.getResultList().stream().findFirst();
    }

    public Optional<User> findByNationalCode(String nationalCode) {
        TypedQuery<User> query = em.createNamedQuery(User.FIND_BY_NATIONAL_CODE, User.class);
        query.setParameter("nationalCode", nationalCode);
        return query.getResultList().stream().findFirst();
    }

    public List<User> findActiveUsers() {
        return em.createNamedQuery(User.FIND_ACTIVE_USERS, User.class).getResultList();
    }

    public List<User> findAllUsers() {
        return em.createNamedQuery(User.FIND_ALL, User.class).getResultList();
    }
}
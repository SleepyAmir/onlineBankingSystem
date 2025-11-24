// 📁 src/main/java/com/sleepy/onlinebankingsystem/repository/RoleRepository.java
package com.sleepy.onlinebankingsystem.repository;

import com.sleepy.onlinebankingsystem.model.entity.Role;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.model.enums.UserRole;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RoleRepository extends BaseRepository<Role> {

    @PersistenceContext(unitName = "sleepy")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<Role> getEntityClass() {
        return Role.class;
    }


    public List<Role> findByUser(User user) {
        return em.createNamedQuery(Role.FIND_BY_USER, Role.class)
                .setParameter("user", user)
                .getResultList();
    }

    public Optional<Role> findByUsernameAndRoleName(String username, UserRole roleName) {
        return em.createNamedQuery(Role.FIND_BY_USERNAME_AND_ROLE_NAME, Role.class)
                .setParameter("username", username)
                .setParameter("roleName", roleName)
                .getResultList()
                .stream()
                .findFirst();
    }

    public List<Role> findByRoleName(UserRole roleName) {
        return em.createNamedQuery(Role.FIND_BY_ROLE_NAME, Role.class)
                .setParameter("roleName", roleName)
                .getResultList();
    }

    public long countAll() {
        return em.createNamedQuery(Role.COUNT_ALL, Long.class)
                .getSingleResult();
    }
}
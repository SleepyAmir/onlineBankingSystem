package com.sleepy.onlinebankingsystem.repository;

import com.sleepy.onlinebankingsystem.model.entity.Authority;
import com.sleepy.onlinebankingsystem.model.entity.Role;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@ApplicationScoped
public class AuthorityRepository extends BaseRepository<Authority> {

    @PersistenceContext(unitName ="sleepy")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() { return em; }

    @Override
    protected Class<Authority> getEntityClass() { return Authority.class; }

    public List<Authority> findByRole(Role role) {
        return em.createNamedQuery(Authority.FIND_BY_Role, Authority.class)
                .setParameter("role", role).getResultList();
    }

    public List<Authority> findByResource(String resource) {
        return em.createNamedQuery(Authority.FIND_BY_RESOURCE, Authority.class)
                .setParameter("resource", resource).getResultList();
    }

    public List<Authority> findByResourceAndAction(String resource, String action) {
        return em.createNamedQuery(Authority.FIND_BY_RESOURCE_AND_ACTION, Authority.class)
                .setParameter("resource", resource)
                .setParameter("action", action)
                .getResultList();
    }

    public List<Authority> findAllAuthorities() {
        return em.createNamedQuery(Authority.FIND_ALL, Authority.class).getResultList();
    }
}
package com.sleepy.onlinebankingsystem.repository;

import com.sleepy.onlinebankingsystem.model.entity.Token;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Optional;


@ApplicationScoped
public class TokenRepository extends BaseRepository<Token> {

    @PersistenceContext(unitName ="sleepy")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() { return em; }

    @Override
    protected Class<Token> getEntityClass() { return Token.class; }

    public Optional<Token> findByTokenValue(String tokenValue) {
        return em.createQuery("SELECT t FROM Token t WHERE t.tokenValue = :tokenValue", Token.class)
                .setParameter("tokenValue", tokenValue)
                .getResultList().stream().findFirst();
    }

    public Optional<Token> findByUsername(String username) {
        return em.createQuery("SELECT t FROM Token t WHERE t.username = :username", Token.class)
                .setParameter("username", username)
                .getResultList().stream().findFirst();
    }
}
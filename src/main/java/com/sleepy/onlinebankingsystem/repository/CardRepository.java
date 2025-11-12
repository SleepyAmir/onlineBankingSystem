package com.sleepy.onlinebankingsystem.repository;

import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.entity.Card;
import com.sleepy.onlinebankingsystem.model.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;


@ApplicationScoped
public class CardRepository extends BaseRepository<Card> {

    @PersistenceContext(unitName = "sleepy")
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() { return em; }

    @Override
    protected Class<Card> getEntityClass() { return Card.class; }

    public List<Card> findByAccount(Account account) {
        return em.createNamedQuery(Card.FIND_BY_ACCOUNT, Card.class)
                .setParameter("account", account).getResultList();
    }

    public Optional<Card> findByCardNumber(String cardNumber) {
        return em.createNamedQuery(Card.FIND_BY_CARD_NUMBER, Card.class)
                .setParameter("cardNumber", cardNumber)
                .getResultList().stream().findFirst();
    }

    public List<Card> findByUser(User user) {
        return em.createNamedQuery(Card.FIND_BY_USER, Card.class)
                .setParameter("user", user).getResultList();
    }

    public List<Card> findActiveCards() {
        return em.createNamedQuery(Card.FIND_ACTIVE_CARDS, Card.class).getResultList();
    }

    public List<Card> findAllCards() {
        return em.createNamedQuery(Card.FIND_ALL, Card.class).getResultList();
    }
}
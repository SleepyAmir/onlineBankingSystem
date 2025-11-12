package com.sleepy.onlinebankingsystem.service.impl;

import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.entity.Card;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.repository.CardRepository;
import com.sleepy.onlinebankingsystem.service.CardService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class CardServiceImpl implements CardService {

    @Inject
    CardRepository cardRepository;

    @Transactional
    @Override
    public Card save(Card card) throws Exception {
        log.info("Saving card: {}", card.getCardNumber());

        if (cardRepository.findByCardNumber(card.getCardNumber()).isPresent()) {
            throw new IllegalArgumentException("Card number already exists: " + card.getCardNumber());
        }

        return cardRepository.save(card);
    }

    @Transactional
    @Override
    public Card update(Card card) throws Exception {
        if (card.getId() == null) throw new IllegalArgumentException("ID is required");
        return cardRepository.save(card);
    }

    @Transactional
    @Override
    public void softDelete(Long id) throws Exception {
        cardRepository.softDelete(id);
    }

    @Transactional
    @Override
    public void softDeleteByCardNumber(String cardNumber) throws Exception {
        cardRepository.findByCardNumber(cardNumber)
                .ifPresent(card -> cardRepository.softDelete(card.getId()));
    }

    @Override
    public Optional<Card> findById(Long id) throws Exception {
        return cardRepository.findById(id);
    }

    @Override
    public Optional<Card> findByCardNumber(String cardNumber) throws Exception {
        return cardRepository.findByCardNumber(cardNumber);
    }

    @Override
    public List<Card> findByAccount(Account account) throws Exception {
        return cardRepository.findByAccount(account);
    }

    @Override
    public List<Card> findByUser(User user) throws Exception {
        return cardRepository.findByUser(user);
    }

    @Override
    public List<Card> findActiveCards() throws Exception {
        return cardRepository.findActiveCards();
    }

    @Override
    public List<Card> findAll(int page, int size) throws Exception {
        return cardRepository.findAll(page, size);
    }


    @Override
    public List<Card> findByUserWithAccountAndUser(Long userId) throws Exception {
        log.debug("Fetching cards with account and user for user ID: {}", userId);
        return cardRepository.getEntityManager()
                .createNamedQuery(Card.FIND_BY_USER_WITH_ACCOUNT_AND_USER, Card.class)
                .setParameter("userId", userId)
                .getResultList();
    }
    // درست شده
    @Override
    public List<Card> findByUserWithAccount(Long userId) throws Exception {
        log.debug("Fetching cards with account for user ID: {}", userId);
        try {
            return cardRepository.getEntityManager()
                    .createNamedQuery(Card.FIND_BY_USER_WITH_ACCOUNT, Card.class)
                    .setParameter("userId", userId)
                    .getResultList();
        } catch (Exception e) {
            log.error("Error fetching cards with account for user ID: {}", userId, e);
            throw e;
        }
    }
}
package com.sleepy.onlinebankingsystem.service;

import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.entity.Card;
import com.sleepy.onlinebankingsystem.model.entity.User;

import java.util.List;
import java.util.Optional;

public interface CardService {
    Card save(Card card) throws Exception;
    Card update(Card card) throws Exception;
    void softDelete(Long id) throws Exception;
    void softDeleteByCardNumber(String cardNumber) throws Exception;

    Optional<Card> findById(Long id) throws Exception;
    Optional<Card> findByCardNumber(String cardNumber) throws Exception;
    List<Card> findByAccount(Account account) throws Exception;
    List<Card> findByUser(User user) throws Exception;
    List<Card> findActiveCards() throws Exception;
    List<Card> findAll(int page, int size) throws Exception;
    List<Card> findByUserWithAccount(Long userId) throws Exception;
    List<Card> findByUserWithAccountAndUser(Long userId) throws Exception;
}
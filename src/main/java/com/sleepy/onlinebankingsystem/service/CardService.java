package com.sleepy.onlinebankingsystem.service;

import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.entity.Card;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.model.enums.CardType;

import java.util.List;
import java.util.Optional;

public interface CardService {
    // ========== متدهای CRUD موجود ==========
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

    // ========== متدهای بیزنس جدید ==========

    /**
     * صدور کارت جدید
     * @param accountId شناسه حساب
     * @param cardType نوع کارت (DEBIT/CREDIT)
     * @return کارت صادر شده
     */
    Card issueCard(Long accountId, CardType cardType) throws Exception;

    /**
     * فعال‌سازی کارت
     * @param cardId شناسه کارت
     * @return کارت فعال شده
     */
    Card activateCard(Long cardId) throws Exception;

    /**
     * مسدودسازی کارت
     * @param cardId شناسه کارت
     * @return کارت مسدود شده
     */
    Card blockCard(Long cardId) throws Exception;

    /**
     * اعتبارسنجی کارت برای تراکنش
     * @param cardNumber شماره کارت
     * @throws Exception اگر کارت نامعتبر یا غیرفعال باشد
     */
    void validateCardForTransaction(String cardNumber) throws Exception;

    /**
     * بررسی امکان صدور کارت برای حساب
     * @param accountId شناسه حساب
     * @throws Exception اگر صدور کارت امکان‌پذیر نباشد
     */
    void validateCardIssuance(Long accountId) throws Exception;
}
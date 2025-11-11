package com.sleepy.onlinebankingsystem.service;

import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.entity.Transaction;
import com.sleepy.onlinebankingsystem.model.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionService {
    Transaction save(Transaction transaction) throws Exception;
    Transaction update(Transaction transaction) throws Exception;
    void softDelete(Long id) throws Exception;
    void softDeleteByTransactionId(String transactionId) throws Exception;

    Optional<Transaction> findById(Long id) throws Exception;
    Optional<Transaction> findByTransactionId(String transactionId) throws Exception;
    List<Transaction> findByAccount(Account account) throws Exception;
    List<Transaction> findByUser(User user) throws Exception;
    List<Transaction> findByDateRange(LocalDateTime start, LocalDateTime end) throws Exception;
    List<Transaction> findAll(int page, int size) throws Exception;
}
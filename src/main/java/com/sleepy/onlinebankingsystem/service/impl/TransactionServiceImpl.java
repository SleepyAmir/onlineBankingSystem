package com.sleepy.onlinebankingsystem.service.impl;

import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.entity.Transaction;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.repository.TransactionRepository;
import com.sleepy.onlinebankingsystem.service.TransactionService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class TransactionServiceImpl implements TransactionService {

    @Inject
    TransactionRepository transactionRepository;

    @Transactional
    @Override
    public Transaction save(Transaction transaction) throws Exception {
        log.info("Saving transaction: {}", transaction.getTransactionId());
        return transactionRepository.save(transaction);
    }

    @Transactional
    @Override
    public Transaction update(Transaction transaction) throws Exception {
        if (transaction.getId() == null) throw new IllegalArgumentException("ID is required");
        return transactionRepository.save(transaction);
    }

    @Transactional
    @Override
    public void softDelete(Long id) throws Exception {
        transactionRepository.softDelete(id);
    }

    @Transactional
    @Override
    public void softDeleteByTransactionId(String transactionId) throws Exception {
        transactionRepository.findByTransactionId(transactionId)
                .ifPresent(t -> transactionRepository.softDelete(t.getId()));
    }

    @Override
    public Optional<Transaction> findById(Long id) throws Exception {
        return transactionRepository.findById(id);
    }

    @Override
    public Optional<Transaction> findByTransactionId(String transactionId) throws Exception {
        return transactionRepository.findByTransactionId(transactionId);
    }

    @Override
    public List<Transaction> findByAccount(Account account) throws Exception {
        return transactionRepository.findByAccount(account);
    }

    @Override
    public List<Transaction> findByUser(User user) throws Exception {
        return transactionRepository.findByUser(user);
    }

    @Override
    public List<Transaction> findByDateRange(LocalDateTime start, LocalDateTime end) throws Exception {
        return transactionRepository.findByDateRange(start, end);
    }

    @Override
    public List<Transaction> findAll(int page, int size) throws Exception {
        return transactionRepository.findAll(page, size);
    }
}
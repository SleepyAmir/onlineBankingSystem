package com.sleepy.onlinebankingsystem.service.impl;

import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.model.enums.AccountStatus;
import com.sleepy.onlinebankingsystem.repository.AccountRepository;
import com.sleepy.onlinebankingsystem.service.AccountService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class AccountServiceImpl implements AccountService {

    @Inject
    AccountRepository accountRepository;

    @Transactional
    @Override
    public Account save(Account account) throws Exception {
        log.info("Saving account: {}", account.getAccountNumber());

        if (accountRepository.findByAccountNumber(account.getAccountNumber()).isPresent()) {
            throw new IllegalArgumentException("Account number already exists: " + account.getAccountNumber());
        }

        return accountRepository.save(account);
    }

    @Transactional
    @Override
    public Account update(Account account) throws Exception {
        if (account.getId() == null) throw new IllegalArgumentException("ID is required for update");
        return accountRepository.save(account);
    }

    @Transactional
    @Override
    public void softDelete(Long id) throws Exception {
        accountRepository.softDelete(id);
    }

    @Transactional
    @Override
    public void softDeleteByAccountNumber(String accountNumber) throws Exception {
        accountRepository.findByAccountNumber(accountNumber)
                .ifPresent(acc -> accountRepository.softDelete(acc.getId()));
    }

    @Override
    public Optional<Account> findById(Long id) throws Exception {
        return accountRepository.findById(id);
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) throws Exception {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    @Override
    public List<Account> findByUser(User user) throws Exception {
        return accountRepository.findByUser(user);
    }

    @Override
    public List<Account> findByStatus(AccountStatus status) throws Exception {
        return accountRepository.findByStatus(status);
    }

    @Override
    public List<Account> findAll(int page, int size) throws Exception {
        return accountRepository.findAll(page, size);
    }
}
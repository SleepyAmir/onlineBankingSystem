package com.sleepy.onlinebankingsystem.service.impl;

import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.model.enums.AccountStatus;
import com.sleepy.onlinebankingsystem.repository.AccountRepository;
import com.sleepy.onlinebankingsystem.service.AccountService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.NoResultException;
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
    @Override
    public List<Account> findByUserWithUser(User user) throws Exception {
        log.info("Fetching accounts with user for user ID: {}", user.getId());
        return accountRepository.findByUserWithUser(user);
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
    public Optional<Account> findByIdWithUser(Long id) throws Exception {
        log.debug("Fetching account with user by ID: {}", id);
        try {
            Account account = accountRepository.getEntityManager()
                    .createNamedQuery(Account.FIND_BY_ID_WITH_USER, Account.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return Optional.of(account);
        } catch (NoResultException e) {
            log.debug("No account found with id: {}", id);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error in findByIdWithUser for id: {}", id, e);
            throw e;
        }
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
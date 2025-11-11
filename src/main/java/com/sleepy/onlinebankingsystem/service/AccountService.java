package com.sleepy.onlinebankingsystem.service;

import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.model.enums.AccountStatus;

import java.util.List;
import java.util.Optional;

public interface AccountService {
    Account save(Account account) throws Exception;
    Account update(Account account) throws Exception;
    void softDelete(Long id) throws Exception;
    void softDeleteByAccountNumber(String accountNumber) throws Exception;

    Optional<Account> findById(Long id) throws Exception;
    Optional<Account> findByAccountNumber(String accountNumber) throws Exception;
    List<Account> findByUser(User user) throws Exception;
    List<Account> findByStatus(AccountStatus status) throws Exception;
    List<Account> findAll(int page, int size) throws Exception;
}
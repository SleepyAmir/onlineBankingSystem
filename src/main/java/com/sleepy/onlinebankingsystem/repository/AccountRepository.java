// src/main/java/com/sleepy/onlinebankingsystem/repository/AccountRepository.java
package com.sleepy.onlinebankingsystem.repository;

import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.model.enums.AccountStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class AccountRepository extends BaseRepository<Account> {

    @PersistenceContext(unitName ="sleepy")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<Account> getEntityClass() {
        return Account.class;
    }

    public List<Account> findByUser(User user) {
        return em.createNamedQuery(Account.FIND_BY_USER, Account.class)
                .setParameter("user", user)
                .getResultList();
    }

    public Optional<Account> findByAccountNumber(String accountNumber) {
        return em.createNamedQuery(Account.FIND_BY_ACCOUNT_NUMBER, Account.class)
                .setParameter("accountNumber", accountNumber)
                .getResultList().stream().findFirst();
    }

    public List<Account> findByStatus(AccountStatus status) {
        return em.createNamedQuery(Account.FIND_BY_STATUS, Account.class)
                .setParameter("status", status)
                .getResultList();
    }

    public List<Account> findAllAccounts() {
        return em.createNamedQuery(Account.FIND_ALL, Account.class).getResultList();
    }
}
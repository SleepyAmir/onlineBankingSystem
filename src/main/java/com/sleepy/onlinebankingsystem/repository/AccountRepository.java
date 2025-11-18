// در AccountRepository.java
package com.sleepy.onlinebankingsystem.repository;

import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.model.enums.AccountStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class AccountRepository extends BaseRepository<Account> {

    @PersistenceContext(unitName ="sleepy")
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
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

    // ✅ متد جدید با JOIN FETCH
    public Optional<Account> findByAccountNumberWithUser(String accountNumber) {
        try {
            Account account = em.createNamedQuery(Account.FIND_BY_ACCOUNT_NUMBER_WITH_USER, Account.class)
                    .setParameter("accountNumber", accountNumber)
                    .getSingleResult();
            return Optional.of(account);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    // ✅ متد جدید با JOIN FETCH برای ID
    public Optional<Account> findByIdWithUser(Long id) {
        try {
            Account account = em.createNamedQuery(Account.FIND_BY_ID_WITH_USER, Account.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return Optional.of(account);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<Account> findByStatus(AccountStatus status) {
        return em.createNamedQuery(Account.FIND_BY_STATUS, Account.class)
                .setParameter("status", status)
                .getResultList();
    }

    public List<Account> findAllAccounts() {
        return em.createNamedQuery(Account.FIND_ALL, Account.class).getResultList();
    }

    public List<Account> findByUserWithUser(User user) {
        return em.createNamedQuery(Account.FIND_BY_USER_WITH_USER, Account.class)
                .setParameter("user", user)
                .getResultList();
    }
}
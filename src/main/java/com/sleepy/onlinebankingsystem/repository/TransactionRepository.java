package com.sleepy.onlinebankingsystem.repository;

import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.entity.Transaction;
import com.sleepy.onlinebankingsystem.model.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@ApplicationScoped
public class TransactionRepository extends BaseRepository<Transaction> {

    @PersistenceContext(unitName ="sleepy")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() { return em; }

    @Override
    protected Class<Transaction> getEntityClass() { return Transaction.class; }

    public List<Transaction> findByAccount(Account account) {
        return em.createNamedQuery(Transaction.FIND_BY_ACCOUNT, Transaction.class)
                .setParameter("account", account).getResultList();
    }

    public List<Transaction> findByUser(User user) {
        return em.createNamedQuery(Transaction.FIND_BY_USER, Transaction.class)
                .setParameter("user", user).getResultList();
    }

    public Optional<Transaction> findByTransactionId(String transactionId) {
        return em.createNamedQuery(Transaction.FIND_BY_TRANSACTION_ID, Transaction.class)
                .setParameter("transactionId", transactionId)
                .getResultList().stream().findFirst();
    }

    public List<Transaction> findByDateRange(LocalDateTime start, LocalDateTime end) {
        return em.createNamedQuery(Transaction.FIND_BY_DATE_RANGE, Transaction.class)
                .setParameter("startDate", start)
                .setParameter("endDate", end)
                .getResultList();
    }

    public List<Transaction> findAllTransactions() {
        return em.createNamedQuery(Transaction.FIND_ALL, Transaction.class).getResultList();
    }
}
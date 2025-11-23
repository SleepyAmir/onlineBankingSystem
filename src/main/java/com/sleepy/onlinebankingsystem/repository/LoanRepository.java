package com.sleepy.onlinebankingsystem.repository;

import com.sleepy.onlinebankingsystem.model.entity.Loan;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.model.enums.LoanStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;


@ApplicationScoped
public class LoanRepository extends BaseRepository<Loan> {

    @PersistenceContext(unitName ="sleepy")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() { return em; }

    @Override
    protected Class<Loan> getEntityClass() {
        return Loan.class;
    }

    public List<Loan> findByUser(User user) {
        return em.createNamedQuery(Loan.FIND_BY_USER, Loan.class)
                .setParameter("user", user).getResultList();
    }

    public Optional<Loan> findByLoanNumber(String loanNumber) {
        return em.createNamedQuery(Loan.FIND_BY_LOAN_NUMBER, Loan.class)
                .setParameter("loanNumber", loanNumber)
                .getResultList().stream().findFirst();
    }

    public List<Loan> findByStatus(LoanStatus status) {
        return em.createNamedQuery(Loan.FIND_BY_STATUS, Loan.class)
                .setParameter("status", status).getResultList();
    }

    public Optional<Loan> findByIdWithUserAndAccount(Long id) {
        try {
            Loan loan = em.createNamedQuery(Loan.FIND_BY_ID_WITH_USER_AND_ACCOUNT, Loan.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return Optional.of(loan);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
    public List<Loan> findByStatusWithUserAndAccount(LoanStatus status) {
        return em.createNamedQuery(Loan.FIND_BY_STATUS_WITH_USER_AND_ACCOUNT, Loan.class)
                .setParameter("status", status)
                .getResultList();
    }

    public Optional<Loan> findByIdForPayment(Long id) {
        try {
            Loan loan = em.createNamedQuery(Loan.FIND_BY_ID_FOR_PAYMENT, Loan.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return Optional.of(loan);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
    public Optional<Loan> findByLoanNumberWithUserAndAccount(String loanNumber) {
        try {
            Loan loan = em.createNamedQuery(Loan.FIND_BY_LOAN_NUMBER_WITH_USER_AND_ACCOUNT, Loan.class)
                    .setParameter("loanNumber", loanNumber)
                    .getSingleResult();
            return Optional.of(loan);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<Loan> findActiveLoans() {
        return em.createNamedQuery(Loan.FIND_ACTIVE_LOANS, Loan.class).getResultList();
    }

    public List<Loan> findAllLoans() {
        return em.createNamedQuery(Loan.FIND_ALL, Loan.class).getResultList();
    }
}
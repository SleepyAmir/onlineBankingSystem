package com.sleepy.onlinebankingsystem.service.impl;

import com.sleepy.onlinebankingsystem.model.entity.Loan;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.model.enums.LoanStatus;
import com.sleepy.onlinebankingsystem.repository.LoanRepository;
import com.sleepy.onlinebankingsystem.service.LoanService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class LoanServiceImpl implements LoanService {

    @Inject
    LoanRepository loanRepository;

    @Transactional
    @Override
    public Loan save(Loan loan) throws Exception {
        log.info("Saving loan: {}", loan.getLoanNumber());

        if (loanRepository.findByLoanNumber(loan.getLoanNumber()).isPresent()) {
            throw new IllegalArgumentException("Loan number already exists: " + loan.getLoanNumber());
        }

        return loanRepository.save(loan);
    }

    @Transactional
    @Override
    public Loan update(Loan loan) throws Exception {
        if (loan.getId() == null) throw new IllegalArgumentException("ID is required");
        return loanRepository.save(loan);
    }

    @Transactional
    @Override
    public void softDelete(Long id) throws Exception {
        loanRepository.softDelete(id);
    }

    @Transactional
    @Override
    public void softDeleteByLoanNumber(String loanNumber) throws Exception {
        loanRepository.findByLoanNumber(loanNumber)
                .ifPresent(loan -> loanRepository.softDelete(loan.getId()));
    }

    @Override
    public Optional<Loan> findById(Long id) throws Exception {
        return loanRepository.findById(id);
    }

    @Override
    public Optional<Loan> findByLoanNumber(String loanNumber) throws Exception {
        return loanRepository.findByLoanNumber(loanNumber);
    }

    @Override
    public List<Loan> findByUser(User user) throws Exception {
        return loanRepository.findByUser(user);
    }

    @Override
    public List<Loan> findByStatus(LoanStatus status) throws Exception {
        return loanRepository.findByStatus(status);
    }

    @Override
    public List<Loan> findActiveLoans() throws Exception {
        return loanRepository.findActiveLoans();
    }

    @Override
    public List<Loan> findAll(int page, int size) throws Exception {
        return loanRepository.findAll(page, size);
    }
}
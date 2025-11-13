package com.sleepy.onlinebankingsystem.service;

import com.sleepy.onlinebankingsystem.model.entity.Loan;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.model.enums.LoanStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface LoanService {
    Loan save(Loan loan) throws Exception;
    Loan update(Loan loan) throws Exception;
    void softDelete(Long id) throws Exception;
    void softDeleteByLoanNumber(String loanNumber) throws Exception;

    Loan payInstallment(Loan loan, BigDecimal amount) throws Exception;
    Optional<Loan> findById(Long id) throws Exception;
    Optional<Loan> findByLoanNumber(String loanNumber) throws Exception;
    List<Loan> findByUser(User user) throws Exception;
    List<Loan> findByStatus(LoanStatus status) throws Exception;
    List<Loan> findActiveLoans() throws Exception;
    List<Loan> findAll(int page, int size) throws Exception;
}
package com.sleepy.onlinebankingsystem.service.impl;

import com.sleepy.onlinebankingsystem.model.entity.Loan;
import com.sleepy.onlinebankingsystem.model.enums.LoanStatus;
import com.sleepy.onlinebankingsystem.service.LoanCalculationService;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@ApplicationScoped
public class LoanCalculationServiceImpl implements LoanCalculationService {

    @Override
    public BigDecimal calculateRemainingBalance(Loan loan) {
        if (loan == null || loan.getPrincipal() == null) {
            return BigDecimal.ZERO;
        }
        return loan.getPrincipal();
    }

    @Override
    public BigDecimal calculatePaidAmount(Loan loan) {
        if (loan == null) {
            return BigDecimal.ZERO;
        }

        if (loan.getStatus() == LoanStatus.PAID) {
            return loan.getTotalRepayment();
        }

        if (loan.getStatus() == LoanStatus.REJECTED ||
                loan.getStatus() == LoanStatus.PENDING) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalLoan = loan.getTotalRepayment();
        BigDecimal remaining = calculateRemainingBalance(loan);

        return totalLoan.subtract(remaining);
    }

    @Override
    public int calculatePaymentProgress(Loan loan) {
        if (loan == null) {
            return 0;
        }

        if (loan.getStatus() == LoanStatus.PAID) {
            return 100;
        }

        if (loan.getStatus() == LoanStatus.REJECTED ||
                loan.getStatus() == LoanStatus.PENDING) {
            return 0;
        }

        BigDecimal totalLoan = loan.getTotalRepayment();
        if (totalLoan.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }

        BigDecimal paidAmount = calculatePaidAmount(loan);
        BigDecimal progress = paidAmount
                .multiply(new BigDecimal("100"))
                .divide(totalLoan, 0, RoundingMode.HALF_UP);

        return progress.intValue();
    }

    @Override
    public int calculateRemainingInstallments(Loan loan) {
        if (loan == null) {
            return 0;
        }

        if (loan.getStatus() == LoanStatus.PAID) {
            return 0;
        }

        if (loan.getStatus() == LoanStatus.REJECTED ||
                loan.getStatus() == LoanStatus.PENDING) {
            return 0;
        }

        if (loan.getMonthlyPayment() == null ||
                loan.getMonthlyPayment().compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }

        BigDecimal remaining = calculateRemainingBalance(loan);
        BigDecimal installments = remaining.divide(
                loan.getMonthlyPayment(), 0, RoundingMode.UP
        );

        return installments.intValue();
    }

    @Override
    public int calculatePaidInstallments(Loan loan) {
        if (loan == null || loan.getDurationMonths() == null) {
            return 0;
        }

        return loan.getDurationMonths() - calculateRemainingInstallments(loan);
    }
}
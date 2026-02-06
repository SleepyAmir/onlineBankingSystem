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
        if (loan == null || loan.getRemainingBalance() == null) {
            return BigDecimal.ZERO;
        }
        return loan.getRemainingBalance();
    }


    @Override
    public BigDecimal calculatePaidAmount(Loan loan) {
        if (loan == null || loan.getPrincipal() == null) {
            return BigDecimal.ZERO;
        }

        if (loan.getStatus() == LoanStatus.PAID) {
            return loan.getPrincipal();
        }

        if (loan.getStatus() == LoanStatus.REJECTED ||
                loan.getStatus() == LoanStatus.PENDING) {
            return BigDecimal.ZERO;
        }

        // مبلغ اصل - مبلغ باقیمانده = مبلغ پرداخت شده
        BigDecimal paidAmount = loan.getPrincipal().subtract(loan.getRemainingBalance());
        return paidAmount.max(BigDecimal.ZERO);
    }


    @Override
    public int calculatePaymentProgress(Loan loan) {
        if (loan == null || loan.getPrincipal() == null) {
            return 0;
        }

        if (loan.getStatus() == LoanStatus.PAID) {
            return 100;
        }

        if (loan.getStatus() == LoanStatus.REJECTED ||
                loan.getStatus() == LoanStatus.PENDING) {
            return 0;
        }

        BigDecimal principal = loan.getPrincipal();
        if (principal.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }

        BigDecimal paidAmount = calculatePaidAmount(loan);
        BigDecimal progress = paidAmount
                .multiply(new BigDecimal("100"))
                .divide(principal, 0, RoundingMode.HALF_UP);

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

        BigDecimal remainingBalance = calculateRemainingBalance(loan);
        BigDecimal installments = remainingBalance.divide(
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

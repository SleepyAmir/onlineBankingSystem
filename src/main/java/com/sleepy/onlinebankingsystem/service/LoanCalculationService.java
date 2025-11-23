package com.sleepy.onlinebankingsystem.service;

import com.sleepy.onlinebankingsystem.model.entity.Loan;
import java.math.BigDecimal;

public interface LoanCalculationService {

    /**
     * محاسبه مبلغ باقیمانده وام
     */
    BigDecimal calculateRemainingBalance(Loan loan);

    /**
     * محاسبه مبلغ پرداخت شده
     */
    BigDecimal calculatePaidAmount(Loan loan);

    /**
     * محاسبه درصد پیشرفت پرداخت (0-100)
     */
    int calculatePaymentProgress(Loan loan);

    /**
     * محاسبه تعداد اقساط باقیمانده
     */
    int calculateRemainingInstallments(Loan loan);

    /**
     * محاسبه تعداد اقساط پرداخت شده
     */
    int calculatePaidInstallments(Loan loan);
}
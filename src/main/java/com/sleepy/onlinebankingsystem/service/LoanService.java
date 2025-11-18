package com.sleepy.onlinebankingsystem.service;

import com.sleepy.onlinebankingsystem.model.entity.Loan;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.model.enums.LoanStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface LoanService {
    // ========== متدهای CRUD موجود ==========
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

    // ========== متدهای بیزنس جدید ==========

    /**
     * درخواست وام جدید
     * @param accountNumber شماره حساب
     * @param principal مبلغ اصل وام
     * @param annualInterestRate نرخ بهره سالانه (درصد)
     * @param durationMonths مدت زمان (ماه)
     * @return وام ثبت شده با وضعیت PENDING
     */
    Loan applyForLoan(String accountNumber, BigDecimal principal,
                      BigDecimal annualInterestRate, Integer durationMonths) throws Exception;

    /**
     * تأیید وام (توسط مدیر)
     * @param loanId شناسه وام
     * @return وام با وضعیت APPROVED
     */
    Loan approveLoan(Long loanId) throws Exception;

    /**
     * رد وام (توسط مدیر)
     * @param loanId شناسه وام
     * @return وام با وضعیت REJECTED
     */
    Loan rejectLoan(Long loanId) throws Exception;

    /**
     * پرداخت قسط وام
     * @param loanId شناسه وام
     * @param amount مبلغ پرداختی (null = قسط استاندارد)
     * @return وام به‌روزرسانی شده
     */
    Loan payLoanInstallment(Long loanId, BigDecimal amount) throws Exception;

    /**
     * محاسبه قسط ماهانه
     * @param principal مبلغ اصل وام
     * @param annualInterestRate نرخ بهره سالانه
     * @param durationMonths مدت زمان
     * @return مبلغ قسط ماهانه
     */
    BigDecimal calculateMonthlyPayment(BigDecimal principal,
                                       BigDecimal annualInterestRate,
                                       Integer durationMonths) throws Exception;

    /**
     * اعتبارسنجی درخواست وام
     * @param accountNumber شماره حساب
     * @param principal مبلغ وام
     * @param annualInterestRate نرخ بهره
     * @param durationMonths مدت زمان
     */
    void validateLoanApplication(String accountNumber, BigDecimal principal,
                                 BigDecimal annualInterestRate,
                                 Integer durationMonths) throws Exception;

    Optional<Loan> findByIdWithUserAndAccount(Long id) throws Exception;
    Optional<Loan> findByLoanNumberWithUserAndAccount(String loanNumber) throws Exception;
}
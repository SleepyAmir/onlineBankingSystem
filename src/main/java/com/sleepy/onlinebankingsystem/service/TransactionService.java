package com.sleepy.onlinebankingsystem.service;

import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.entity.Transaction;
import com.sleepy.onlinebankingsystem.model.entity.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionService {
    // ========== متدهای CRUD موجود ==========
    Transaction save(Transaction transaction) throws Exception;
    Transaction update(Transaction transaction) throws Exception;
    void softDelete(Long id) throws Exception;
    void softDeleteByTransactionId(String transactionId) throws Exception;

    Optional<Transaction> findById(Long id) throws Exception;
    Optional<Transaction> findByTransactionId(String transactionId) throws Exception;
    List<Transaction> findByAccount(Account account) throws Exception;
    List<Transaction> findByUser(User user) throws Exception;
    List<Transaction> findByDateRange(LocalDateTime start, LocalDateTime end) throws Exception;
    List<Transaction> findAll(int page, int size) throws Exception;

    // ========== متدهای بیزنس جدید ==========

    /**
     * واریز وجه به حساب
     * @param toAccountNumber شماره حساب مقصد
     * @param amount مبلغ
     * @param description توضیحات (اختیاری)
     * @return تراکنش ثبت شده
     */
    Transaction processDeposit(String toAccountNumber, BigDecimal amount, String description) throws Exception;

    /**
     * برداشت وجه از حساب
     * @param fromAccountNumber شماره حساب مبدأ
     * @param amount مبلغ
     * @param description توضیحات (اختیاری)
     * @return تراکنش ثبت شده
     */
    Transaction processWithdrawal(String fromAccountNumber, BigDecimal amount, String description) throws Exception;

    /**
     * انتقال وجه بین دو حساب
     * @param fromAccountNumber شماره حساب مبدأ
     * @param toAccountNumber شماره حساب مقصد
     * @param amount مبلغ
     * @param description توضیحات (اختیاری)
     * @return تراکنش ثبت شده
     */
    Transaction processTransfer(String fromAccountNumber, String toAccountNumber,
                                BigDecimal amount, String description) throws Exception;

    /**
     * انتقال با شماره کارت (برای API)
     * @param fromCardNumber شماره کارت مبدأ
     * @param toCardNumber شماره کارت مقصد
     * @param amount مبلغ
     * @param description توضیحات
     * @return تراکنش ثبت شده
     */
    Transaction processCardTransfer(String fromCardNumber, String toCardNumber,
                                    BigDecimal amount, String description) throws Exception;

    /**
     * اعتبارسنجی تراکنش
     * @param amount مبلغ
     * @param fromAccount حساب مبدأ (nullable)
     * @param toAccount حساب مقصد (nullable)
     */
    void validateTransaction(BigDecimal amount, Account fromAccount, Account toAccount) throws Exception;

    /**
     * برگشت تراکنش (Reverse)
     * @param transactionId شناسه تراکنش
     * @return تراکنش برگشت خورده
     */
    Transaction reverseTransaction(String transactionId) throws Exception;
}
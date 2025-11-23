package com.sleepy.onlinebankingsystem.service;

import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.model.enums.AccountStatus;
import com.sleepy.onlinebankingsystem.model.enums.AccountType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountService {

    // ========== متدهای CRUD موجود ==========
    Account save(Account account) throws Exception;
    Account update(Account account) throws Exception;
    void softDelete(Long id) throws Exception;
    void softDeleteByAccountNumber(String accountNumber) throws Exception;

    Optional<Account> findById(Long id) throws Exception;
    Optional<Account> findByIdWithUser(Long id) throws Exception;
    Optional<Account> findByAccountNumber(String accountNumber) throws Exception;
    List<Account> findByUser(User user) throws Exception;
    List<Account> findByUserWithUser(User user) throws Exception;
    List<Account> findByStatus(AccountStatus status) throws Exception;
    List<Account> findAll(int page, int size) throws Exception;

    // ========== متدهای بیزنس جدید ==========

    /**
     * ایجاد حساب جدید
     * @param userId شناسه کاربر
     * @param accountType نوع حساب
     * @param initialBalance موجودی اولیه (nullable)
     * @return حساب ایجاد شده
     */
    Account createAccount(Long userId, AccountType accountType, BigDecimal initialBalance)
            throws Exception;

    /**
     * تغییر وضعیت حساب
     * @param accountId شناسه حساب
     * @param newStatus وضعیت جدید
     * @return حساب به‌روزرسانی شده
     */
    Account changeAccountStatus(Long accountId, AccountStatus newStatus) throws Exception;

    /**
     * فریز کردن حساب
     * @param accountId شناسه حساب
     * @return حساب فریز شده
     */
    Account freezeAccount(Long accountId) throws Exception;

    /**
     * بستن حساب
     * @param accountId شناسه حساب
     * @return حساب بسته شده
     */
    Account closeAccount(Long accountId) throws Exception;

    /**
     * اعتبارسنجی حساب برای تراکنش
     * @param accountNumber شماره حساب
     * @throws Exception اگر حساب نامعتبر باشد
     */
    void validateAccountForTransaction(String accountNumber) throws Exception;

    /**
     * اعتبارسنجی حساب برای حذف
     * @param accountId شناسه حساب
     * @throws Exception اگر حساب قابل حذف نباشد
     */
    void validateAccountForDeletion(Long accountId) throws Exception;

    /**
     * بررسی موجودی کافی
     * @param accountNumber شماره حساب
     * @param amount مبلغ مورد نیاز
     * @throws Exception اگر موجودی کافی نباشد
     */
    void validateSufficientBalance(String accountNumber, BigDecimal amount) throws Exception;

    /**
     * تولید شماره حساب یکتا
     * @return شماره حساب 16 رقمی
     */
    String generateAccountNumber() throws Exception;

    Optional<Account> findByAccountNumberWithUser(String accountNumber) throws Exception;
    List<Account> findAllWithUsers(int page, int size) throws Exception;


}
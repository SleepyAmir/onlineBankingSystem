package com.sleepy.onlinebankingsystem.service.impl;

import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.entity.Card;
import com.sleepy.onlinebankingsystem.model.entity.Transaction;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.model.enums.AccountStatus;
import com.sleepy.onlinebankingsystem.model.enums.TransactionStatus;
import com.sleepy.onlinebankingsystem.model.enums.TransactionType;
import com.sleepy.onlinebankingsystem.repository.TransactionRepository;
import com.sleepy.onlinebankingsystem.service.AccountService;
import com.sleepy.onlinebankingsystem.service.CardService;
import com.sleepy.onlinebankingsystem.service.TransactionService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@ApplicationScoped
public class TransactionServiceImpl implements TransactionService {

    @Inject
    TransactionRepository transactionRepository;

    @Inject
    AccountService accountService;

    @Inject
    CardService cardService;

    // ========== متدهای CRUD موجود ==========

    @Transactional
    @Override
    public Transaction save(Transaction transaction) throws Exception {
        log.info("Saving transaction: {}", transaction.getTransactionId());
        return transactionRepository.save(transaction);
    }

    @Transactional
    @Override
    public Transaction update(Transaction transaction) throws Exception {
        if (transaction.getId() == null) throw new IllegalArgumentException("ID is required");
        return transactionRepository.save(transaction);
    }

    @Transactional
    @Override
    public void softDelete(Long id) throws Exception {
        transactionRepository.softDelete(id);
    }

    @Transactional
    @Override
    public void softDeleteByTransactionId(String transactionId) throws Exception {
        transactionRepository.findByTransactionId(transactionId)
                .ifPresent(t -> transactionRepository.softDelete(t.getId()));
    }

    @Override
    public Optional<Transaction> findById(Long id) throws Exception {
        return transactionRepository.findById(id);
    }

    @Override
    public Optional<Transaction> findByTransactionId(String transactionId) throws Exception {
        return transactionRepository.findByTransactionId(transactionId);
    }

    @Override
    public List<Transaction> findByAccount(Account account) throws Exception {
        return transactionRepository.findByAccount(account);
    }

    @Override
    public List<Transaction> findByUser(User user) throws Exception {
        return transactionRepository.findByUser(user);
    }

    @Override
    public List<Transaction> findByDateRange(LocalDateTime start, LocalDateTime end) throws Exception {
        return transactionRepository.findByDateRange(start, end);
    }

    @Override
    public List<Transaction> findAll(int page, int size) throws Exception {
        return transactionRepository.findAll(page, size);
    }

    // ========== متدهای بیزنس جدید ==========

    @Transactional
    @Override
    public Transaction processDeposit(String toAccountNumber, BigDecimal amount, String description)
            throws Exception {

        log.info("Processing deposit: {} to account {}", amount, toAccountNumber);

        // 1. اعتبارسنجی مبلغ
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("مبلغ باید بیشتر از صفر باشد");
        }

        // 2. پیدا کردن حساب مقصد
        Account toAccount = accountService.findByAccountNumber(toAccountNumber)
                .orElseThrow(() -> new IllegalArgumentException("حساب مقصد یافت نشد"));

        // 3. بررسی وضعیت حساب
        if (toAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("حساب مقصد فعال نیست");
        }

        // 4. افزایش موجودی
        toAccount.setBalance(toAccount.getBalance().add(amount));
        accountService.update(toAccount);

        // 5. ثبت تراکنش
        Transaction transaction = Transaction.builder()
                .transactionId(generateTransactionId())
                .toAccount(toAccount)
                .amount(amount)
                .type(TransactionType.DEPOSIT)
                .transactionDate(LocalDateTime.now())
                .status(TransactionStatus.COMPLETED)
                .description(description != null ? description : "واریز وجه")
                .referenceNumber(generateReferenceNumber())
                .build();

        Transaction saved = transactionRepository.save(transaction);
        log.info("Deposit completed successfully: {}", saved.getTransactionId());

        return saved;
    }

    @Transactional
    @Override
    public Transaction processWithdrawal(String fromAccountNumber, BigDecimal amount, String description)
            throws Exception {

        log.info("Processing withdrawal: {} from account {}", amount, fromAccountNumber);

        // 1. اعتبارسنجی مبلغ
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("مبلغ باید بیشتر از صفر باشد");
        }

        // 2. پیدا کردن حساب مبدأ
        Account fromAccount = accountService.findByAccountNumber(fromAccountNumber)
                .orElseThrow(() -> new IllegalArgumentException("حساب مبدأ یافت نشد"));

        // 3. بررسی وضعیت حساب
        if (fromAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("حساب مبدأ فعال نیست");
        }

        // 4. بررسی موجودی کافی
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("موجودی حساب کافی نیست");
        }

        // 5. کاهش موجودی
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        accountService.update(fromAccount);

        // 6. ثبت تراکنش
        Transaction transaction = Transaction.builder()
                .transactionId(generateTransactionId())
                .fromAccount(fromAccount)
                .amount(amount)
                .type(TransactionType.WITHDRAWAL)
                .transactionDate(LocalDateTime.now())
                .status(TransactionStatus.COMPLETED)
                .description(description != null ? description : "برداشت وجه")
                .referenceNumber(generateReferenceNumber())
                .build();

        Transaction saved = transactionRepository.save(transaction);
        log.info("Withdrawal completed successfully: {}", saved.getTransactionId());

        return saved;
    }

    @Transactional
    @Override
    public Transaction processTransfer(String fromAccountNumber, String toAccountNumber,
                                       BigDecimal amount, String description) throws Exception {

        log.info("Processing transfer: {} from {} to {}", amount, fromAccountNumber, toAccountNumber);

        // 1. اعتبارسنجی مبلغ
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("مبلغ باید بیشتر از صفر باشد");
        }

        // 2. پیدا کردن حساب‌ها
        Account fromAccount = accountService.findByAccountNumber(fromAccountNumber)
                .orElseThrow(() -> new IllegalArgumentException("حساب مبدأ یافت نشد"));

        Account toAccount = accountService.findByAccountNumber(toAccountNumber)
                .orElseThrow(() -> new IllegalArgumentException("حساب مقصد یافت نشد"));

        // 3. بررسی انتقال به خودی خود
        if (fromAccount.getId().equals(toAccount.getId())) {
            throw new IllegalArgumentException("انتقال به همان حساب امکان‌پذیر نیست");
        }

        // 4. اعتبارسنجی کامل
        validateTransaction(amount, fromAccount, toAccount);

        // 5. انجام انتقال
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        accountService.update(fromAccount);
        accountService.update(toAccount);

        // 6. ثبت تراکنش
        Transaction transaction = Transaction.builder()
                .transactionId(generateTransactionId())
                .fromAccount(fromAccount)
                .toAccount(toAccount)
                .amount(amount)
                .type(TransactionType.TRANSFER)
                .transactionDate(LocalDateTime.now())
                .status(TransactionStatus.COMPLETED)
                .description(description != null ? description : "انتقال وجه")
                .referenceNumber(generateReferenceNumber())
                .build();

        Transaction saved = transactionRepository.save(transaction);
        log.info("Transfer completed successfully: {}", saved.getTransactionId());

        return saved;
    }

    @Transactional
    @Override
    public Transaction processCardTransfer(String fromCardNumber, String toCardNumber,
                                           BigDecimal amount, String description) throws Exception {

        log.info("Processing card transfer: {} from card {} to card {}",
                amount, fromCardNumber, toCardNumber);

        // 1. اعتبارسنجی مبلغ
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("مبلغ باید بیشتر از صفر باشد");
        }

        // 2. پیدا کردن کارت‌ها
        Card fromCard = cardService.findByCardNumber(fromCardNumber)
                .orElseThrow(() -> new IllegalArgumentException("کارت مبدأ یافت نشد"));

        Card toCard = cardService.findByCardNumber(toCardNumber)
                .orElseThrow(() -> new IllegalArgumentException("کارت مقصد یافت نشد"));

        // 3. بررسی فعال بودن کارت‌ها
        if (!fromCard.isActive()) {
            throw new IllegalStateException("کارت مبدأ غیرفعال است");
        }
        if (!toCard.isActive()) {
            throw new IllegalStateException("کارت مقصد غیرفعال است");
        }

        // 4. دریافت حساب‌های مربوطه
        Account fromAccount = fromCard.getAccount();
        Account toAccount = toCard.getAccount();

        // 5. بررسی انتقال به خودی خود
        if (fromAccount.getId().equals(toAccount.getId())) {
            throw new IllegalArgumentException("انتقال به همان حساب امکان‌پذیر نیست");
        }

        // 6. اعتبارسنجی کامل
        validateTransaction(amount, fromAccount, toAccount);

        // 7. انجام انتقال
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        accountService.update(fromAccount);
        accountService.update(toAccount);

        // 8. ثبت تراکنش
        Transaction transaction = Transaction.builder()
                .transactionId(generateTransactionId())
                .fromAccount(fromAccount)
                .toAccount(toAccount)
                .amount(amount)
                .type(TransactionType.TRANSFER)
                .transactionDate(LocalDateTime.now())
                .status(TransactionStatus.COMPLETED)
                .description(description != null ? description : "انتقال کارت به کارت")
                .referenceNumber(generateReferenceNumber())
                .build();

        Transaction saved = transactionRepository.save(transaction);
        log.info("Card transfer completed successfully: {}", saved.getTransactionId());

        return saved;
    }

    @Override
    public void validateTransaction(BigDecimal amount, Account fromAccount, Account toAccount)
            throws Exception {

        // 1. بررسی مبلغ
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("مبلغ باید بیشتر از صفر باشد");
        }

        // 2. بررسی حساب مبدأ
        if (fromAccount != null) {
            if (fromAccount.getStatus() != AccountStatus.ACTIVE) {
                throw new IllegalStateException("حساب مبدأ فعال نیست");
            }
            if (fromAccount.getBalance().compareTo(amount) < 0) {
                throw new IllegalStateException("موجودی حساب مبدأ کافی نیست");
            }
        }

        // 3. بررسی حساب مقصد
        if (toAccount != null) {
            if (toAccount.getStatus() != AccountStatus.ACTIVE) {
                throw new IllegalStateException("حساب مقصد فعال نیست");
            }
        }
    }

    @Transactional
    @Override
    public Transaction reverseTransaction(String transactionId) throws Exception {

        log.info("Reversing transaction: {}", transactionId);

        // 1. پیدا کردن تراکنش
        Transaction original = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("تراکنش یافت نشد"));

        // 2. بررسی امکان برگشت
        if (original.getStatus() == TransactionStatus.REVERSED) {
            throw new IllegalStateException("این تراکنش قبلاً برگشت خورده است");
        }
        if (original.getStatus() == TransactionStatus.FAILED) {
            throw new IllegalStateException("تراکنش ناموفق قابل برگشت نیست");
        }

        // 3. برگشت موجودی‌ها
        Account fromAccount = original.getFromAccount();
        Account toAccount = original.getToAccount();

        if (fromAccount != null && toAccount != null) {
            // انتقال - برعکس می‌کنیم
            fromAccount.setBalance(fromAccount.getBalance().add(original.getAmount()));
            toAccount.setBalance(toAccount.getBalance().subtract(original.getAmount()));
            accountService.update(fromAccount);
            accountService.update(toAccount);
        } else if (fromAccount != null) {
            // برداشت - پس می‌دیم
            fromAccount.setBalance(fromAccount.getBalance().add(original.getAmount()));
            accountService.update(fromAccount);
        } else if (toAccount != null) {
            // واریز - برمی‌گردونیم
            toAccount.setBalance(toAccount.getBalance().subtract(original.getAmount()));
            accountService.update(toAccount);
        }

        // 4. تغییر وضعیت تراکنش اصلی
        original.setStatus(TransactionStatus.REVERSED);
        transactionRepository.save(original);

        // 5. ثبت تراکنش برگشتی
        Transaction reversal = Transaction.builder()
                .transactionId(generateTransactionId())
                .fromAccount(toAccount)  // برعکس
                .toAccount(fromAccount)  // برعکس
                .amount(original.getAmount())
                .type(original.getType())
                .transactionDate(LocalDateTime.now())
                .status(TransactionStatus.COMPLETED)
                .description("برگشت تراکنش: " + transactionId)
                .referenceNumber(generateReferenceNumber())
                .build();

        Transaction saved = transactionRepository.save(reversal);
        log.info("Transaction reversed successfully: {}", saved.getTransactionId());

        return saved;
    }

    // ========== متدهای کمکی ==========

    private String generateTransactionId() {
        return "TRX" + System.currentTimeMillis() + "-" +
                UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String generateReferenceNumber() {
        return UUID.randomUUID().toString().substring(0, 10).toUpperCase();
    }
}
package com.sleepy.onlinebankingsystem.service.impl;

import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.model.enums.AccountStatus;
import com.sleepy.onlinebankingsystem.model.enums.AccountType;
import com.sleepy.onlinebankingsystem.repository.AccountRepository;
import com.sleepy.onlinebankingsystem.service.AccountService;
import com.sleepy.onlinebankingsystem.service.UserService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class AccountServiceImpl implements AccountService {

    @Inject
    AccountRepository accountRepository;

    @Inject
    UserService userService;

    private final SecureRandom random = new SecureRandom();

    // حدود مجاز
    private static final BigDecimal MIN_INITIAL_BALANCE = BigDecimal.ZERO;
    private static final BigDecimal MAX_INITIAL_BALANCE = new BigDecimal("10000000000"); // 10B
    private static final int ACCOUNT_NUMBER_LENGTH = 16;
    private static final int MAX_GENERATION_ATTEMPTS = 100;

    // ========== متدهای CRUD موجود ==========

    @Transactional
    @Override
    public Account save(Account account) throws Exception {
        log.info("Saving account: {}", account.getAccountNumber());

        if (accountRepository.findByAccountNumber(account.getAccountNumber()).isPresent()) {
            throw new IllegalArgumentException("Account number already exists: " + account.getAccountNumber());
        }

        return accountRepository.save(account);
    }

    @Transactional
    @Override
    public Account update(Account account) throws Exception {
        if (account.getId() == null) throw new IllegalArgumentException("ID is required for update");
        return accountRepository.save(account);
    }

    @Transactional
    @Override
    public void softDelete(Long id) throws Exception {
        accountRepository.softDelete(id);
    }

    @Transactional
    @Override
    public void softDeleteByAccountNumber(String accountNumber) throws Exception {
        accountRepository.findByAccountNumber(accountNumber)
                .ifPresent(acc -> accountRepository.softDelete(acc.getId()));
    }

    @Override
    public Optional<Account> findById(Long id) throws Exception {
        return accountRepository.findById(id);
    }

    @Override
    public Optional<Account> findByIdWithUser(Long id) throws Exception {
        log.debug("Fetching account with user by ID: {}", id);
        try {
            Account account = accountRepository.getEntityManager()
                    .createNamedQuery(Account.FIND_BY_ID_WITH_USER, Account.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return Optional.of(account);

        } catch (NoResultException e) {
            log.debug("No account found with id: {}", id);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error in findByIdWithUser for id: {}", id, e);
            throw e;
        }
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) throws Exception {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    @Override
    public List<Account> findByUser(User user) throws Exception {
        return accountRepository.findByUser(user);
    }

//    @Override
//    public List<Account> findByUserWithUser(User user) throws Exception {
//        log.info("Fetching accounts with user for user ID: {}", user.getId());
//        return accountRepository.findByUserWithUser(user);
//    }
@Override
public List<Account> findByUserWithUser(User user) throws Exception {
    log.info("Fetching accounts with user for user ID: {}", user.getId());
    List<Account> accounts = accountRepository.findByUserWithUser(user);

    // ✅ Force Initialize کردن Enum ها
    accounts.forEach(account -> {
        // این خط باعث می‌شود Hibernate تمام فیلدهای lazy را بارگذاری کند
        Hibernate.initialize(account);
        account.getType(); // Force load enum
        account.getStatus(); // Force load enum
    });

    return accounts;
}

    @Override
    public List<Account> findByStatus(AccountStatus status) throws Exception {
        return accountRepository.findByStatus(status);
    }

    @Override
    public List<Account> findAll(int page, int size) throws Exception {
        return accountRepository.findAll(page, size);
    }

    // ========== متدهای بیزنس جدید ==========

    @Transactional
    @Override
    public Account createAccount(Long userId, AccountType accountType, BigDecimal initialBalance)
            throws Exception {

        log.info("Creating account for user ID: {} with type: {}", userId, accountType);

        // 1. اعتبارسنجی ورودی
        if (userId == null) {
            throw new IllegalArgumentException("شناسه کاربر الزامی است");
        }
        if (accountType == null) {
            throw new IllegalArgumentException("نوع حساب الزامی است");
        }

        // 2. پیدا کردن کاربر
        User user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("کاربر یافت نشد"));

        // 3. بررسی فعال بودن کاربر
        if (!user.isActive()) {
            throw new IllegalStateException("کاربر باید فعال باشد");
        }

        // 4. اعتبارسنجی موجودی اولیه
        BigDecimal balance = initialBalance != null ? initialBalance : BigDecimal.ZERO;

        if (balance.compareTo(MIN_INITIAL_BALANCE) < 0) {
            throw new IllegalArgumentException("موجودی اولیه نمی‌تواند منفی باشد");
        }
        if (balance.compareTo(MAX_INITIAL_BALANCE) > 0) {
            throw new IllegalArgumentException(
                    String.format("موجودی اولیه نمی‌تواند بیشتر از %.0f باشد", MAX_INITIAL_BALANCE)
            );
        }

        // 5. تولید شماره حساب یکتا
        String accountNumber = generateAccountNumber();

        // 6. ساخت حساب
        Account account = Account.builder()
                .user(user)
                .accountNumber(accountNumber)
                .type(accountType)
                .balance(balance)
                .status(AccountStatus.ACTIVE)
                .build();

        Account savedAccount = accountRepository.save(account);
        log.info("Account created successfully: {}", savedAccount.getAccountNumber());

        return savedAccount;
    }

    @Transactional
    @Override
    public Account changeAccountStatus(Long accountId, AccountStatus newStatus) throws Exception {

        log.info("Changing account status: ID {} to {}", accountId, newStatus);

        // 1. اعتبارسنجی
        if (accountId == null) {
            throw new IllegalArgumentException("شناسه حساب الزامی است");
        }
        if (newStatus == null) {
            throw new IllegalArgumentException("وضعیت جدید الزامی است");
        }

        // 2. پیدا کردن حساب
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("حساب یافت نشد"));

        // 3. بررسی تغییر
        if (account.getStatus() == newStatus) {
            throw new IllegalStateException("حساب از قبل در این وضعیت است");
        }

        // 4. تغییر وضعیت
        AccountStatus oldStatus = account.getStatus();
        account.setStatus(newStatus);
        Account updatedAccount = accountRepository.save(account);

        log.info("Account status changed: {} from {} to {}",
                account.getAccountNumber(), oldStatus, newStatus);

        return updatedAccount;
    }

    @Transactional
    @Override
    public Account freezeAccount(Long accountId) throws Exception {

        log.info("Freezing account: ID {}", accountId);

        // فراخوانی changeAccountStatus
        return changeAccountStatus(accountId, AccountStatus.FROZEN);
    }

    @Transactional
    @Override
    public Account closeAccount(Long accountId) throws Exception {

        log.info("Closing account: ID {}", accountId);

        // 1. پیدا کردن حساب
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("حساب یافت نشد"));

        // 2. بررسی موجودی
        if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalStateException("حساب با موجودی مثبت قابل بستن نیست");
        }

        // 3. بستن حساب
        return changeAccountStatus(accountId, AccountStatus.CLOSED);
    }

    @Override
    public void validateAccountForTransaction(String accountNumber) throws Exception {

        // 1. بررسی وجود حساب
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("حساب یافت نشد"));

        // 2. بررسی وضعیت
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("حساب باید فعال باشد");
        }

        log.debug("Account validated for transaction: {}", accountNumber);
    }

    @Override
    public void validateAccountForDeletion(Long accountId) throws Exception {

        // 1. بررسی وجود حساب
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("حساب یافت نشد"));

        // 2. بررسی موجودی
        if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalStateException("حساب با موجودی مثبت قابل حذف نیست");
        }

        // 3. بررسی وضعیت
        if (account.getStatus() == AccountStatus.ACTIVE) {
            throw new IllegalStateException("حساب فعال باید ابتدا بسته شود");
        }

        log.debug("Account validated for deletion: {}", account.getAccountNumber());
    }

    @Override
    public void validateSufficientBalance(String accountNumber, BigDecimal amount)
            throws Exception {

        // 1. بررسی مبلغ
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("مبلغ باید بیشتر از صفر باشد");
        }

        // 2. پیدا کردن حساب
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("حساب یافت نشد"));

        // 3. بررسی موجودی
        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException(
                    String.format("موجودی کافی نیست. موجودی فعلی: %.2f, مبلغ مورد نیاز: %.2f",
                            account.getBalance(), amount)
            );
        }

        log.debug("Sufficient balance validated for account: {}", accountNumber);
    }

    @Override
    public String generateAccountNumber() throws Exception {

        int attempts = 0;
        String accountNumber;

        do {
            accountNumber = generateRandomAccountNumber();
            attempts++;

            if (attempts >= MAX_GENERATION_ATTEMPTS) {
                throw new IllegalStateException("خطا در تولید شماره حساب یکتا");
            }

        } while (accountRepository.findByAccountNumber(accountNumber).isPresent());

        log.debug("Generated unique account number: {}", accountNumber);
        return accountNumber;
    }

    // ========== متدهای کمکی ==========

    /**
     * تولید شماره حساب 16 رقمی تصادفی
     */
    private String generateRandomAccountNumber() {
        StringBuilder sb = new StringBuilder(ACCOUNT_NUMBER_LENGTH);

        // رقم اول نباید صفر باشد
        sb.append(random.nextInt(9) + 1);

        // 15 رقم بعدی
        for (int i = 0; i < ACCOUNT_NUMBER_LENGTH - 1; i++) {
            sb.append(random.nextInt(10));
        }

        return sb.toString();
    }
}
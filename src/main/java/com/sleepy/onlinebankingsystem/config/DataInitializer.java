package com.sleepy.onlinebankingsystem.config;

import com.sleepy.onlinebankingsystem.model.entity.*;
import com.sleepy.onlinebankingsystem.model.enums.*;
import com.sleepy.onlinebankingsystem.service.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.*;

/**
 * DataInitializer با استفاده از Service Layer
 * تمام عملیات از طریق Service انجام می‌شود
 */
@Slf4j
@WebListener
@ApplicationScoped
public class DataInitializer implements ServletContextListener {

    @Inject private UserService userService;
    @Inject private AccountService accountService;
    @Inject private CardService cardService;
    @Inject private TransactionService transactionService;
    @Inject private LoanService loanService;

    private static final String DEFAULT_PASSWORD = "123456";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            initializeData();
        } catch (Exception e) {
            log.error("Data initialization failed", e);
            throw new RuntimeException("Failed to initialize data", e);
        }
    }

    @Transactional
    public void initializeData() throws Exception {
        log.info("Starting Data Initialization...");

        if (isDatabaseInitialized()) {
            log.info("Database already initialized. Skipping...");
            return;
        }

        // 1️⃣ ایجاد کاربران با استفاده از UserService
        Map<String, User> users = createInitialUsers();

        // 2️⃣ ایجاد حساب‌ها با استفاده از AccountService
        Map<String, List<Account>> accounts = createAccounts(users);

        // 3️⃣ ایجاد کارت‌ها با استفاده از CardService
        createCards(accounts);

        // 4️⃣ ایجاد تراکنش‌ها با استفاده از TransactionService
        createSampleTransactions(accounts);

        // 5️⃣ ایجاد وام‌ها با استفاده از LoanService
        createSampleLoans(accounts);

        log.info("Data Initialization Completed Successfully!");
        printLoginCredentials();
    }

    private boolean isDatabaseInitialized() {
        try {
            return userService.findByUsername("admin").isPresent();
        } catch (Exception e) {
            return false;
        }
    }

    // ========== ایجاد کاربران ==========

    private Map<String, User> createInitialUsers() throws Exception {
        Map<String, User> users = new LinkedHashMap<>();

        log.info("Creating users...");

        // Admin
        users.put("admin", userService.createUserByAdmin(
                "admin", DEFAULT_PASSWORD, "مدیر", "سیستم",
                "09121111111", "0011223344", UserRole.ADMIN, true
        ));

        // Manager
        users.put("manager", userService.createUserByAdmin(
                "manager", DEFAULT_PASSWORD, "مدیر", "بانک",
                "09122222222", "1122334455", UserRole.MANAGER, true
        ));

        // Customer 1
        users.put("amir", userService.registerUser(
                "amir", DEFAULT_PASSWORD, "امیر", "احمدی",
                "09123333333", "1234567890"
        ));

        // Customer 2
        users.put("sara", userService.registerUser(
                "sara", DEFAULT_PASSWORD, "سارا", "کریمی",
                "09124444444", "0987654321"
        ));

        log.info("Created {} users", users.size());
        return users;
    }

    // ========== ایجاد حساب‌ها ==========

    private Map<String, List<Account>> createAccounts(Map<String, User> users) throws Exception {
        Map<String, List<Account>> accounts = new HashMap<>();

        log.info("Creating accounts...");

        // Amir's Accounts
        List<Account> amirAccounts = new ArrayList<>();
        amirAccounts.add(accountService.createAccount(
                users.get("amir").getId(),
                AccountType.SAVINGS,
                new BigDecimal("10000000")
        ));
        amirAccounts.add(accountService.createAccount(
                users.get("amir").getId(),
                AccountType.CHECKING,
                new BigDecimal("5000000")
        ));
        accounts.put("amir", amirAccounts);

        // Sara's Accounts
        List<Account> saraAccounts = new ArrayList<>();
        saraAccounts.add(accountService.createAccount(
                users.get("sara").getId(),
                AccountType.SAVINGS,
                new BigDecimal("8000000")
        ));
        saraAccounts.add(accountService.createAccount(
                users.get("sara").getId(),
                AccountType.CHECKING,
                new BigDecimal("3000000")
        ));
        accounts.put("sara", saraAccounts);

        log.info("Created {} accounts",
                accounts.values().stream().mapToLong(List::size).sum());
        return accounts;
    }

    // ========== ایجاد کارت‌ها ==========

    private void createCards(Map<String, List<Account>> accounts) throws Exception {
        log.info("Creating cards...");
        int cardCount = 0;

        for (List<Account> accountList : accounts.values()) {
            for (Account account : accountList) {
                // صدور کارت با استفاده از CardService
                cardService.issueCard(account.getId(), CardType.DEBIT);
                cardCount++;
            }
        }

        log.info("Created {} cards", cardCount);
    }

    // ========== ایجاد تراکنش‌ها ==========

    private void createSampleTransactions(Map<String, List<Account>> accounts) throws Exception {
        log.info("Creating sample transactions...");

        Account amirSavings = accounts.get("amir").get(0);
        Account amirChecking = accounts.get("amir").get(1);
        Account saraSavings = accounts.get("sara").get(0);
        Account saraChecking = accounts.get("sara").get(1);

        // 1. واریز به حساب امیر
        transactionService.processDeposit(
                amirSavings.getAccountNumber(),
                new BigDecimal("2000000"),
                "واریز اولیه"
        );

        // 2. برداشت از حساب سارا
        transactionService.processWithdrawal(
                saraChecking.getAccountNumber(),
                new BigDecimal("500000"),
                "برداشت نمونه"
        );

        // 3. انتقال از امیر به سارا
        transactionService.processTransfer(
                amirSavings.getAccountNumber(),
                saraSavings.getAccountNumber(),
                new BigDecimal("1500000"),
                "انتقال نمونه"
        );

        log.info("Created sample transactions");
    }

    // ========== ایجاد وام‌ها ==========

    private void createSampleLoans(Map<String, List<Account>> accounts) throws Exception {
        log.info("Creating sample loans...");

        Account amirAccount = accounts.get("amir").get(0);
        Account saraAccount = accounts.get("sara").get(0);

        // 1. وام در انتظار تأیید (Amir)
        loanService.applyForLoan(
                amirAccount.getAccountNumber(),
                new BigDecimal("50000000"),
                new BigDecimal("18.00"),
                24
        );

        // 2. وام تأیید شده (Sara)
        Loan saraLoan = loanService.applyForLoan(
                saraAccount.getAccountNumber(),
                new BigDecimal("30000000"),
                new BigDecimal("15.00"),
                36
        );

        // تأیید وام سارا
        loanService.approveLoan(saraLoan.getId());

        log.info("Created sample loans");
    }

    // ========== Helpers ==========

    private void printLoginCredentials() {
        log.info("========================================");
        log.info("Login Credentials (Password: {})", DEFAULT_PASSWORD);
        log.info("Admin    : admin");
        log.info("Manager  : manager");
        log.info("Customer1: amir");
        log.info("Customer2: sara");
        log.info("========================================");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.info("Application context destroyed");
    }
}
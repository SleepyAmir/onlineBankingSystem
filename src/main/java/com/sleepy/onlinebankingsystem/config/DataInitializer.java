package com.sleepy.onlinebankingsystem.config;

import com.sleepy.onlinebankingsystem.model.entity.*;
import com.sleepy.onlinebankingsystem.model.enums.*;
import com.sleepy.onlinebankingsystem.service.*;
import com.sleepy.onlinebankingsystem.utils.PasswordUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@WebListener
@ApplicationScoped
public class DataInitializer implements ServletContextListener {

    @Inject private UserService userService;
    @Inject private RoleService roleService;
    @Inject private AuthorityService authorityService;
    @Inject private AccountService accountService;
    @Inject private CardService cardService;
    @Inject private TransactionService transactionService;
    @Inject private LoanService loanService;
    @Inject private PasswordUtil passwordUtil;

    private final SecureRandom random = new SecureRandom();
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

        Map<String, User> users = createInitialUsers();
        Map<String, Role> roles = createRolesForUsers(users);
        createAuthorities(roles);
        Map<String, List<Account>> accounts = createAccounts(users);
        createCards(accounts);
        createSampleTransactions(accounts);
        createSampleLoans(users, accounts);

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

    private Map<String, User> createInitialUsers() throws Exception {
        Map<String, User> users = new LinkedHashMap<>();
        String hashed = passwordUtil.hash(DEFAULT_PASSWORD);

        users.put("admin", userService.save(User.builder()
                .username("admin").password(hashed).firstName("مدیر").lastName("سیستم")
                .phone("09121111111").nationalCode("0011223344").active(true).build()));

        users.put("manager", userService.save(User.builder()
                .username("manager").password(hashed).firstName("مدیر").lastName("بانک")
                .phone("09122222222").nationalCode("1122334455").active(true).build()));

        users.put("amir", userService.save(User.builder()
                .username("amir").password(hashed).firstName("امیر").lastName("احمدی")
                .phone("09123333333").nationalCode("1234567890").active(true).build()));

        users.put("sara", userService.save(User.builder()
                .username("sara").password(hashed).firstName("سارا").lastName("کریمی")
                .phone("09124444444").nationalCode("0987654321").active(true).build()));

        return users;
    }

    private Map<String, Role> createRolesForUsers(Map<String, User> users) throws Exception {
        Map<String, Role> roles = new HashMap<>();

        Role adminRole = roleService.save(Role.builder().user(users.get("admin")).role(UserRole.ADMIN).build());
        Role managerRole = roleService.save(Role.builder().user(users.get("manager")).role(UserRole.MANAGER).build());
        Role amirRole = roleService.save(Role.builder().user(users.get("amir")).role(UserRole.CUSTOMER).build());
        Role saraRole = roleService.save(Role.builder().user(users.get("sara")).role(UserRole.CUSTOMER).build());

        roles.put("admin", adminRole);
        roles.put("manager", managerRole);
        roles.put("amir", amirRole);
        roles.put("sara", saraRole);

        return roles;
    }

    private void createAuthorities(Map<String, Role> roles) throws Exception {
        Role adminRole = roles.get("admin");
        Role managerRole = roles.get("manager");
        Role customerRole = roles.get("amir");

        // Admin:
        authorityService.save(Authority.builder().role(adminRole).resource("ACCOUNT").action("CREATE").build());
        authorityService.save(Authority.builder().role(adminRole).resource("ACCOUNT").action("READ").build());
        authorityService.save(Authority.builder().role(adminRole).resource("ACCOUNT").action("UPDATE").build());
        authorityService.save(Authority.builder().role(adminRole).resource("ACCOUNT").action("DELETE").build());

        authorityService.save(Authority.builder().role(adminRole).resource("TRANSACTION").action("CREATE").build());
        authorityService.save(Authority.builder().role(adminRole).resource("TRANSACTION").action("READ").build());

        // Manager:
        authorityService.save(Authority.builder().role(managerRole).resource("ACCOUNT").action("READ").build());
        authorityService.save(Authority.builder().role(managerRole).resource("ACCOUNT").action("UPDATE").build());
        authorityService.save(Authority.builder().role(managerRole).resource("TRANSACTION").action("READ").build());

        // Customer:
        authorityService.save(Authority.builder().role(customerRole).resource("ACCOUNT").action("READ").build());
        authorityService.save(Authority.builder().role(customerRole).resource("TRANSACTION").action("READ").build());
    }

    private Map<String, List<Account>> createAccounts(Map<String, User> users) throws Exception {
        Map<String, List<Account>> accounts = new HashMap<>();


        List<Account> amirAccounts = new ArrayList<>();
        Account amirSavings = Account.builder()
                .user(users.get("amir"))
                .accountNumber(generateAccountNumber())
                .type(AccountType.SAVINGS)
                .balance(new BigDecimal("10000000"))
                .status(AccountStatus.ACTIVE)
                .build();
        amirAccounts.add(accountService.save(amirSavings));

        Account amirChecking = Account.builder()
                .user(users.get("amir"))
                .accountNumber(generateAccountNumber())
                .type(AccountType.CHECKING)
                .balance(new BigDecimal("5000000"))
                .status(AccountStatus.ACTIVE)
                .build();
        amirAccounts.add(accountService.save(amirChecking));
        accounts.put("amir", amirAccounts);


        List<Account> saraAccounts = new ArrayList<>();
        Account saraSavings = Account.builder()
                .user(users.get("sara"))
                .accountNumber(generateAccountNumber())
                .type(AccountType.SAVINGS)
                .balance(new BigDecimal("8000000"))
                .status(AccountStatus.ACTIVE)
                .build();
        saraAccounts.add(accountService.save(saraSavings));

        Account saraChecking = Account.builder()
                .user(users.get("sara"))
                .accountNumber(generateAccountNumber())
                .type(AccountType.CHECKING)
                .balance(new BigDecimal("3000000"))
                .status(AccountStatus.ACTIVE)
                .build();
        saraAccounts.add(accountService.save(saraChecking));
        accounts.put("sara", saraAccounts);

        log.info("Created {} accounts", accounts.values().stream().mapToLong(List::size).sum());
        return accounts;
    }

    private void createCards(Map<String, List<Account>> accounts) throws Exception {
        for (List<Account> list : accounts.values()) {
            for (Account acc : list) {
                cardService.save(Card.builder()
                        .account(acc)
                        .cardNumber(generateCardNumber())
                        .cvv(generateCVV())
                        .expiryDate(LocalDate.now().plusYears(3))
                        .type(CardType.DEBIT)
                        .active(true)
                        .build());
            }
        }
        log.info("Created cards for all accounts");
    }

    private void createSampleTransactions(Map<String, List<Account>> accounts) throws Exception {
        Account amirSavings = accounts.get("amir").get(0);
        Account saraSavings = accounts.get("sara").get(0);
        Account saraChecking = accounts.get("sara").get(1);

        // واریز
        Transaction deposit = Transaction.builder()
                .transactionId(generateTransactionId())
                .toAccount(amirSavings)
                .amount(new BigDecimal("2000000"))
                .type(TransactionType.DEPOSIT)
                .transactionDate(LocalDateTime.now())
                .status(TransactionStatus.COMPLETED)
                .description("واریز اولیه")
                .referenceNumber(UUID.randomUUID().toString().substring(0, 10))
                .build();
        transactionService.save(deposit);
        amirSavings.setBalance(amirSavings.getBalance().add(new BigDecimal("2000000")));
        accountService.update(amirSavings);

        // برداشت
        Transaction withdrawal = Transaction.builder()
                .transactionId(generateTransactionId())
                .fromAccount(saraChecking)
                .amount(new BigDecimal("1000000"))
                .type(TransactionType.WITHDRAWAL)
                .transactionDate(LocalDateTime.now())
                .status(TransactionStatus.COMPLETED)
                .description("برداشت نمونه")
                .referenceNumber(UUID.randomUUID().toString().substring(0, 10))
                .build();
        transactionService.save(withdrawal);
        saraChecking.setBalance(saraChecking.getBalance().subtract(new BigDecimal("1000000")));
        accountService.update(saraChecking);

        // انتقال
        Transaction transfer = Transaction.builder()
                .transactionId(generateTransactionId())
                .fromAccount(amirSavings)
                .toAccount(saraSavings)
                .amount(new BigDecimal("1500000"))
                .type(TransactionType.TRANSFER)
                .transactionDate(LocalDateTime.now())
                .status(TransactionStatus.COMPLETED)
                .description("انتقال نمونه")
                .referenceNumber(UUID.randomUUID().toString().substring(0, 10))
                .build();
        transactionService.save(transfer);
        amirSavings.setBalance(amirSavings.getBalance().subtract(new BigDecimal("1500000")));
        saraSavings.setBalance(saraSavings.getBalance().add(new BigDecimal("1500000")));
        accountService.update(amirSavings);
        accountService.update(saraSavings);

        log.info("Created sample transactions");
    }

    private void createSampleLoans(Map<String, User> users, Map<String, List<Account>> accounts) throws Exception {
        Account amirAcc = accounts.get("amir").get(0);
        Account saraAcc = accounts.get("sara").get(0);

        loanService.save(Loan.builder()
                .account(amirAcc).user(users.get("amir"))
                .loanNumber(generateLoanNumber())
                .principal(new BigDecimal("50000000"))
                .annualInterestRate(new BigDecimal("18.00"))
                .durationMonths(24)
                .monthlyPayment(calculateMonthlyPayment(new BigDecimal("50000000"), new BigDecimal("18.00"), 24))
                .startDate(LocalDate.now())
                .status(LoanStatus.PENDING)
                .build());

        loanService.save(Loan.builder()
                .account(saraAcc).user(users.get("sara"))
                .loanNumber(generateLoanNumber())
                .principal(new BigDecimal("30000000"))
                .annualInterestRate(new BigDecimal("15.00"))
                .durationMonths(36)
                .monthlyPayment(calculateMonthlyPayment(new BigDecimal("30000000"), new BigDecimal("15.00"), 36))
                .startDate(LocalDate.now().minusMonths(1))
                .status(LoanStatus.APPROVED)
                .build());

        log.info("Created sample loans");
    }


    private String generateAccountNumber() {
        StringBuilder sb = new StringBuilder();
        sb.append(1 + random.nextInt(9));
        for (int i = 0; i < 15; i++) sb.append(random.nextInt(10));
        return sb.toString();
    }

    private String generateCardNumber() {
        StringBuilder sb = new StringBuilder("6037");
        for (int i = 0; i < 12; i++) sb.append(random.nextInt(10));
        return sb.toString();
    }

    private String generateCVV() {
        return String.valueOf(100 + random.nextInt(900));
    }

    private String generateTransactionId() {
        return "TRX" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String generateLoanNumber() {
        StringBuilder sb = new StringBuilder("LOAN-");
        for (int i = 0; i < 12; i++) sb.append(random.nextInt(10));
        return sb.toString();
    }

    private BigDecimal calculateMonthlyPayment(BigDecimal p, BigDecimal r, int n) {
        if (r.compareTo(BigDecimal.ZERO) == 0) return p.divide(new BigDecimal(n), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal monthlyRate = r.divide(new BigDecimal("1200"), 6, BigDecimal.ROUND_HALF_UP);
        double power = Math.pow(1 + monthlyRate.doubleValue(), n);
        BigDecimal num = monthlyRate.multiply(new BigDecimal(power));
        BigDecimal den = new BigDecimal(power).subtract(BigDecimal.ONE);
        return p.multiply(num).divide(den, 2, BigDecimal.ROUND_HALF_UP);
    }

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
    public void contextDestroyed(ServletContextEvent sce) {}
}
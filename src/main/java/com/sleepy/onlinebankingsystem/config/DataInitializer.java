package com.sleepy.onlinebankingsystem.config;

import com.sleepy.onlinebankingsystem.model.entity.*;
import com.sleepy.onlinebankingsystem.model.enums.*;
import com.sleepy.onlinebankingsystem.service.*;
import com.sleepy.onlinebankingsystem.utils.PasswordUtil;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * مقداردهی اولیه پایگاه داده
 * این کلاس در ابتدای اجرای برنامه اجرا می‌شود و داده‌های اولیه را ایجاد می‌کند
 */
@Slf4j
@WebListener
public class DataInitializer implements ServletContextListener {

    @Inject
    private UserService userService;

    @Inject
    private RoleService roleService;

    @Inject
    private AuthorityService authorityService;

    @Inject
    private AccountService accountService;

    @Inject
    private CardService cardService;

    @Inject
    private TransactionService transactionService;

    @Inject
    private LoanService loanService;

    @Inject
    private PasswordUtil passwordUtil;

    private final SecureRandom random = new SecureRandom();

    // رمز عبور پیش‌فرض برای همه کاربران
    private static final String DEFAULT_PASSWORD = "123456";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            log.info("========================================");
            log.info("🚀 Starting Data Initialization...");
            log.info("========================================");

            // بررسی وجود داده
            if (isDatabaseInitialized()) {
                log.info("✅ Database is already initialized. Skipping...");
                return;
            }

            // ایجاد کاربران اولیه
            Map<String, User> users = createInitialUsers();
            log.info("✅ Users created: {}", users.size());

            // ایجاد نقش‌ها
            createRolesForUsers(users);
            log.info("✅ Roles assigned");

            // ایجاد دسترسی‌ها
            createAuthorities(users);
            log.info("✅ Authorities created");

            // ایجاد حساب‌های بانکی
            Map<String, List<Account>> accounts = createAccounts(users);
            log.info("✅ Accounts created");

            // ایجاد کارت‌ها
            createCards(accounts);
            log.info("✅ Cards created");

            // ایجاد تراکنش‌های نمونه
            createSampleTransactions(accounts);
            log.info("✅ Sample transactions created");

            // ایجاد وام‌های نمونه
            createSampleLoans(users, accounts);
            log.info("✅ Sample loans created");

            log.info("========================================");
            log.info("✅ Data Initialization Completed Successfully!");
            log.info("========================================");
            printLoginCredentials();

        } catch (Exception e) {
            log.error("❌ Error during data initialization", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.info("🛑 Application context destroyed");
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
        String hashedPassword = passwordUtil.hash(DEFAULT_PASSWORD);

        User admin = User.builder()
                .username("admin")
                .password(hashedPassword)
                .firstName("مدیر")
                .lastName("سیستم")
                .phone("09121111111")
                .nationalCode("0011223344")
                .active(true)
                .build();
        users.put("admin", userService.save(admin));

        User manager = User.builder()
                .username("manager")
                .password(hashedPassword)
                .firstName("رضا")
                .lastName("مدیری")
                .phone("09122222222")
                .nationalCode("1122334455")
                .active(true)
                .build();
        users.put("manager", userService.save(manager));

        User customer1 = User.builder()
                .username("amir")
                .password(hashedPassword)
                .firstName("امیر")
                .lastName("حسینی")
                .phone("09123333333")
                .nationalCode("2233445566")
                .active(true)
                .build();
        users.put("amir", userService.save(customer1));

        User customer2 = User.builder()
                .username("sara")
                .password(hashedPassword)
                .firstName("سارا")
                .lastName("احمدی")
                .phone("09124444444")
                .nationalCode("3344556677")
                .active(true)
                .build();
        users.put("sara", userService.save(customer2));

        User customer3 = User.builder()
                .username("ali")
                .password(hashedPassword)
                .firstName("علی")
                .lastName("محمدی")
                .phone("09125555555")
                .nationalCode("4455667788")
                .active(true)
                .build();
        users.put("ali", userService.save(customer3));

        User inactiveCustomer = User.builder()
                .username("inactive")
                .password(hashedPassword)
                .firstName("کاربر")
                .lastName("غیرفعال")
                .phone("09126666666")
                .nationalCode("5566778899")
                .active(false)
                .build();
        users.put("inactive", userService.save(inactiveCustomer));

        return users;
    }

    private void createRolesForUsers(Map<String, User> users) throws Exception {
        Role adminRole = Role.builder()
                .user(users.get("admin"))
                .role(UserRole.ADMIN)
                .build();
        roleService.save(adminRole);

        Role managerRole = Role.builder()
                .user(users.get("manager"))
                .role(UserRole.MANAGER)
                .build();
        roleService.save(managerRole);

        for (String username : Arrays.asList("amir", "sara", "ali", "inactive")) {
            Role customerRole = Role.builder()
                    .user(users.get(username))
                    .role(UserRole.CUSTOMER)
                    .build();
            roleService.save(customerRole);
        }
    }

    private void createAuthorities(Map<String, User> users) throws Exception {
        User admin = users.get("admin");
        User manager = users.get("manager");

        List<Role> adminRoles = roleService.findByUser(admin);
        List<Role> managerRoles = roleService.findByUser(manager);

        Role adminRole = adminRoles.get(0);
        Role managerRole = managerRoles.get(0);

        String[] resources = {"USER", "ACCOUNT", "TRANSACTION", "LOAN", "CARD", "REPORT"};
        String[] actions = {"CREATE", "READ", "UPDATE", "DELETE", "APPROVE"};

        for (String resource : resources) {
            for (String action : actions) {
                Authority authority = Authority.builder()
                        .role(adminRole)
                        .resource(resource)
                        .action(action)
                        .build();
                authorityService.save(authority);
            }
        }

        String[] managerResources = {"ACCOUNT", "TRANSACTION", "LOAN", "CARD"};
        String[] managerActions = {"READ", "UPDATE", "APPROVE"};

        for (String resource : managerResources) {
            for (String action : managerActions) {
                Authority authority = Authority.builder()
                        .role(managerRole)
                        .resource(resource)
                        .action(action)
                        .build();
                authorityService.save(authority);
            }
        }
    }

    private Map<String, List<Account>> createAccounts(Map<String, User> users) throws Exception {
        Map<String, List<Account>> userAccounts = new HashMap<>();

        List<Account> adminAccounts = new ArrayList<>();
        adminAccounts.add(createAccount(users.get("admin"), AccountType.CHECKING,
                new BigDecimal("50000000"), AccountStatus.ACTIVE));
        userAccounts.put("admin", adminAccounts);

        List<Account> managerAccounts = new ArrayList<>();
        managerAccounts.add(createAccount(users.get("manager"), AccountType.CHECKING,
                new BigDecimal("30000000"), AccountStatus.ACTIVE));
        userAccounts.put("manager", managerAccounts);

        List<Account> amirAccounts = new ArrayList<>();
        amirAccounts.add(createAccount(users.get("amir"), AccountType.CHECKING,
                new BigDecimal("10000000"), AccountStatus.ACTIVE));
        amirAccounts.add(createAccount(users.get("amir"), AccountType.SAVINGS,
                new BigDecimal("25000000"), AccountStatus.ACTIVE));
        userAccounts.put("amir", amirAccounts);

        List<Account> saraAccounts = new ArrayList<>();
        saraAccounts.add(createAccount(users.get("sara"), AccountType.CHECKING,
                new BigDecimal("15000000"), AccountStatus.ACTIVE));
        saraAccounts.add(createAccount(users.get("sara"), AccountType.SAVINGS,
                new BigDecimal("20000000"), AccountStatus.ACTIVE));
        userAccounts.put("sara", saraAccounts);

        List<Account> aliAccounts = new ArrayList<>();
        aliAccounts.add(createAccount(users.get("ali"), AccountType.CHECKING,
                new BigDecimal("5000000"), AccountStatus.ACTIVE));
        aliAccounts.add(createAccount(users.get("ali"), AccountType.SAVINGS,
                new BigDecimal("8000000"), AccountStatus.FROZEN));
        userAccounts.put("ali", aliAccounts);

        List<Account> inactiveAccounts = new ArrayList<>();
        inactiveAccounts.add(createAccount(users.get("inactive"), AccountType.CHECKING,
                new BigDecimal("1000000"), AccountStatus.CLOSED));
        userAccounts.put("inactive", inactiveAccounts);

        return userAccounts;
    }

    private Account createAccount(User user, AccountType type, BigDecimal balance,
                                  AccountStatus status) throws Exception {
        Account account = Account.builder()
                .user(user)
                .accountNumber(generateAccountNumber())
                .type(type)
                .balance(balance)
                .status(status)
                .build();
        return accountService.save(account);
    }

    private void createCards(Map<String, List<Account>> userAccounts) throws Exception {
        for (Map.Entry<String, List<Account>> entry : userAccounts.entrySet()) {
            String username = entry.getKey();

            if (username.equals("inactive")) continue;

            for (Account account : entry.getValue()) {
                if (account.getStatus() == AccountStatus.ACTIVE) {
                    Card debitCard = Card.builder()
                            .account(account)
                            .cardNumber(generateCardNumber())
                            .cvv(generateCVV())
                            .expiryDate(LocalDate.now().plusYears(3))
                            .type(CardType.DEBIT)
                            .active(true)
                            .build();
                    cardService.save(debitCard);

                    if (entry.getValue().indexOf(account) == 0) {
                        Card creditCard = Card.builder()
                                .account(account)
                                .cardNumber(generateCardNumber())
                                .cvv(generateCVV())
                                .expiryDate(LocalDate.now().plusYears(2))
                                .type(CardType.CREDIT)
                                .active(true)
                                .build();
                        cardService.save(creditCard);
                    }
                }
            }
        }
    }

    private void createSampleTransactions(Map<String, List<Account>> userAccounts) throws Exception {
        List<Account> amirAccounts = userAccounts.get("amir");
        List<Account> saraAccounts = userAccounts.get("sara");

        if (amirAccounts.isEmpty() || saraAccounts.isEmpty()) return;

        Account amirChecking = amirAccounts.get(0);
        Account saraChecking = saraAccounts.get(0);

        Transaction deposit = Transaction.builder()
                .transactionId(generateTransactionId())
                .toAccount(amirChecking)
                .amount(new BigDecimal("2000000"))
                .type(TransactionType.DEPOSIT)
                .transactionDate(LocalDateTime.now().minusDays(5))
                .status(TransactionStatus.COMPLETED)
                .description("واریز وجه نقد")
                .referenceNumber(UUID.randomUUID().toString().substring(0, 10))
                .build();
        transactionService.save(deposit);

        Transaction withdrawal = Transaction.builder()
                .transactionId(generateTransactionId())
                .fromAccount(saraChecking)
                .amount(new BigDecimal("1000000"))
                .type(TransactionType.WITHDRAWAL)
                .transactionDate(LocalDateTime.now().minusDays(3))
                .status(TransactionStatus.COMPLETED)
                .description("برداشت وجه نقد")
                .referenceNumber(UUID.randomUUID().toString().substring(0, 10))
                .build();
        transactionService.save(withdrawal);

        Transaction transfer = Transaction.builder()
                .transactionId(generateTransactionId())
                .fromAccount(amirChecking)
                .toAccount(saraChecking)
                .amount(new BigDecimal("500000"))
                .type(TransactionType.TRANSFER)
                .transactionDate(LocalDateTime.now().minusDays(1))
                .status(TransactionStatus.COMPLETED)
                .description("انتقال وجه")
                .referenceNumber(UUID.randomUUID().toString().substring(0, 10))
                .build();
        transactionService.save(transfer);
    }

    private void createSampleLoans(Map<String, User> users,
                                   Map<String, List<Account>> userAccounts) throws Exception {
        List<Account> amirAccounts = userAccounts.get("amir");
        List<Account> saraAccounts = userAccounts.get("sara");

        if (amirAccounts.isEmpty() || saraAccounts.isEmpty()) return;

        Loan pendingLoan = Loan.builder()
                .account(amirAccounts.get(0))
                .user(users.get("amir"))
                .loanNumber(generateLoanNumber())
                .principal(new BigDecimal("50000000"))
                .annualInterestRate(new BigDecimal("18.00"))
                .durationMonths(24)
                .monthlyPayment(calculateMonthlyPayment(new BigDecimal("50000000"),
                        new BigDecimal("18.00"), 24))
                .startDate(LocalDate.now())
                .status(LoanStatus.PENDING)
                .build();
        loanService.save(pendingLoan);

        Loan approvedLoan = Loan.builder()
                .account(saraAccounts.get(0))
                .user(users.get("sara"))
                .loanNumber(generateLoanNumber())
                .principal(new BigDecimal("30000000"))
                .annualInterestRate(new BigDecimal("15.00"))
                .durationMonths(36)
                .monthlyPayment(calculateMonthlyPayment(new BigDecimal("30000000"),
                        new BigDecimal("15.00"), 36))
                .startDate(LocalDate.now().minusMonths(1))
                .status(LoanStatus.APPROVED)
                .build();
        loanService.save(approvedLoan);

        Loan activeLoan = Loan.builder()
                .account(saraAccounts.get(1))
                .user(users.get("sara"))
                .loanNumber(generateLoanNumber())
                .principal(new BigDecimal("20000000"))
                .annualInterestRate(new BigDecimal("12.00"))
                .durationMonths(12)
                .monthlyPayment(calculateMonthlyPayment(new BigDecimal("20000000"),
                        new BigDecimal("12.00"), 12))
                .startDate(LocalDate.now().minusMonths(3))
                .status(LoanStatus.ACTIVE)
                .build();
        loanService.save(activeLoan);
    }

    private String generateAccountNumber() {
        StringBuilder sb = new StringBuilder(16);
        sb.append(random.nextInt(9) + 1);
        for (int i = 0; i < 15; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private String generateCardNumber() {
        StringBuilder sb = new StringBuilder(16);
        sb.append("6037");
        for (int i = 0; i < 12; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private String generateCVV() {
        int cvv = 100 + random.nextInt(900);
        return String.valueOf(cvv);
    }

    private String generateTransactionId() {
        return "TRX" + System.currentTimeMillis() + "-" +
                UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String generateLoanNumber() {
        StringBuilder sb = new StringBuilder("LOAN-");
        for (int i = 0; i < 12; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private BigDecimal calculateMonthlyPayment(BigDecimal principal,
                                               BigDecimal annualRate, Integer months) {
        if (annualRate.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(new BigDecimal(months), 2,
                    BigDecimal.ROUND_HALF_UP);
        }

        BigDecimal monthlyRate = annualRate
                .divide(new BigDecimal("12"), 6, BigDecimal.ROUND_HALF_UP)
                .divide(new BigDecimal("100"), 6, BigDecimal.ROUND_HALF_UP);

        double onePlusR = 1 + monthlyRate.doubleValue();
        double power = Math.pow(onePlusR, months);

        BigDecimal numerator = monthlyRate.multiply(new BigDecimal(power));
        BigDecimal denominator = new BigDecimal(power).subtract(BigDecimal.ONE);

        return principal.multiply(numerator).divide(denominator, 2,
                BigDecimal.ROUND_HALF_UP);
    }

    private void printLoginCredentials() {
        log.info("========================================");
        log.info("🔐 Login Credentials (Password: {})", DEFAULT_PASSWORD);
        log.info("========================================");
        log.info("👤 Admin    : username = admin");
        log.info("👤 Manager  : username = manager");
        log.info("👤 Customer1: username = amir");
        log.info("👤 Customer2: username = sara");
        log.info("👤 Customer3: username = ali");
        log.info("========================================");
    }
}
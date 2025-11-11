package com.sleepy.onlinebankingsystem.config;


import com.sleepy.onlinebankingsystem.service.AccountService;

import com.sleepy.onlinebankingsystem.service.RoleService;
import com.sleepy.onlinebankingsystem.service.UserService;
import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.entity.Role;
import com.sleepy.onlinebankingsystem.model.entity.User;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.sleepy.onlinebankingsystem.model.entity.*;
import com.sleepy.onlinebankingsystem.model.enums.*;
import com.sleepy.onlinebankingsystem.service.*;
import java.math.BigDecimal;


/**
 * Application data initializer adapted to this project (no Customer entity).
 *
 * - Runs once at application startup in a Jakarta EE container (@Singleton + @Startup).
 * - Creates sample users, roles and a couple of accounts/cards/loan entries if they don't already exist.
 *
 * Notes you must review and adapt:
 * - Passwords are stored as pre-hashed 60-char placeholders so they satisfy the User entity validation.
 *   Replace these placeholders with real BCrypt hashes (or integrate your password hashing) before using in any real environment.
 * - Service method signatures (findByUsername, save, etc.) are called according to typical service contracts in this repo.
 *   If your concrete service APIs differ, adapt the calls accordingly.
 * - If you don't run inside an EJB container, replace @Singleton/@Startup with an appropriate startup mechanism.
 */
@Slf4j
@Singleton
@Startup
public class DataInitializer {

    // NOTE: These are example 60-char placeholders to satisfy the User entity validation (@Size(min=60,max=60)).
    // Replace with real BCrypt hashes in your environment.
    private static final String ADMIN_PASSWORD = "admin123";      // 8 chars
    private static final String TELLER_PASSWORD = "teller12";    // 9 chars
    private static final String USER_PASSWORD = "pass123";

    @Inject
    private UserService userService;

    @Inject
    private RoleService roleService;

    @Inject
    private AccountService accountService;

    @Inject
    private CardService cardService;

    @Inject
    private LoanService loanService;

    @Inject
    private AuthorityService authorityService;

    @PostConstruct
    public void init() {
        log.info("========================================");
        log.info("  Starting Data Initialization...");
        log.info("========================================");

        try {
            // if admin user exists, assume initial data already created
            Optional<User> existingAdmin = userService.findByUsername("admin");
            if (existingAdmin.isPresent()) {
                log.info(" Initial data already exists. Skipping initialization.");
                return;
            }

            // Users
            User admin = createUserIfNotExists("admin", ADMIN_PASSWORD, "Admin", "System", "09100000000", "0000000000", true);
            createRoleIfNotExists(admin, "ADMIN");

            User teller = createUserIfNotExists("teller", TELLER_PASSWORD, "Teller", "Branch", "09100000001", "0000000001", true);
            createRoleIfNotExists(teller, "TELLER");

            User jdoe = createUserIfNotExists("jdoe", USER_PASSWORD, "John", "Doe", "09123456789", "1234567890", true);
            createRoleIfNotExists(jdoe, "CUSTOMER");

            // Accounts for jdoe
            Account chk = createAccountIfNotExists("4000000000001001", AccountType.CHECKING, BigDecimal.valueOf(1000.00), jdoe);
            Account sav = createAccountIfNotExists("4000000000002001", AccountType.SAVINGS, BigDecimal.valueOf(5000.00), jdoe);

            // Card for checking account
            createCardIfNotExists(chk, "5212345678901234", "123", LocalDate.now().plusYears(3), CardType.DEBIT);

            // Sample loan attached to savings account (demo)
            createLoanIfNotExists(sav, "LN-1001", BigDecimal.valueOf(10_000.00), BigDecimal.valueOf(18.00), 24, jdoe);

            // Example authority for ADMIN role (resource/action)
            createAuthorityIfNotExists("ADMIN", "USER", "CRUD");

            log.info("========================================");
            log.info(" Data Initialization Completed Successfully!");
            log.info("========================================");
            log.info("Demo Accounts / Users:");
            log.info("  - Admin:   username=admin    password=admin123 (replace placeholder hash with real hash)");
            log.info("  - Teller:  username=teller   password=teller123 (replace placeholder hash with real hash)");
            log.info("  - Customer username=jdoe     password=password123 (replace placeholder hash with real hash)");
            log.info("========================================");

        } catch (Exception e) {
            log.error(" Error during data initialization: {}", e.getMessage(), e);
        }
    }

    private User createUserIfNotExists(String username, String pwdHash,
                                       String firstName, String lastName,
                                       String phone, String nationalCode,
                                       boolean active) throws Exception {

        Optional<User> userOpt = userService.findByUsername(username);

        if (userOpt.isPresent()) {
            log.info("    User '{}' already exists", username);
            return userOpt.get();
        }

        User user = User.builder()
                .username(username)
                .password(pwdHash) // pre-hashed placeholder - replace with real hash
                .firstName(firstName)
                .lastName(lastName)
                .phone(phone)
                .nationalCode(nationalCode)
                .active(active)
                .build();

        userService.save(user);
        log.info("    Created user: {}", username);
        return user;
    }

    private void createRoleIfNotExists(User user, String roleName) throws Exception {
        // The Role entity in this project has a named query "Role.findByUsernameAndRoleName" used elsewhere;
        // adapt this call if your RoleService API differs.
        Optional<Role> existing = roleService.findByUsernameAndRoleName(user.getUsername(), roleName);
        if (existing != null && !existing.isEmpty()) {
            log.info("    Role '{}' already exists for user '{}'", roleName, user.getUsername());
            return;
        }

        Role role = Role.builder()
                .user(user)
                .role(roleName)
                .build();

        roleService.save(role);
        log.info("    Created role '{}' for user '{}'", roleName, user.getUsername());
    }

    private Account createAccountIfNotExists(String accountNumber, AccountType type, BigDecimal balance, User owner) throws Exception {
        Optional<Account> existing = accountService.findByAccountNumber(accountNumber);
        if (existing.isPresent()) {
            log.info("    Account '{}' already exists", accountNumber);
            return existing.get();
        }

        Account account = Account.builder()
                .user(owner)
                .accountNumber(accountNumber)
                .type(type)
                .balance(balance)
                .status(AccountStatus.ACTIVE)
                .build();

        accountService.save(account);
        log.info("    Created account: {}", accountNumber);
        return account;
    }

    private Card createCardIfNotExists(Account account, String cardNumber, String cvv, LocalDate expiry, CardType type) throws Exception {
        Optional<Card> found = cardService.findByCardNumber(cardNumber);
        if (found != null && !found.isEmpty()) {
            log.info("    Card '{}' already exists", cardNumber);
            return found.get();
        }

        Card card = Card.builder()
                .account(account)
                .cardNumber(cardNumber)
                .cvv(cvv)
                .expiryDate(expiry)
                .type(type)
                .active(true)
                .build();

        cardService.save(card);
        log.info("    Created card: {}", cardNumber);
        return card;
    }

    private Loan createLoanIfNotExists(Account account, String loanNumber, BigDecimal principal,
                                       BigDecimal annualInterestRate, int durationMonths, User user) throws Exception {
        Optional<Loan> existing = loanService.findByLoanNumber(loanNumber);
        if (existing.isPresent()) {
            log.info("    Loan '{}' already exists", loanNumber);
            return existing.get();
        }

        Loan loan = Loan.builder()
                .account(account)
                .loanNumber(loanNumber)
                .principal(principal)
                .annualInterestRate(annualInterestRate)
                .durationMonths(durationMonths)
                .monthlyPayment(null)
                .startDate(LocalDate.now())
                .status(LoanStatus.PENDING)
                .user(user)
                .build();

        loanService.save(loan);
        log.info("    Created loan: {}", loanNumber);
        return loan;
    }

    private Authority createAuthorityIfNotExists(String roleName, String resource, String action) throws Exception {
        // Find role by name first
        List<Role> roles = roleService.findByRoleName(roleName);
        Role role;
        if (roles != null && !roles.isEmpty()) {
            role = roles.get(0);
        } else {
            // create a role holder without an associated user (some projects allow roles without users)
            role = Role.builder().role(roleName).build();
            roleService.save(role);
            log.info("    Created standalone role '{}'", roleName);
        }

        List<Authority> existing = authorityService.findByResourceAndAction(resource, action);
        if (existing != null && !existing.isEmpty()) {
            for (Authority a : existing) {
                if (a.getRole() != null && a.getRole().getId().equals(role.getId())) {
                    log.info("    Authority already exists for role='{}' resource='{}' action='{}'", roleName, resource, action);
                    return a;
                }
            }
        }

        Authority auth = Authority.builder()
                .role(role)
                .resource(resource)
                .action(action)
                .build();

        authorityService.save(auth);
        log.info("    Created authority: role='{}' resource='{}' action='{}'", roleName, resource, action);
        return auth;
    }
}
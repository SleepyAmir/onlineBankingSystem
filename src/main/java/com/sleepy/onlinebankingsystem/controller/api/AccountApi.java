package com.sleepy.onlinebankingsystem.controller.api;

import com.sleepy.onlinebankingsystem.model.dto.request.CreateAccountRequest;
import com.sleepy.onlinebankingsystem.model.dto.response.AccountResponse;
import com.sleepy.onlinebankingsystem.model.dto.response.ApiResponse;
import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.model.enums.AccountStatus;
import com.sleepy.onlinebankingsystem.model.enums.AccountType;
import com.sleepy.onlinebankingsystem.service.AccountService;
import com.sleepy.onlinebankingsystem.service.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/accounts")
@Slf4j
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AccountApi {

    @Inject
    private AccountService accountService;

    @Inject
    private UserService userService;

    /**
     * ایجاد حساب جدید
     * POST /api/accounts
     */
    @POST
    public Response createAccount(CreateAccountRequest request) {
        try {
            log.info("Creating account for user: {}", request.getUserId());

            // پیدا کردن کاربر
            Optional<User> userOpt = userService.findById(request.getUserId());
            if (userOpt.isEmpty()) {
                return Response.status(404)
                        .entity(ApiResponse.error("کاربر یافت نشد"))
                        .build();
            }

            // اعتبارسنجی موجودی اولیه
            if (request.getInitialBalance() != null &&
                    request.getInitialBalance().compareTo(BigDecimal.ZERO) < 0) {
                return Response.status(400)
                        .entity(ApiResponse.error("موجودی اولیه نمی‌تواند منفی باشد"))
                        .build();
            }

            // ساخت حساب
            Account account = Account.builder()
                    .user(userOpt.get())
                    .accountNumber(generateAccountNumber())
                    .type(request.getType())
                    .balance(request.getInitialBalance() != null ?
                            request.getInitialBalance() : BigDecimal.ZERO)
                    .status(AccountStatus.ACTIVE)
                    .build();

            Account savedAccount = accountService.save(account);
            log.info("Account created successfully: {}", savedAccount.getAccountNumber());

            AccountResponse response = AccountResponse.builder()
                    .accountNumber(savedAccount.getAccountNumber())
                    .type(savedAccount.getType())
                    .balance(savedAccount.getBalance())
                    .status(savedAccount.getStatus())
                    .build();

            return Response.status(201).entity(ApiResponse.success(response)).build();

        } catch (Exception e) {
            log.error("Error creating account", e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در ایجاد حساب: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * دریافت همه حساب‌ها
     * GET /api/accounts
     */
    @GET
    public Response getAllAccounts(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size) {
        try {
            List<Account> accounts = accountService.findAll(page, size);
            log.info("Retrieved {} accounts", accounts.size());

            List<AccountResponse> responses = accounts.stream()
                    .map(acc -> AccountResponse.builder()
                            .accountNumber(acc.getAccountNumber())
                            .type(acc.getType())
                            .balance(acc.getBalance())
                            .status(acc.getStatus())
                            .build())
                    .collect(Collectors.toList());

            return Response.ok().entity(ApiResponse.success(responses)).build();

        } catch (Exception e) {
            log.error("Error fetching accounts", e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در دریافت حساب‌ها: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * دریافت حساب با ID
     * GET /api/accounts/{id}
     */
    @GET
    @Path("/{id}")
    public Response getAccountById(@PathParam("id") Long id) {
        try {
            Optional<Account> accountOpt = accountService.findById(id);

            if (accountOpt.isEmpty()) {
                return Response.status(404)
                        .entity(ApiResponse.error("حساب یافت نشد"))
                        .build();
            }

            Account acc = accountOpt.get();
            AccountResponse response = AccountResponse.builder()
                    .accountNumber(acc.getAccountNumber())
                    .type(acc.getType())
                    .balance(acc.getBalance())
                    .status(acc.getStatus())
                    .build();

            return Response.ok().entity(ApiResponse.success(response)).build();

        } catch (Exception e) {
            log.error("Error fetching account by id: {}", id, e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در دریافت حساب: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * دریافت حساب با شماره حساب
     * GET /api/accounts/number/{accountNumber}
     */
    @GET
    @Path("/number/{accountNumber}")
    public Response getAccountByNumber(@PathParam("accountNumber") String accountNumber) {
        try {
            Optional<Account> accountOpt = accountService.findByAccountNumber(accountNumber);

            if (accountOpt.isEmpty()) {
                return Response.status(404)
                        .entity(ApiResponse.error("حساب یافت نشد"))
                        .build();
            }

            Account acc = accountOpt.get();
            AccountResponse response = AccountResponse.builder()
                    .accountNumber(acc.getAccountNumber())
                    .type(acc.getType())
                    .balance(acc.getBalance())
                    .status(acc.getStatus())
                    .build();

            return Response.ok().entity(ApiResponse.success(response)).build();

        } catch (Exception e) {
            log.error("Error fetching account by number: {}", accountNumber, e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در دریافت حساب: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * دریافت حساب‌های یک کاربر
     * GET /api/accounts/user/{userId}
     */
    @GET
    @Path("/user/{userId}")
    public Response getAccountsByUserId(@PathParam("userId") Long userId) {
        try {
            Optional<User> userOpt = userService.findById(userId);

            if (userOpt.isEmpty()) {
                return Response.status(404)
                        .entity(ApiResponse.error("کاربر یافت نشد"))
                        .build();
            }

            List<Account> accounts = accountService.findByUser(userOpt.get());

            List<AccountResponse> responses = accounts.stream()
                    .map(acc -> AccountResponse.builder()
                            .accountNumber(acc.getAccountNumber())
                            .type(acc.getType())
                            .balance(acc.getBalance())
                            .status(acc.getStatus())
                            .build())
                    .collect(Collectors.toList());

            return Response.ok().entity(ApiResponse.success(responses)).build();

        } catch (Exception e) {
            log.error("Error fetching accounts for user: {}", userId, e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در دریافت حساب‌ها: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * به‌روزرسانی حساب
     * PUT /api/accounts/{id}
     */
    @PUT
    @Path("/{id}")
    public Response updateAccount(
            @PathParam("id") Long id,
            AccountUpdateRequest request) {
        try {
            Optional<Account> accountOpt = accountService.findById(id);

            if (accountOpt.isEmpty()) {
                return Response.status(404)
                        .entity(ApiResponse.error("حساب یافت نشد"))
                        .build();
            }

            Account account = accountOpt.get();

            if (request.getType() != null) {
                account.setType(request.getType());
            }
            if (request.getStatus() != null) {
                account.setStatus(request.getStatus());
            }
            if (request.getBalance() != null) {
                if (request.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                    return Response.status(400)
                            .entity(ApiResponse.error("موجودی نمی‌تواند منفی باشد"))
                            .build();
                }
                account.setBalance(request.getBalance());
            }

            Account updatedAccount = accountService.update(account);
            log.info("Account updated successfully: {}", account.getAccountNumber());

            AccountResponse response = AccountResponse.builder()
                    .accountNumber(updatedAccount.getAccountNumber())
                    .type(updatedAccount.getType())
                    .balance(updatedAccount.getBalance())
                    .status(updatedAccount.getStatus())
                    .build();

            return Response.ok().entity(ApiResponse.success(response)).build();

        } catch (Exception e) {
            log.error("Error updating account: {}", id, e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در به‌روزرسانی حساب: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * تغییر وضعیت حساب
     * PATCH /api/accounts/{id}/status
     */
    @PATCH
    @Path("/{id}/status")
    public Response changeAccountStatus(
            @PathParam("id") Long id,
            @QueryParam("status") String status) {
        try {
            Optional<Account> accountOpt = accountService.findById(id);

            if (accountOpt.isEmpty()) {
                return Response.status(404)
                        .entity(ApiResponse.error("حساب یافت نشد"))
                        .build();
            }

            AccountStatus newStatus = AccountStatus.valueOf(status);
            Account account = accountOpt.get();
            account.setStatus(newStatus);

            Account updatedAccount = accountService.update(account);
            log.info("Account status changed: {} to {}",
                    account.getAccountNumber(), newStatus);

            AccountResponse response = AccountResponse.builder()
                    .accountNumber(updatedAccount.getAccountNumber())
                    .type(updatedAccount.getType())
                    .balance(updatedAccount.getBalance())
                    .status(updatedAccount.getStatus())
                    .build();

            return Response.ok().entity(ApiResponse.success(response)).build();

        } catch (IllegalArgumentException e) {
            return Response.status(400)
                    .entity(ApiResponse.error("وضعیت نامعتبر است"))
                    .build();
        } catch (Exception e) {
            log.error("Error changing account status: {}", id, e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در تغییر وضعیت: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * حذف نرم حساب
     * DELETE /api/accounts/{id}
     */
    @DELETE
    @Path("/{id}")
    public Response deleteAccount(@PathParam("id") Long id) {
        try {
            Optional<Account> accountOpt = accountService.findById(id);

            if (accountOpt.isEmpty()) {
                return Response.status(404)
                        .entity(ApiResponse.error("حساب یافت نشد"))
                        .build();
            }

            Account account = accountOpt.get();

            // بررسی موجودی
            if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
                return Response.status(400)
                        .entity(ApiResponse.error("حساب با موجودی مثبت قابل حذف نیست"))
                        .build();
            }

            accountService.softDelete(id);
            log.info("Account soft-deleted: {}", account.getAccountNumber());

            return Response.ok()
                    .entity(ApiResponse.success("حساب با موفقیت حذف شد"))
                    .build();

        } catch (Exception e) {
            log.error("Error deleting account: {}", id, e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در حذف حساب: " + e.getMessage()))
                    .build();
        }
    }

    // ==================== Helper Methods ====================

    private String generateAccountNumber() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(16);

        sb.append(random.nextInt(9) + 1);
        for (int i = 0; i < 15; i++) {
            sb.append(random.nextInt(10));
        }

        return sb.toString();
    }

    // ==================== Request/Response DTOs ====================

    public static class AccountCreateRequest {
        private Long userId;
        private AccountType type;
        private BigDecimal initialBalance;

        // Getters & Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public AccountType getType() { return type; }
        public void setType(AccountType type) { this.type = type; }
        public BigDecimal getInitialBalance() { return initialBalance; }
        public void setInitialBalance(BigDecimal initialBalance) {
            this.initialBalance = initialBalance;
        }
    }

    public static class AccountUpdateRequest {
        private AccountType type;
        private AccountStatus status;
        private BigDecimal balance;

        // Getters & Setters
        public AccountType getType() { return type; }
        public void setType(AccountType type) { this.type = type; }
        public AccountStatus getStatus() { return status; }
        public void setStatus(AccountStatus status) { this.status = status; }
        public BigDecimal getBalance() { return balance; }
        public void setBalance(BigDecimal balance) { this.balance = balance; }
    }
}
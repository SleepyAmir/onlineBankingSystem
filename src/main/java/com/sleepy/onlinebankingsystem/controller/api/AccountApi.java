package com.sleepy.onlinebankingsystem.controller.api;

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
    public Response createAccount(AccountCreateRequest request) {
        try {
            log.info("API: Creating account for user: {}", request.getUserId());

            // بررسی ورودی اولیه
            if (request.getUserId() == null) {
                return Response.status(400)
                        .entity(ApiResponse.error("شناسه کاربر الزامی است"))
                        .build();
            }
            if (request.getType() == null) {
                return Response.status(400)
                        .entity(ApiResponse.error("نوع حساب الزامی است"))
                        .build();
            }

            // فراخوانی Service
            Account account = accountService.createAccount(
                    request.getUserId(),
                    request.getType(),
                    request.getInitialBalance()
            );

            // تبدیل به Response
            AccountResponse response = mapToResponse(account);

            return Response.status(201)
                    .entity(ApiResponse.success(response))
                    .build();

        } catch (IllegalArgumentException e) {
            log.warn("Validation error in account creation: {}", e.getMessage());
            return Response.status(400)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (IllegalStateException e) {
            log.warn("Business logic error in account creation: {}", e.getMessage());
            return Response.status(400)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Error creating account", e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در ایجاد حساب: " + e.getMessage()))
                    .build();
        }
    }

    // ==================== Query ====================

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
            List<AccountResponse> responses = accounts.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());

            return Response.ok()
                    .entity(responses)
                    .build();

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

            AccountResponse response = mapToResponse(accountOpt.get());

            return Response.ok()
                    .entity(response)
                    .build();

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

            AccountResponse response = mapToResponse(accountOpt.get());

            return Response.ok()
                    .entity(response)
                    .build();

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
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());

            return Response.ok()
                    .entity(responses)
                    .build();

        } catch (Exception e) {
            log.error("Error fetching accounts for user: {}", userId, e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در دریافت حساب‌ها: " + e.getMessage()))
                    .build();
        }
    }

    // ==================== به‌روزرسانی ====================

    /**
     * به‌روزرسانی حساب
     * PUT /api/accounts/{id}
     */
    @PUT
    @Path("/{id}")
    public Response updateAccount(@PathParam("id") Long id, AccountUpdateRequest request) {
        try {
            // پیدا کردن حساب
            Optional<Account> accountOpt = accountService.findById(id);
            if (accountOpt.isEmpty()) {
                return Response.status(404)
                        .entity(ApiResponse.error("حساب یافت نشد"))
                        .build();
            }

            Account account = accountOpt.get();

            // به‌روزرسانی فیلدها
            if (request.getType() != null) {
                account.setType(request.getType());
            }
            if (request.getBalance() != null) {
                if (request.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                    return Response.status(400)
                            .entity(ApiResponse.error("موجودی نمی‌تواند منفی باشد"))
                            .build();
                }
                account.setBalance(request.getBalance());
            }

            // ذخیره
            Account updatedAccount = accountService.update(account);

            AccountResponse response = mapToResponse(updatedAccount);

            return Response.ok()
                    .entity(ApiResponse.success(response))
                    .build();

        } catch (IllegalArgumentException e) {
            log.warn("Validation error in account update: {}", e.getMessage());
            return Response.status(400)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Error updating account: {}", id, e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در به‌روزرسانی: " + e.getMessage()))
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
            log.info("API: Changing account status for ID: {} to {}", id, status);

            // اعتبارسنجی ورودی
            if (status == null || status.isBlank()) {
                return Response.status(400)
                        .entity(ApiResponse.error("وضعیت جدید الزامی است"))
                        .build();
            }

            AccountStatus newStatus;
            try {
                newStatus = AccountStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
                return Response.status(400)
                        .entity(ApiResponse.error("وضعیت نامعتبر است"))
                        .build();
            }

            // فراخوانی Service
            Account account = accountService.changeAccountStatus(id, newStatus);

            AccountResponse response = mapToResponse(account);

            return Response.ok()
                    .entity(ApiResponse.success(response))
                    .build();

        } catch (IllegalArgumentException e) {
            log.warn("Validation error in status change: {}", e.getMessage());
            return Response.status(404)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (IllegalStateException e) {
            log.warn("Business error in status change: {}", e.getMessage());
            return Response.status(400)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Error changing account status: {}", id, e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در تغییر وضعیت: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * فریز کردن حساب
     * POST /api/accounts/{id}/freeze
     */
    @POST
    @Path("/{id}/freeze")
    public Response freezeAccount(@PathParam("id") Long id) {
        try {
            log.info("API: Freezing account ID: {}", id);

            // فراخوانی Service
            Account account = accountService.freezeAccount(id);

            AccountResponse response = mapToResponse(account);

            return Response.ok()
                    .entity(ApiResponse.success(response))
                    .build();

        } catch (IllegalArgumentException e) {
            log.warn("Validation error in freeze: {}", e.getMessage());
            return Response.status(404)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (IllegalStateException e) {
            log.warn("Business error in freeze: {}", e.getMessage());
            return Response.status(400)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Error freezing account: {}", id, e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در فریز حساب: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * بستن حساب
     * POST /api/accounts/{id}/close
     */
    @POST
    @Path("/{id}/close")
    public Response closeAccount(@PathParam("id") Long id) {
        try {
            log.info("API: Closing account ID: {}", id);

            // فراخوانی Service
            Account account = accountService.closeAccount(id);

            AccountResponse response = mapToResponse(account);

            return Response.ok()
                    .entity(ApiResponse.success(response))
                    .build();

        } catch (IllegalArgumentException e) {
            log.warn("Validation error in close: {}", e.getMessage());
            return Response.status(404)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (IllegalStateException e) {
            log.warn("Business error in close: {}", e.getMessage());
            return Response.status(400)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Error closing account: {}", id, e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در بستن حساب: " + e.getMessage()))
                    .build();
        }
    }

    // ==================== حذف ====================

    /**
     * حذف نرم حساب
     * DELETE /api/accounts/{id}
     */
    @DELETE
    @Path("/{id}")
    public Response deleteAccount(@PathParam("id") Long id) {
        try {
            // اعتبارسنجی برای حذف
            accountService.validateAccountForDeletion(id);

            // حذف نرم
            accountService.softDelete(id);

            log.info("Account soft-deleted via API: ID {}", id);

            return Response.ok()
                    .entity(ApiResponse.success("حساب با موفقیت حذف شد"))
                    .build();

        } catch (IllegalArgumentException e) {
            log.warn("Validation error in account deletion: {}", e.getMessage());
            return Response.status(404)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (IllegalStateException e) {
            log.warn("Business error in account deletion: {}", e.getMessage());
            return Response.status(400)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Error deleting account: {}", id, e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در حذف حساب: " + e.getMessage()))
                    .build();
        }
    }

    // ==================== متدهای کمکی ====================

    /**
     * تبدیل Entity به DTO
     */
    private AccountResponse mapToResponse(Account account) {
        return AccountResponse.builder()
                .accountNumber(account.getAccountNumber())
                .type(account.getType())
                .balance(account.getBalance())
                .status(account.getStatus())
                .build();
    }

    // ==================== DTOs ====================

    public static class AccountCreateRequest {
        private Long userId;
        private AccountType type;
        private BigDecimal initialBalance;

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
        private BigDecimal balance;

        public AccountType getType() { return type; }
        public void setType(AccountType type) { this.type = type; }
        public BigDecimal getBalance() { return balance; }
        public void setBalance(BigDecimal balance) { this.balance = balance; }
    }
}
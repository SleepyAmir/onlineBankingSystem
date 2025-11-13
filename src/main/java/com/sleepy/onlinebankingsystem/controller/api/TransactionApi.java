package com.sleepy.onlinebankingsystem.controller.api;

import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.entity.Transaction;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.model.enums.AccountStatus;
import com.sleepy.onlinebankingsystem.model.enums.TransactionStatus;
import com.sleepy.onlinebankingsystem.model.enums.TransactionType;
import com.sleepy.onlinebankingsystem.service.AccountService;
import com.sleepy.onlinebankingsystem.service.TransactionService;
import com.sleepy.onlinebankingsystem.service.UserService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Path("/api/transactions")
@Slf4j
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TransactionApi {

    @Inject
    private TransactionService transactionService;

    @Inject
    private AccountService accountService;

    @Inject
    private UserService userService;

    /**
     * واریز وجه
     * POST /api/transactions/deposit
     */
    @POST
    @Path("/deposit")
    public Response deposit(DepositRequest request) {
        try {
            log.info("Processing deposit to account: {}", request.getToAccountId());

            // اعتبارسنجی مبلغ
            if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                return Response.status(400)
                        .entity(new ErrorResponse("مبلغ باید بیشتر از صفر باشد"))
                        .build();
            }

            // پیدا کردن حساب مقصد
            Optional<Account> toAccountOpt = accountService.findById(request.getToAccountId());
            if (toAccountOpt.isEmpty()) {
                return Response.status(404)
                        .entity(new ErrorResponse("حساب مقصد یافت نشد"))
                        .build();
            }

            Account toAccount = toAccountOpt.get();

            // بررسی وضعیت حساب
            if (toAccount.getStatus() != AccountStatus.ACTIVE) {
                return Response.status(400)
                        .entity(new ErrorResponse("حساب مقصد فعال نیست"))
                        .build();
            }

            // افزایش موجودی
            toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));
            accountService.update(toAccount);

            // ثبت تراکنش
            Transaction transaction = Transaction.builder()
                    .transactionId(generateTransactionId())
                    .toAccount(toAccount)
                    .amount(request.getAmount())
                    .type(TransactionType.DEPOSIT)
                    .transactionDate(LocalDateTime.now())
                    .status(TransactionStatus.COMPLETED)
                    .description(request.getDescription() != null ?
                            request.getDescription() : "واریز وجه")
                    .referenceNumber(UUID.randomUUID().toString().substring(0, 10))
                    .build();

            Transaction savedTransaction = transactionService.save(transaction);
            log.info("Deposit successful: {}", savedTransaction.getTransactionId());

            return Response.status(201).entity(savedTransaction).build();

        } catch (Exception e) {
            log.error("Error processing deposit", e);
            return Response.status(500)
                    .entity(new ErrorResponse("خطا در واریز: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * برداشت وجه
     * POST /api/transactions/withdrawal
     */
    @POST
    @Path("/withdrawal")
    public Response withdrawal(WithdrawalRequest request) {
        try {
            log.info("Processing withdrawal from account: {}", request.getFromAccountId());

            // اعتبارسنجی مبلغ
            if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                return Response.status(400)
                        .entity(new ErrorResponse("مبلغ باید بیشتر از صفر باشد"))
                        .build();
            }

            // پیدا کردن حساب مبدأ
            Optional<Account> fromAccountOpt = accountService.findById(request.getFromAccountId());
            if (fromAccountOpt.isEmpty()) {
                return Response.status(404)
                        .entity(new ErrorResponse("حساب مبدأ یافت نشد"))
                        .build();
            }

            Account fromAccount = fromAccountOpt.get();

            // بررسی وضعیت حساب
            if (fromAccount.getStatus() != AccountStatus.ACTIVE) {
                return Response.status(400)
                        .entity(new ErrorResponse("حساب مبدأ فعال نیست"))
                        .build();
            }

            // بررسی موجودی کافی
            if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
                return Response.status(400)
                        .entity(new ErrorResponse("موجودی کافی نیست"))
                        .build();
            }

            // کاهش موجودی
            fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
            accountService.update(fromAccount);

            // ثبت تراکنش
            Transaction transaction = Transaction.builder()
                    .transactionId(generateTransactionId())
                    .fromAccount(fromAccount)
                    .amount(request.getAmount())
                    .type(TransactionType.WITHDRAWAL)
                    .transactionDate(LocalDateTime.now())
                    .status(TransactionStatus.COMPLETED)
                    .description(request.getDescription() != null ?
                            request.getDescription() : "برداشت وجه")
                    .referenceNumber(UUID.randomUUID().toString().substring(0, 10))
                    .build();

            Transaction savedTransaction = transactionService.save(transaction);
            log.info("Withdrawal successful: {}", savedTransaction.getTransactionId());

            return Response.status(201).entity(savedTransaction).build();

        } catch (Exception e) {
            log.error("Error processing withdrawal", e);
            return Response.status(500)
                    .entity(new ErrorResponse("خطا در برداشت: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * انتقال وجه
     * POST /api/transactions/transfer
     */
    @POST
    @Path("/transfer")
    public Response transfer(TransferRequest request) {
        try {
            log.info("Processing transfer from account {} to {}",
                    request.getFromAccountId(), request.getToAccountNumber());

            // اعتبارسنجی مبلغ
            if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                return Response.status(400)
                        .entity(new ErrorResponse("مبلغ باید بیشتر از صفر باشد"))
                        .build();
            }

            // پیدا کردن حساب مبدأ
            Optional<Account> fromAccountOpt = accountService.findById(request.getFromAccountId());
            if (fromAccountOpt.isEmpty()) {
                return Response.status(404)
                        .entity(new ErrorResponse("حساب مبدأ یافت نشد"))
                        .build();
            }

            // پیدا کردن حساب مقصد
            Optional<Account> toAccountOpt = accountService
                    .findByAccountNumber(request.getToAccountNumber());
            if (toAccountOpt.isEmpty()) {
                return Response.status(404)
                        .entity(new ErrorResponse("حساب مقصد یافت نشد"))
                        .build();
            }

            Account fromAccount = fromAccountOpt.get();
            Account toAccount = toAccountOpt.get();

            // بررسی انتقال به همان حساب
            if (fromAccount.getId().equals(toAccount.getId())) {
                return Response.status(400)
                        .entity(new ErrorResponse("انتقال به همان حساب امکان‌پذیر نیست"))
                        .build();
            }

            // بررسی وضعیت حساب‌ها
            if (fromAccount.getStatus() != AccountStatus.ACTIVE ||
                    toAccount.getStatus() != AccountStatus.ACTIVE) {
                return Response.status(400)
                        .entity(new ErrorResponse("یکی از حساب‌ها فعال نیست"))
                        .build();
            }

            // بررسی موجودی کافی
            if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
                return Response.status(400)
                        .entity(new ErrorResponse("موجودی کافی نیست"))
                        .build();
            }

            // انجام انتقال
            fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
            toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));

            accountService.update(fromAccount);
            accountService.update(toAccount);

            // ثبت تراکنش
            Transaction transaction = Transaction.builder()
                    .transactionId(generateTransactionId())
                    .fromAccount(fromAccount)
                    .toAccount(toAccount)
                    .amount(request.getAmount())
                    .type(TransactionType.TRANSFER)
                    .transactionDate(LocalDateTime.now())
                    .status(TransactionStatus.COMPLETED)
                    .description(request.getDescription() != null ?
                            request.getDescription() : "انتقال وجه")
                    .referenceNumber(UUID.randomUUID().toString().substring(0, 10))
                    .build();

            Transaction savedTransaction = transactionService.save(transaction);
            log.info("Transfer successful: {}", savedTransaction.getTransactionId());

            return Response.status(201).entity(savedTransaction).build();

        } catch (Exception e) {
            log.error("Error processing transfer", e);
            return Response.status(500)
                    .entity(new ErrorResponse("خطا در انتقال: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * دریافت همه تراکنش‌ها
     * GET /api/transactions
     */
    @GET
    public Response getAllTransactions(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        try {
            List<Transaction> transactions = transactionService.findAll(page, size);
            return Response.ok().entity(transactions).build();

        } catch (Exception e) {
            log.error("Error fetching transactions", e);
            return Response.status(500)
                    .entity(new ErrorResponse("خطا در دریافت تراکنش‌ها: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * دریافت تراکنش با ID
     * GET /api/transactions/{id}
     */
    @GET
    @Path("/{id}")
    public Response getTransactionById(@PathParam("id") Long id) {
        try {
            Optional<Transaction> transactionOpt = transactionService.findById(id);

            if (transactionOpt.isEmpty()) {
                return Response.status(404)
                        .entity(new ErrorResponse("تراکنش یافت نشد"))
                        .build();
            }

            return Response.ok().entity(transactionOpt.get()).build();

        } catch (Exception e) {
            log.error("Error fetching transaction by id: {}", id, e);
            return Response.status(500)
                    .entity(new ErrorResponse("خطا در دریافت تراکنش: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * دریافت تراکنش‌های یک حساب
     * GET /api/transactions/account/{accountId}
     */
    @GET
    @Path("/account/{accountId}")
    public Response getTransactionsByAccount(@PathParam("accountId") Long accountId) {
        try {
            Optional<Account> accountOpt = accountService.findById(accountId);

            if (accountOpt.isEmpty()) {
                return Response.status(404)
                        .entity(new ErrorResponse("حساب یافت نشد"))
                        .build();
            }

            List<Transaction> transactions = transactionService
                    .findByAccount(accountOpt.get());
            return Response.ok().entity(transactions).build();

        } catch (Exception e) {
            log.error("Error fetching transactions for account: {}", accountId, e);
            return Response.status(500)
                    .entity(new ErrorResponse("خطا در دریافت تراکنش‌ها: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * دریافت تراکنش‌های یک کاربر
     * GET /api/transactions/user/{userId}
     */
    @GET
    @Path("/user/{userId}")
    public Response getTransactionsByUser(@PathParam("userId") Long userId) {
        try {
            Optional<User> userOpt = userService.findById(userId);

            if (userOpt.isEmpty()) {
                return Response.status(404)
                        .entity(new ErrorResponse("کاربر یافت نشد"))
                        .build();
            }

            List<Transaction> transactions = transactionService.findByUser(userOpt.get());
            return Response.ok().entity(transactions).build();

        } catch (Exception e) {
            log.error("Error fetching transactions for user: {}", userId, e);
            return Response.status(500)
                    .entity(new ErrorResponse("خطا در دریافت تراکنش‌ها: " + e.getMessage()))
                    .build();
        }
    }

    // ==================== Helper Methods ====================

    private String generateTransactionId() {
        return "TRX" + System.currentTimeMillis() + "-" +
                UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // ==================== Request DTOs ====================

    public static class DepositRequest {
        private Long toAccountId;
        private BigDecimal amount;
        private String description;

        public Long getToAccountId() { return toAccountId; }
        public void setToAccountId(Long toAccountId) { this.toAccountId = toAccountId; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class WithdrawalRequest {
        private Long fromAccountId;
        private BigDecimal amount;
        private String description;

        public Long getFromAccountId() { return fromAccountId; }
        public void setFromAccountId(Long fromAccountId) {
            this.fromAccountId = fromAccountId;
        }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class TransferRequest {
        private Long fromAccountId;
        private String toAccountNumber;
        private BigDecimal amount;
        private String description;

        public Long getFromAccountId() { return fromAccountId; }
        public void setFromAccountId(Long fromAccountId) {
            this.fromAccountId = fromAccountId;
        }
        public String getToAccountNumber() { return toAccountNumber; }
        public void setToAccountNumber(String toAccountNumber) {
            this.toAccountNumber = toAccountNumber;
        }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
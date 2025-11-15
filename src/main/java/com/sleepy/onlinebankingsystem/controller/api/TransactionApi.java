package com.sleepy.onlinebankingsystem.controller.api;

import com.sleepy.onlinebankingsystem.model.dto.request.TransferRequest;
import com.sleepy.onlinebankingsystem.model.dto.response.ApiResponse;
import com.sleepy.onlinebankingsystem.model.dto.response.TransactionResponse;
import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.entity.Transaction;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.service.AccountService;
import com.sleepy.onlinebankingsystem.service.TransactionService;
import com.sleepy.onlinebankingsystem.service.UserService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/transactions")
@Slf4j
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
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
            log.info("API: Processing deposit to account: {}", request.getToAccountNumber());

            // بررسی ورودی
            if (request.getToAccountNumber() == null || request.getToAccountNumber().isBlank()) {
                return Response.status(400)
                        .entity(ApiResponse.error("شماره حساب مقصد الزامی است"))
                        .build();
            }

            // فراخوانی Service
            Transaction transaction = transactionService.processDeposit(
                    request.getToAccountNumber(),
                    request.getAmount(),
                    request.getDescription()
            );

            // تبدیل به Response
            TransactionResponse response = mapToResponse(transaction);

            return Response.status(201).entity(ApiResponse.success(response)).build();

        } catch (IllegalArgumentException e) {
            log.warn("Validation error in deposit: {}", e.getMessage());
            return Response.status(400)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (IllegalStateException e) {
            log.warn("Business logic error in deposit: {}", e.getMessage());
            return Response.status(400)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Error processing deposit", e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در واریز: " + e.getMessage()))
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
            log.info("API: Processing withdrawal from account: {}", request.getFromAccountNumber());

            // بررسی ورودی
            if (request.getFromAccountNumber() == null || request.getFromAccountNumber().isBlank()) {
                return Response.status(400)
                        .entity(ApiResponse.error("شماره حساب مبدأ الزامی است"))
                        .build();
            }

            // فراخوانی Service
            Transaction transaction = transactionService.processWithdrawal(
                    request.getFromAccountNumber(),
                    request.getAmount(),
                    request.getDescription()
            );

            // تبدیل به Response
            TransactionResponse response = mapToResponse(transaction);

            return Response.status(201).entity(ApiResponse.success(response)).build();

        } catch (IllegalArgumentException e) {
            log.warn("Validation error in withdrawal: {}", e.getMessage());
            return Response.status(400)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (IllegalStateException e) {
            log.warn("Business logic error in withdrawal: {}", e.getMessage());
            return Response.status(400)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Error processing withdrawal", e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در برداشت: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * انتقال وجه (کارت به کارت)
     * POST /api/transactions/transfer
     */
    @POST
    @Path("/transfer")
    public Response transfer(TransferRequest request) {
        try {
            log.info("API: Processing card transfer from {} to {}",
                    request.getFromCardNumber(), request.getToCardNumber());

            // بررسی ورودی
            if (request.getFromCardNumber() == null || request.getFromCardNumber().isBlank()) {
                return Response.status(400)
                        .entity(ApiResponse.error("شماره کارت مبدأ الزامی است"))
                        .build();
            }
            if (request.getToCardNumber() == null || request.getToCardNumber().isBlank()) {
                return Response.status(400)
                        .entity(ApiResponse.error("شماره کارت مقصد الزامی است"))
                        .build();
            }

            // فراخوانی Service
            Transaction transaction = transactionService.processCardTransfer(
                    request.getFromCardNumber(),
                    request.getToCardNumber(),
                    request.getAmount(),
                    request.getDescription()
            );

            // تبدیل به Response (با شماره کارت)
            TransactionResponse response = TransactionResponse.builder()
                    .transactionId(transaction.getTransactionId())
                    .fromAccount(request.getFromCardNumber())  // نمایش کارت
                    .toAccount(request.getToCardNumber())      // نمایش کارت
                    .amount(transaction.getAmount())
                    .type(transaction.getType())
                    .date(transaction.getTransactionDate())
                    .status(transaction.getStatus())
                    .referenceNumber(transaction.getReferenceNumber())
                    .build();

            return Response.ok().entity(ApiResponse.success(response)).build();

        } catch (IllegalArgumentException e) {
            log.warn("Validation error in transfer: {}", e.getMessage());
            return Response.status(400)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (IllegalStateException e) {
            log.warn("Business logic error in transfer: {}", e.getMessage());
            return Response.status(400)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Error processing transfer", e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در انتقال: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * برگشت تراکنش
     * POST /api/transactions/{transactionId}/reverse
     */
    @POST
    @Path("/{transactionId}/reverse")
    public Response reverseTransaction(@PathParam("transactionId") String transactionId) {
        try {
            log.info("API: Reversing transaction: {}", transactionId);

            // فراخوانی Service
            Transaction reversedTransaction = transactionService.reverseTransaction(transactionId);

            // تبدیل به Response
            TransactionResponse response = mapToResponse(reversedTransaction);

            return Response.ok().entity(ApiResponse.success(response)).build();

        } catch (IllegalArgumentException e) {
            log.warn("Validation error in reverse: {}", e.getMessage());
            return Response.status(400)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (IllegalStateException e) {
            log.warn("Business logic error in reverse: {}", e.getMessage());
            return Response.status(400)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Error reversing transaction", e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در برگشت تراکنش: " + e.getMessage()))
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
            List<TransactionResponse> responses = transactions.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());

            return Response.ok().entity(ApiResponse.success(responses)).build();

        } catch (Exception e) {
            log.error("Error fetching transactions", e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در دریافت تراکنش‌ها: " + e.getMessage()))
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
                        .entity(ApiResponse.error("تراکنش یافت نشد"))
                        .build();
            }

            TransactionResponse response = mapToResponse(transactionOpt.get());

            return Response.ok().entity(ApiResponse.success(response)).build();

        } catch (Exception e) {
            log.error("Error fetching transaction by id: {}", id, e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در دریافت تراکنش: " + e.getMessage()))
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
                        .entity(ApiResponse.error("حساب یافت نشد"))
                        .build();
            }

            List<Transaction> transactions = transactionService.findByAccount(accountOpt.get());
            List<TransactionResponse> responses = transactions.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());

            return Response.ok().entity(ApiResponse.success(responses)).build();

        } catch (Exception e) {
            log.error("Error fetching transactions for account: {}", accountId, e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در دریافت تراکنش‌ها: " + e.getMessage()))
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
                        .entity(ApiResponse.error("کاربر یافت نشد"))
                        .build();
            }

            List<Transaction> transactions = transactionService.findByUser(userOpt.get());
            List<TransactionResponse> responses = transactions.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());

            return Response.ok().entity(ApiResponse.success(responses)).build();

        } catch (Exception e) {
            log.error("Error fetching transactions for user: {}", userId, e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در دریافت تراکنش‌ها: " + e.getMessage()))
                    .build();
        }
    }

    // ========== متدهای کمکی ==========

    /**
     * تبدیل Entity به DTO
     */
    private TransactionResponse mapToResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .transactionId(transaction.getTransactionId())
                .fromAccount(transaction.getFromAccount() != null ?
                        transaction.getFromAccount().getAccountNumber() : null)
                .toAccount(transaction.getToAccount() != null ?
                        transaction.getToAccount().getAccountNumber() : null)
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .date(transaction.getTransactionDate())
                .status(transaction.getStatus())
                .referenceNumber(transaction.getReferenceNumber())
                .build();
    }

    // ========== Request DTOs ==========

    public static class DepositRequest {
        private String toAccountNumber;
        private BigDecimal amount;
        private String description;

        public String getToAccountNumber() { return toAccountNumber; }
        public void setToAccountNumber(String toAccountNumber) {
            this.toAccountNumber = toAccountNumber;
        }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class WithdrawalRequest {
        private String fromAccountNumber;
        private BigDecimal amount;
        private String description;

        public String getFromAccountNumber() { return fromAccountNumber; }
        public void setFromAccountNumber(String fromAccountNumber) {
            this.fromAccountNumber = fromAccountNumber;
        }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}
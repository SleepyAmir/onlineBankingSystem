package com.sleepy.onlinebankingsystem.controller.api;

import com.sleepy.onlinebankingsystem.model.dto.request.TransferRequest;
import com.sleepy.onlinebankingsystem.model.dto.response.ApiResponse;
import com.sleepy.onlinebankingsystem.model.dto.response.TransactionResponse;
import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.entity.Card;
import com.sleepy.onlinebankingsystem.model.entity.Transaction;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.model.enums.AccountStatus;
import com.sleepy.onlinebankingsystem.model.enums.TransactionStatus;
import com.sleepy.onlinebankingsystem.model.enums.TransactionType;
import com.sleepy.onlinebankingsystem.service.AccountService;
import com.sleepy.onlinebankingsystem.service.CardService;
import com.sleepy.onlinebankingsystem.service.TransactionService;
import com.sleepy.onlinebankingsystem.service.UserService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.security.PrivateKey;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

    @Inject
    private CardService cardService;

    /**
     * واریز وجه
     * POST /api/transactions/deposit
     */
    @POST
    @Path("/deposit")
    public Response deposit(DepositRequest request) {
        try {
            log.info("Processing deposit to account: {}", request.getToAccountNumber());

            if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                return Response.status(400)
                        .entity(ApiResponse.error("مبلغ باید بیشتر از صفر باشد"))
                        .build();
            }

            Optional<Account> toAccountOpt = accountService.findByAccountNumber(request.getToAccountNumber());
            if (toAccountOpt.isEmpty()) {
                return Response.status(404)
                        .entity(ApiResponse.error("حساب مقصد یافت نشد"))
                        .build();
            }

            Account toAccount = toAccountOpt.get();

            if (toAccount.getStatus() != AccountStatus.ACTIVE) {
                return Response.status(400)
                        .entity(ApiResponse.error("حساب مقصد فعال نیست"))
                        .build();
            }

            toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));
            accountService.update(toAccount);

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

            TransactionResponse response = TransactionResponse.builder()
                    .transactionId(savedTransaction.getTransactionId())
                    .fromAccount(savedTransaction.getFromAccount() != null ? savedTransaction.getFromAccount().getAccountNumber() : null)
                    .toAccount(savedTransaction.getToAccount() != null ? savedTransaction.getToAccount().getAccountNumber() : null)
                    .amount(savedTransaction.getAmount())
                    .type(savedTransaction.getType())
                    .date(savedTransaction.getTransactionDate())
                    .status(savedTransaction.getStatus())
                    .referenceNumber(savedTransaction.getReferenceNumber())
                    .build();

            return Response.status(201).entity(ApiResponse.success(response)).build();

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
            log.info("Processing withdrawal from account: {}", request.getFromAccountNumber());

            if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                return Response.status(400)
                        .entity(ApiResponse.error("مبلغ باید بیشتر از صفر باشد"))
                        .build();
            }

            Optional<Account> fromAccountOpt = accountService.findByAccountNumber(request.getFromAccountNumber());
            if (fromAccountOpt.isEmpty()) {
                return Response.status(404)
                        .entity(ApiResponse.error("حساب مبدأ یافت نشد"))
                        .build();
            }

            Account fromAccount = fromAccountOpt.get();

            if (fromAccount.getStatus() != AccountStatus.ACTIVE) {
                return Response.status(400)
                        .entity(ApiResponse.error("حساب مبدأ فعال نیست"))
                        .build();
            }

            if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
                return Response.status(400)
                        .entity(ApiResponse.error("موجودی کافی نیست"))
                        .build();
            }

            fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
            accountService.update(fromAccount);

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

            TransactionResponse response = TransactionResponse.builder()
                    .transactionId(savedTransaction.getTransactionId())
                    .fromAccount(savedTransaction.getFromAccount() != null ? savedTransaction.getFromAccount().getAccountNumber() : null)
                    .toAccount(savedTransaction.getToAccount() != null ? savedTransaction.getToAccount().getAccountNumber() : null)
                    .amount(savedTransaction.getAmount())
                    .type(savedTransaction.getType())
                    .date(savedTransaction.getTransactionDate())
                    .status(savedTransaction.getStatus())
                    .referenceNumber(savedTransaction.getReferenceNumber())
                    .build();

            return Response.status(201).entity(ApiResponse.success(response)).build();

        } catch (Exception e) {
            log.error("Error processing withdrawal", e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در برداشت: " + e.getMessage()))
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
            log.info("Processing transfer from card {} to card {}",
                    request.getFromCardNumber(), request.getToCardNumber());


            if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                return Response.status(400)
                        .entity(ApiResponse.error("مبلغ باید بیشتر از صفر باشد"))
                        .build();
            }

            Optional<Card> fromCardOpt = cardService.findByCardNumber(request.getFromCardNumber());
            if (fromCardOpt.isEmpty()) {
                return Response.status(404)
                        .entity(ApiResponse.error("کارت مبدأ یافت نشد"))
                        .build();
            }
            Card fromCard = fromCardOpt.get();
            if (!fromCard.isActive()) {
                return Response.status(400)
                        .entity(ApiResponse.error("کارت مبدأ غیرفعال است"))
                        .build();
            }
            Account fromAccount = fromCard.getAccount();

            Optional<Card> toCardOpt = cardService.findByCardNumber(request.getToCardNumber());
            if (toCardOpt.isEmpty()) {
                return Response.status(404)
                        .entity(ApiResponse.error("کارت مقصد یافت نشد"))
                        .build();
            }
            Card toCard = toCardOpt.get();
            if (!toCard.isActive()) {
                return Response.status(400)
                        .entity(ApiResponse.error("کارت مقصد غیرفعال است"))
                        .build();
            }
            Account toAccount = toCard.getAccount();

            if (fromAccount.getStatus() != AccountStatus.ACTIVE || toAccount.getStatus() != AccountStatus.ACTIVE) {
                return Response.status(400)
                        .entity(ApiResponse.error("هر دو حساب باید فعال باشند"))
                        .build();
            }

            if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
                return Response.status(400)
                        .entity(ApiResponse.error("موجودی حساب مبدأ کافی نیست"))
                        .build();
            }

            Transaction transaction = Transaction.builder()
                    .transactionId(generateTransactionId())
                    .fromAccount(fromAccount)
                    .toAccount(toAccount)
                    .amount(request.getAmount())
                    .type(TransactionType.TRANSFER)
                    .transactionDate(LocalDateTime.now())
                    .status(TransactionStatus.COMPLETED)
                    .description(request.getDescription())
                    .referenceNumber(UUID.randomUUID().toString().substring(0, 10))
                    .build();

            transactionService.save(transaction);

            fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
            toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));
            accountService.update(fromAccount);
            accountService.update(toAccount);

            TransactionResponse response = TransactionResponse.builder()
                    .transactionId(transaction.getTransactionId())
                    .fromAccount(fromCard.getCardNumber())  // برگرداندن cardNumber
                    .toAccount(toCard.getCardNumber())      // برگرداندن cardNumber
                    .amount(transaction.getAmount())
                    .type(transaction.getType())
                    .date(transaction.getTransactionDate())
                    .status(transaction.getStatus())
                    .referenceNumber(transaction.getReferenceNumber())
                    .build();

            return Response.ok().entity(ApiResponse.success(response)).build();

        } catch (Exception e) {
            log.error("Error in transfer", e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در انتقال: " + e.getMessage()))
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
                    .map(t -> TransactionResponse.builder()
                            .transactionId(t.getTransactionId())
                            .fromAccount(t.getFromAccount() != null ? t.getFromAccount().getAccountNumber() : null)
                            .toAccount(t.getToAccount() != null ? t.getToAccount().getAccountNumber() : null)
                            .amount(t.getAmount())
                            .type(t.getType())
                            .date(t.getTransactionDate())
                            .status(t.getStatus())
                            .referenceNumber(t.getReferenceNumber())
                            .build())
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

            Transaction t = transactionOpt.get();
            TransactionResponse response = TransactionResponse.builder()
                    .transactionId(t.getTransactionId())
                    .fromAccount(t.getFromAccount() != null ? t.getFromAccount().getAccountNumber() : null)
                    .toAccount(t.getToAccount() != null ? t.getToAccount().getAccountNumber() : null)
                    .amount(t.getAmount())
                    .type(t.getType())
                    .date(t.getTransactionDate())
                    .status(t.getStatus())
                    .referenceNumber(t.getReferenceNumber())
                    .build();

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

            List<Transaction> transactions = transactionService
                    .findByAccount(accountOpt.get());
            List<TransactionResponse> responses = transactions.stream()
                    .map(t -> TransactionResponse.builder()
                            .transactionId(t.getTransactionId())
                            .fromAccount(t.getFromAccount() != null ? t.getFromAccount().getAccountNumber() : null)
                            .toAccount(t.getToAccount() != null ? t.getToAccount().getAccountNumber() : null)
                            .amount(t.getAmount())
                            .type(t.getType())
                            .date(t.getTransactionDate())
                            .status(t.getStatus())
                            .referenceNumber(t.getReferenceNumber())
                            .build())
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
                    .map(t -> TransactionResponse.builder()
                            .transactionId(t.getTransactionId())
                            .fromAccount(t.getFromAccount() != null ? t.getFromAccount().getAccountNumber() : null)
                            .toAccount(t.getToAccount() != null ? t.getToAccount().getAccountNumber() : null)
                            .amount(t.getAmount())
                            .type(t.getType())
                            .date(t.getTransactionDate())
                            .status(t.getStatus())
                            .referenceNumber(t.getReferenceNumber())
                            .build())
                    .collect(Collectors.toList());

            return Response.ok().entity(ApiResponse.success(responses)).build();

        } catch (Exception e) {
            log.error("Error fetching transactions for user: {}", userId, e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در دریافت تراکنش‌ها: " + e.getMessage()))
                    .build();
        }
    }


    private String generateTransactionId() {
        return "TRX" + System.currentTimeMillis() + "-" +
                UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }


    public static class DepositRequest {
        private String toAccountNumber;
        private BigDecimal amount;
        private String description;

        public String getToAccountNumber() { return toAccountNumber; }
        public void setToAccountNumber(String toAccountNumber) { this.toAccountNumber = toAccountNumber; }
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
        public void setFromAccountNumber(String fromAccountNumber) { this.fromAccountNumber = fromAccountNumber; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}
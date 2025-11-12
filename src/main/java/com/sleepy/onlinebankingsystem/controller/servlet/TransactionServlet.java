package com.sleepy.onlinebankingsystem.controller.servlet;

import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.entity.Transaction;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.model.enums.AccountStatus;
import com.sleepy.onlinebankingsystem.model.enums.TransactionStatus;
import com.sleepy.onlinebankingsystem.model.enums.UserRole;
import com.sleepy.onlinebankingsystem.service.AccountService;
import com.sleepy.onlinebankingsystem.service.TransactionService;
import com.sleepy.onlinebankingsystem.service.UserService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Slf4j
@WebServlet("/transactions")
public class TransactionServlet extends HttpServlet {

    @Inject private TransactionService transactionService;
    @Inject private AccountService accountService;
    @Inject private UserService userService;

    @Override
    @Transactional
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("username") == null) {
                resp.sendRedirect(req.getContextPath() + "/auth/login");
                return;
            }

            String currentUsername = (String) session.getAttribute("username");
            Optional<User> userOpt = userService.findByUsername(currentUsername);

            if (userOpt.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/auth/login?error=user_not_found");
                return;
            }

            User user = userOpt.get();
            List<Account> accounts = accountService.findByUser(user);
            req.setAttribute("accounts", accounts);

            req.getRequestDispatcher("/views/transactions/form.jsp").forward(req, resp);

        } catch (Exception e) {
            log.error("Error loading transaction form", e);
            req.setAttribute("error", "خطا در بارگذاری فرم: " + e.getMessage());
            req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            String action = req.getParameter("action");

            if (action == null || action.isBlank()) {
                req.setAttribute("error", "نوع عملیات الزامی است");
                doGet(req, resp);
                return;
            }

            switch (action) {
                case "deposit":
                    handleDeposit(req, resp);
                    break;
                case "withdrawal":
                    handleWithdrawal(req, resp);
                    break;
                case "transfer":
                    handleTransfer(req, resp);
                    break;
                default:
                    req.setAttribute("error", "عملیات نامعتبر است");
                    doGet(req, resp);
                    break;
            }

        } catch (Exception e) {
            log.error("Error processing transaction", e);
            req.setAttribute("error", "خطا در پردازش: " + e.getMessage());
            doGet(req, resp);
        }
    }

    // ──────────────────────────────────────────────────────────────
    // واریز
    // ──────────────────────────────────────────────────────────────
    private void handleDeposit(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {

        String accountId = req.getParameter("toAccountId");
        String amountParam = req.getParameter("amount");
        String description = req.getParameter("description");

        if (accountId == null || accountId.isBlank()) {
            req.setAttribute("error", "انتخاب حساب مقصد الزامی است");
            doGet(req, resp);
            return;
        }

        BigDecimal amount = validateAmount(amountParam, req, resp);
        if (amount == null) return;

        Optional<Account> toAccountOpt = accountService.findById(Long.parseLong(accountId));
        if (toAccountOpt.isEmpty()) {
            req.setAttribute("error", "حساب مقصد یافت نشد");
            doGet(req, resp);
            return;
        }

        Account toAccount = toAccountOpt.get();
        if (toAccount.getStatus() != AccountStatus.ACTIVE) {
            req.setAttribute("error", "حساب مقصد فعال نیست");
            doGet(req, resp);
            return;
        }

        if (!isAdminOrManager(req) && !toAccount.getUser().getUsername().equals(getCurrentUsername(req))) {
            req.setAttribute("error", "شما فقط می‌توانید به حساب خودتان واریز کنید");
            doGet(req, resp);
            return;
        }

        toAccount.setBalance(toAccount.getBalance().add(amount));
        accountService.update(toAccount);

        Transaction transaction = createTransaction(null, toAccount, amount, "واریز به حساب", description);
        transactionService.save(transaction);

        redirectToDetail(resp, transaction.getId(), "deposit_success");
    }

    // ──────────────────────────────────────────────────────────────
    // برداشت
    // ──────────────────────────────────────────────────────────────
    private void handleWithdrawal(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {

        String accountId = req.getParameter("fromAccountId");
        String amountParam = req.getParameter("amount");
        String description = req.getParameter("description");

        if (accountId == null || accountId.isBlank()) {
            req.setAttribute("error", "انتخاب حساب مبدأ الزامی است");
            doGet(req, resp);
            return;
        }

        BigDecimal amount = validateAmount(amountParam, req, resp);
        if (amount == null) return;

        Optional<Account> fromAccountOpt = accountService.findById(Long.parseLong(accountId));
        if (fromAccountOpt.isEmpty()) {
            req.setAttribute("error", "حساب مبدأ یافت نشد");
            doGet(req, resp);
            return;
        }

        Account fromAccount = fromAccountOpt.get();
        if (fromAccount.getStatus() != AccountStatus.ACTIVE) {
            req.setAttribute("error", "حساب مبدأ فعال نیست");
            doGet(req, resp);
            return;
        }

        if (!isAdminOrManager(req) && !fromAccount.getUser().getUsername().equals(getCurrentUsername(req))) {
            req.setAttribute("error", "شما فقط می‌توانید از حساب خودتان برداشت کنید");
            doGet(req, resp);
            return;
        }

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            req.setAttribute("error", "موجودی کافی نیست");
            doGet(req, resp);
            return;
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        accountService.update(fromAccount);

        Transaction transaction = createTransaction(fromAccount, null, amount, "برداشت از حساب", description);
        transactionService.save(transaction);

        redirectToDetail(resp, transaction.getId(), "withdrawal_success");
    }

    // ──────────────────────────────────────────────────────────────
    // انتقال
    // ──────────────────────────────────────────────────────────────
    private void handleTransfer(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {

        String fromAccountId = req.getParameter("fromAccountId");
        String toAccountNumber = req.getParameter("toAccountNumber");
        String amountParam = req.getParameter("amount");
        String description = req.getParameter("description");

        if (fromAccountId == null || fromAccountId.isBlank()) {
            req.setAttribute("error", "انتخاب حساب مبدأ الزامی است");
            doGet(req, resp);
            return;
        }
        if (toAccountNumber == null || toAccountNumber.isBlank()) {
            req.setAttribute("error", "شماره حساب مقصد الزامی است");
            doGet(req, resp);
            return;
        }

        BigDecimal amount = validateAmount(amountParam, req, resp);
        if (amount == null) return;

        Optional<Account> fromOpt = accountService.findById(Long.parseLong(fromAccountId));
        Optional<Account> toOpt = accountService.findByAccountNumber(toAccountNumber);

        if (fromOpt.isEmpty()) {
            req.setAttribute("error", "حساب مبدأ یافت نشد");
            doGet(req, resp);
            return;
        }
        if (toOpt.isEmpty()) {
            req.setAttribute("error", "حساب مقصد یافت نشد");
            doGet(req, resp);
            return;
        }

        Account from = fromOpt.get();
        Account to = toOpt.get();

        if (from.getId().equals(to.getId())) {
            req.setAttribute("error", "انتقال به همان حساب امکان‌پذیر نیست");
            doGet(req, resp);
            return;
        }

        if (from.getStatus() != AccountStatus.ACTIVE || to.getStatus() != AccountStatus.ACTIVE) {
            req.setAttribute("error", "یکی از حساب‌ها فعال نیست");
            doGet(req, resp);
            return;
        }

        if (!isAdminOrManager(req) && !from.getUser().getUsername().equals(getCurrentUsername(req))) {
            req.setAttribute("error", "شما فقط می‌توانید از حساب خودتان انتقال دهید");
            doGet(req, resp);
            return;
        }

        if (from.getBalance().compareTo(amount) < 0) {
            req.setAttribute("error", "موجودی کافی نیست");
            doGet(req, resp);
            return;
        }

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
        accountService.update(from);
        accountService.update(to);

        Transaction transaction = createTransaction(from, to, amount, "انتقال وجه", description);
        transactionService.save(transaction);

        redirectToDetail(resp, transaction.getId(), "transfer_success");
    }

    // ──────────────────────────────────────────────────────────────
    // متدهای کمکی
    // ──────────────────────────────────────────────────────────────
    private BigDecimal validateAmount(String amountParam, HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (amountParam == null || amountParam.isBlank()) {
            req.setAttribute("error", "مبلغ الزامی است");
            doGet(req, resp);
            return null;
        }
        try {
            BigDecimal amount = new BigDecimal(amountParam);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                req.setAttribute("error", "مبلغ باید بیشتر از صفر باشد");
                doGet(req, resp);
                return null;
            }
            return amount;
        } catch (NumberFormatException e) {
            req.setAttribute("error", "مبلغ نامعتبر است");
            doGet(req, resp);
            return null;
        }
    }

    private Transaction createTransaction(Account from, Account to, BigDecimal amount, String defaultDesc, String description) {
        return Transaction.builder()
                .transactionId(generateTransactionId())
                .fromAccount(from)
                .toAccount(to)
                .amount(amount)
                .transactionDate(LocalDateTime.now())
                .status(TransactionStatus.COMPLETED)
                .description(description != null && !description.isBlank() ? description : defaultDesc)
                .referenceNumber(UUID.randomUUID().toString().substring(0, 10))
                .build();
    }

    private String generateTransactionId() {
        return "TRX" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private void redirectToDetail(HttpServletResponse resp, Long id, String message) throws IOException {
        resp.sendRedirect("/transactions/detail?id=" + id + "&message=" + message);
    }

    private String getCurrentUsername(HttpServletRequest req) {
        return (String) req.getSession(false).getAttribute("username");
    }

    private boolean isAdminOrManager(HttpServletRequest req) {
        @SuppressWarnings("unchecked")
        Set<UserRole> roles = (Set<UserRole>) req.getSession(false).getAttribute("roles");
        return roles != null && (roles.contains(UserRole.ADMIN) || roles.contains(UserRole.MANAGER));
    }
}
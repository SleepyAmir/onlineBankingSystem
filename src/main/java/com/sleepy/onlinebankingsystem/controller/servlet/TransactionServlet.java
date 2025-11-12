package com.sleepy.onlinebankingsystem.controller.servlet;

import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.entity.Transaction;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.model.enums.AccountStatus;
import com.sleepy.onlinebankingsystem.model.enums.TransactionStatus;
import com.sleepy.onlinebankingsystem.model.enums.TransactionType;
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

    @Inject
    private TransactionService transactionService;

    @Inject
    private AccountService accountService;

    @Inject
    private UserService userService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        try {
            HttpSession session = req.getSession(false);
            String currentUsername = (String) session.getAttribute("username");

            // 1️⃣ دریافت حساب‌های کاربر
            Optional<User> userOpt = userService.findByUsername(currentUsername);
            
            if (userOpt.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/auth/login?error=user_not_found");
                return;
            }

            User user = userOpt.get();
            List<Account> userAccounts = accountService.findByUser(user);

            // 2️⃣ ارسال اطلاعات به JSP
            req.setAttribute("accounts", userAccounts);
            req.setAttribute("transactionTypes", TransactionType.values());

            // 3️⃣ نمایش فرم تراکنش
            req.getRequestDispatcher("/WEB-INF/views/transactions/form.jsp").forward(req, resp);

        } catch (Exception e) {
            log.error("Error loading transaction form", e);
            req.setAttribute("error", "خطا در بارگذاری فرم: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        try {
            // 1️⃣ دریافت نوع تراکنش
            String transactionTypeParam = req.getParameter("transactionType");
            
            if (transactionTypeParam == null || transactionTypeParam.isBlank()) {
                req.setAttribute("error", "نوع تراکنش الزامی است");
                doGet(req, resp);
                return;
            }

            TransactionType transactionType = TransactionType.valueOf(transactionTypeParam);

            // 2️⃣ هدایت به متد مناسب بر اساس نوع تراکنش
            switch (transactionType) {
                case DEPOSIT:
                    handleDeposit(req, resp);
                    break;
                case WITHDRAWAL:
                    handleWithdrawal(req, resp);
                    break;
                case TRANSFER:
                    handleTransfer(req, resp);
                    break;
                default:
                    req.setAttribute("error", "نوع تراکنش پشتیبانی نمی‌شود");
                    doGet(req, resp);
            }

        } catch (IllegalArgumentException e) {
            log.error("Invalid transaction type", e);
            req.setAttribute("error", "نوع تراکنش نامعتبر است");
            doGet(req, resp);
        } catch (Exception e) {
            log.error("Error processing transaction", e);
            req.setAttribute("error", "خطا در پردازش تراکنش: " + e.getMessage());
            doGet(req, resp);
        }
    }

    /**
     * 💰 واریز (Deposit)
     */
    private void handleDeposit(HttpServletRequest req, HttpServletResponse resp) 
            throws Exception {
        
        // 1️⃣ دریافت پارامترها
        String accountId = req.getParameter("toAccountId");
        String amountParam = req.getParameter("amount");
        String description = req.getParameter("description");

        // 2️⃣ اعتبارسنجی
        if (accountId == null || accountId.isBlank()) {
            req.setAttribute("error", "انتخاب حساب مقصد الزامی است");
            doGet(req, resp);
            return;
        }

        if (amountParam == null || amountParam.isBlank()) {
            req.setAttribute("error", "مبلغ الزامی است");
            doGet(req, resp);
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountParam);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                req.setAttribute("error", "مبلغ باید بیشتر از صفر باشد");
                doGet(req, resp);
                return;
            }
        } catch (NumberFormatException e) {
            req.setAttribute("error", "مبلغ نامعتبر است");
            doGet(req, resp);
            return;
        }

        // 3️⃣ پیدا کردن حساب مقصد
        Long toAccountIdLong = Long.parseLong(accountId);
        Optional<Account> toAccountOpt = accountService.findById(toAccountIdLong);
        
        if (toAccountOpt.isEmpty()) {
            req.setAttribute("error", "حساب مقصد یافت نشد");
            doGet(req, resp);
            return;
        }

        Account toAccount = toAccountOpt.get();

        // 4️⃣ بررسی وضعیت حساب
        if (toAccount.getStatus() != AccountStatus.ACTIVE) {
            req.setAttribute("error", "حساب مقصد فعال نیست");
            doGet(req, resp);
            return;
        }

        // 5️⃣ بررسی مالکیت حساب
        HttpSession session = req.getSession(false);
        String currentUsername = (String) session.getAttribute("username");
        
        @SuppressWarnings("unchecked")
        Set<UserRole> userRoles = (Set<UserRole>) session.getAttribute("roles");

        // کاربر عادی فقط می‌تواند به حساب خودش واریز کند
        if (!userRoles.contains(UserRole.ADMIN) && !userRoles.contains(UserRole.MANAGER)) {
            if (!toAccount.getUser().getUsername().equals(currentUsername)) {
                req.setAttribute("error", "شما فقط می‌توانید به حساب خودتان واریز کنید");
                doGet(req, resp);
                return;
            }
        }

        // 6️⃣ افزایش موجودی
        toAccount.setBalance(toAccount.getBalance().add(amount));
        accountService.update(toAccount);

        // 7️⃣ ثبت تراکنش
        String transactionId = generateTransactionId();
        
        Transaction transaction = Transaction.builder()
                .transactionId(transactionId)
                .toAccount(toAccount)
                .amount(amount)
                .type(TransactionType.DEPOSIT)
                .transactionDate(LocalDateTime.now())
                .status(TransactionStatus.COMPLETED)
                .description(description != null ? description : "واریز به حساب")
                .referenceNumber(UUID.randomUUID().toString().substring(0, 10))
                .build();

        transactionService.save(transaction);

        log.info("Deposit successful: {} to account {} by {}", 
                amount, toAccount.getAccountNumber(), currentUsername);

        // 8️⃣ هدایت به صفحه موفقیت
        resp.sendRedirect(req.getContextPath() + "/transactions/detail?id=" + 
                transaction.getId() + "&message=deposit_success");
    }

    /**
     * 💸 برداشت (Withdrawal)
     */
    private void handleWithdrawal(HttpServletRequest req, HttpServletResponse resp) 
            throws Exception {
        
        // 1️⃣ دریافت پارامترها
        String accountId = req.getParameter("fromAccountId");
        String amountParam = req.getParameter("amount");
        String description = req.getParameter("description");

        // 2️⃣ اعتبارسنجی
        if (accountId == null || accountId.isBlank()) {
            req.setAttribute("error", "انتخاب حساب مبدأ الزامی است");
            doGet(req, resp);
            return;
        }

        if (amountParam == null || amountParam.isBlank()) {
            req.setAttribute("error", "مبلغ الزامی است");
            doGet(req, resp);
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountParam);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                req.setAttribute("error", "مبلغ باید بیشتر از صفر باشد");
                doGet(req, resp);
                return;
            }
        } catch (NumberFormatException e) {
            req.setAttribute("error", "مبلغ نامعتبر است");
            doGet(req, resp);
            return;
        }

        // 3️⃣ پیدا کردن حساب مبدأ
        Long fromAccountIdLong = Long.parseLong(accountId);
        Optional<Account> fromAccountOpt = accountService.findById(fromAccountIdLong);
        
        if (fromAccountOpt.isEmpty()) {
            req.setAttribute("error", "حساب مبدأ یافت نشد");
            doGet(req, resp);
            return;
        }

        Account fromAccount = fromAccountOpt.get();

        // 4️⃣ بررسی وضعیت حساب
        if (fromAccount.getStatus() != AccountStatus.ACTIVE) {
            req.setAttribute("error", "حساب مبدأ فعال نیست");
            doGet(req, resp);
            return;
        }

        // 5️⃣ بررسی مالکیت حساب
        HttpSession session = req.getSession(false);
        String currentUsername = (String) session.getAttribute("username");
        
        @SuppressWarnings("unchecked")
        Set<UserRole> userRoles = (Set<UserRole>) session.getAttribute("roles");

        if (!userRoles.contains(UserRole.ADMIN) && !userRoles.contains(UserRole.MANAGER)) {
            if (!fromAccount.getUser().getUsername().equals(currentUsername)) {
                req.setAttribute("error", "شما فقط می‌توانید از حساب خودتان برداشت کنید");
                doGet(req, resp);
                return;
            }
        }

        // 6️⃣ بررسی موجودی کافی
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            req.setAttribute("error", "موجودی حساب کافی نیست");
            doGet(req, resp);
            return;
        }

        // 7️⃣ کاهش موجودی
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        accountService.update(fromAccount);

        // 8️⃣ ثبت تراکنش
        String transactionId = generateTransactionId();
        
        Transaction transaction = Transaction.builder()
                .transactionId(transactionId)
                .fromAccount(fromAccount)
                .amount(amount)
                .type(TransactionType.WITHDRAWAL)
                .transactionDate(LocalDateTime.now())
                .status(TransactionStatus.COMPLETED)
                .description(description != null ? description : "برداشت از حساب")
                .referenceNumber(UUID.randomUUID().toString().substring(0, 10))
                .build();

        transactionService.save(transaction);

        log.info("Withdrawal successful: {} from account {} by {}", 
                amount, fromAccount.getAccountNumber(), currentUsername);

        // 9️⃣ هدایت به صفحه موفقیت
        resp.sendRedirect(req.getContextPath() + "/transactions/detail?id=" + 
                transaction.getId() + "&message=withdrawal_success");
    }

    /**
     * 🔄 انتقال (Transfer)
     */
    private void handleTransfer(HttpServletRequest req, HttpServletResponse resp) 
            throws Exception {
        
        // 1️⃣ دریافت پارامترها
        String fromAccountIdParam = req.getParameter("fromAccountId");
        String toAccountNumberParam = req.getParameter("toAccountNumber");
        String amountParam = req.getParameter("amount");
        String description = req.getParameter("description");

        // 2️⃣ اعتبارسنجی
        if (fromAccountIdParam == null || fromAccountIdParam.isBlank()) {
            req.setAttribute("error", "انتخاب حساب مبدأ الزامی است");
            doGet(req, resp);
            return;
        }

        if (toAccountNumberParam == null || toAccountNumberParam.isBlank()) {
            req.setAttribute("error", "شماره حساب مقصد الزامی است");
            doGet(req, resp);
            return;
        }

        if (amountParam == null || amountParam.isBlank()) {
            req.setAttribute("error", "مبلغ الزامی است");
            doGet(req, resp);
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountParam);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                req.setAttribute("error", "مبلغ باید بیشتر از صفر باشد");
                doGet(req, resp);
                return;
            }
        } catch (NumberFormatException e) {
            req.setAttribute("error", "مبلغ نامعتبر است");
            doGet(req, resp);
            return;
        }

        // 3️⃣ پیدا کردن حساب مبدأ
        Long fromAccountId = Long.parseLong(fromAccountIdParam);
        Optional<Account> fromAccountOpt = accountService.findById(fromAccountId);
        
        if (fromAccountOpt.isEmpty()) {
            req.setAttribute("error", "حساب مبدأ یافت نشد");
            doGet(req, resp);
            return;
        }

        Account fromAccount = fromAccountOpt.get();

        // 4️⃣ پیدا کردن حساب مقصد
        Optional<Account> toAccountOpt = accountService.findByAccountNumber(toAccountNumberParam);
        
        if (toAccountOpt.isEmpty()) {
            req.setAttribute("error", "حساب مقصد با این شماره یافت نشد");
            doGet(req, resp);
            return;
        }

        Account toAccount = toAccountOpt.get();

        // 5️⃣ بررسی عدم انتقال به خود
        if (fromAccount.getId().equals(toAccount.getId())) {
            req.setAttribute("error", "انتقال به همان حساب امکان‌پذیر نیست");
            doGet(req, resp);
            return;
        }

        // 6️⃣ بررسی وضعیت حساب‌ها
        if (fromAccount.getStatus() != AccountStatus.ACTIVE) {
            req.setAttribute("error", "حساب مبدأ فعال نیست");
            doGet(req, resp);
            return;
        }

        if (toAccount.getStatus() != AccountStatus.ACTIVE) {
            req.setAttribute("error", "حساب مقصد فعال نیست");
            doGet(req, resp);
            return;
        }

        // 7️⃣ بررسی مالکیت حساب مبدأ
        HttpSession session = req.getSession(false);
        String currentUsername = (String) session.getAttribute("username");
        
        @SuppressWarnings("unchecked")
        Set<UserRole> userRoles = (Set<UserRole>) session.getAttribute("roles");

        if (!userRoles.contains(UserRole.ADMIN) && !userRoles.contains(UserRole.MANAGER)) {
            if (!fromAccount.getUser().getUsername().equals(currentUsername)) {
                req.setAttribute("error", "شما فقط می‌توانید از حساب خودتان انتقال دهید");
                doGet(req, resp);
                return;
            }
        }

        // 8️⃣ بررسی موجودی کافی
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            req.setAttribute("error", "موجودی حساب مبدأ کافی نیست");
            doGet(req, resp);
            return;
        }

        // 9️⃣ انجام انتقال (Transaction)
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        accountService.update(fromAccount);
        accountService.update(toAccount);

        // 🔟 ثبت تراکنش
        String transactionId = generateTransactionId();
        
        Transaction transaction = Transaction.builder()
                .transactionId(transactionId)
                .fromAccount(fromAccount)
                .toAccount(toAccount)
                .amount(amount)
                .type(TransactionType.TRANSFER)
                .transactionDate(LocalDateTime.now())
                .status(TransactionStatus.COMPLETED)
                .description(description != null ? description : "انتقال وجه")
                .referenceNumber(UUID.randomUUID().toString().substring(0, 10))
                .build();

        transactionService.save(transaction);

        log.info("Transfer successful: {} from {} to {} by {}", 
                amount, fromAccount.getAccountNumber(), 
                toAccount.getAccountNumber(), currentUsername);

        // 1️⃣1️⃣ هدایت به صفحه موفقیت
        resp.sendRedirect(req.getContextPath() + "/transactions/detail?id=" + 
                transaction.getId() + "&message=transfer_success");
    }

    /**
     * تولید شناسه یکتا برای تراکنش
     */
    private String generateTransactionId() {
        return "TRX" + System.currentTimeMillis() + "-" + 
               UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
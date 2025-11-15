package com.sleepy.onlinebankingsystem.controller.servlet;

import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.entity.Transaction;
import com.sleepy.onlinebankingsystem.model.entity.User;
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
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@WebServlet("/transactions")
public class TransactionServlet extends HttpServlet {

    @Inject
    private TransactionService transactionService;

    @Inject
    private AccountService accountService;

    @Inject
    private UserService userService;

    /**
     * نمایش فرم تراکنش
     */
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

    /**
     * پردازش تراکنش
     */
    @Override
    @Transactional
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            String action = req.getParameter("action");

            if (action == null || action.isBlank()) {
                setError(req, resp, "نوع عملیات الزامی است");
                return;
            }

            // مسیریابی به متد مربوطه
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
                    setError(req, resp, "عملیات نامعتبر است");
                    break;
            }

        } catch (Exception e) {
            log.error("Error processing transaction", e);
            setError(req, resp, "خطا در پردازش: " + e.getMessage());
        }
    }

    // ========== متدهای پردازش ==========

    /**
     * واریز وجه
     */
    private void handleDeposit(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {

        String accountId = req.getParameter("toAccountId");
        String amountParam = req.getParameter("amount");
        String description = req.getParameter("description");

        // اعتبارسنجی ورودی
        if (accountId == null || accountId.isBlank()) {
            setError(req, resp, "انتخاب حساب مقصد الزامی است");
            return;
        }

        BigDecimal amount = validateAndParseAmount(amountParam);
        if (amount == null) {
            setError(req, resp, "مبلغ نامعتبر است");
            return;
        }

        // دریافت شماره حساب
        Account account = accountService.findById(Long.parseLong(accountId))
                .orElseThrow(() -> new IllegalArgumentException("حساب یافت نشد"));

        // بررسی دسترسی
        if (!hasAccessToAccount(req, account)) {
            setError(req, resp, "شما فقط می‌توانید به حساب خودتان واریز کنید");
            return;
        }

        // فراخوانی Service
        Transaction transaction = transactionService.processDeposit(
                account.getAccountNumber(),
                amount,
                description
        );

        // هدایت به صفحه موفقیت
        redirectToDetail(resp, req.getContextPath(), transaction.getId(), "deposit_success");
    }

    /**
     * برداشت وجه
     */
    private void handleWithdrawal(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {

        String accountId = req.getParameter("fromAccountId");
        String amountParam = req.getParameter("amount");
        String description = req.getParameter("description");

        // اعتبارسنجی ورودی
        if (accountId == null || accountId.isBlank()) {
            setError(req, resp, "انتخاب حساب مبدأ الزامی است");
            return;
        }

        BigDecimal amount = validateAndParseAmount(amountParam);
        if (amount == null) {
            setError(req, resp, "مبلغ نامعتبر است");
            return;
        }

        // دریافت شماره حساب
        Account account = accountService.findById(Long.parseLong(accountId))
                .orElseThrow(() -> new IllegalArgumentException("حساب یافت نشد"));

        // بررسی دسترسی
        if (!hasAccessToAccount(req, account)) {
            setError(req, resp, "شما فقط می‌توانید از حساب خودتان برداشت کنید");
            return;
        }

        // فراخوانی Service
        Transaction transaction = transactionService.processWithdrawal(
                account.getAccountNumber(),
                amount,
                description
        );

        // هدایت به صفحه موفقیت
        redirectToDetail(resp, req.getContextPath(), transaction.getId(), "withdrawal_success");
    }

    /**
     * انتقال وجه
     */
    private void handleTransfer(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {

        String fromAccountId = req.getParameter("fromAccountId");
        String toAccountNumber = req.getParameter("toAccountNumber");
        String amountParam = req.getParameter("amount");
        String description = req.getParameter("description");

        // اعتبارسنجی ورودی
        if (fromAccountId == null || fromAccountId.isBlank()) {
            setError(req, resp, "انتخاب حساب مبدأ الزامی است");
            return;
        }
        if (toAccountNumber == null || toAccountNumber.isBlank()) {
            setError(req, resp, "شماره حساب مقصد الزامی است");
            return;
        }

        BigDecimal amount = validateAndParseAmount(amountParam);
        if (amount == null) {
            setError(req, resp, "مبلغ نامعتبر است");
            return;
        }

        // دریافت حساب مبدأ
        Account fromAccount = accountService.findById(Long.parseLong(fromAccountId))
                .orElseThrow(() -> new IllegalArgumentException("حساب مبدأ یافت نشد"));

        // بررسی دسترسی
        if (!hasAccessToAccount(req, fromAccount)) {
            setError(req, resp, "شما فقط می‌توانید از حساب خودتان انتقال دهید");
            return;
        }

        // فراخوانی Service
        Transaction transaction = transactionService.processTransfer(
                fromAccount.getAccountNumber(),
                toAccountNumber,
                amount,
                description
        );

        // هدایت به صفحه موفقیت
        redirectToDetail(resp, req.getContextPath(), transaction.getId(), "transfer_success");
    }

    // ========== متدهای کمکی ==========

    /**
     * اعتبارسنجی و تبدیل مبلغ
     */
    private BigDecimal validateAndParseAmount(String amountParam) {
        if (amountParam == null || amountParam.isBlank()) {
            return null;
        }
        try {
            BigDecimal amount = new BigDecimal(amountParam);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                return null;
            }
            return amount;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * بررسی دسترسی به حساب
     */
    private boolean hasAccessToAccount(HttpServletRequest req, Account account) {
        HttpSession session = req.getSession(false);
        String currentUsername = (String) session.getAttribute("username");

        @SuppressWarnings("unchecked")
        Set<UserRole> roles = (Set<UserRole>) session.getAttribute("roles");

        // Admin و Manager به همه حساب‌ها دسترسی دارن
        if (roles.contains(UserRole.ADMIN) || roles.contains(UserRole.MANAGER)) {
            return true;
        }

        // کاربر عادی فقط به حساب خودش دسترسی داره
        return account.getUser().getUsername().equals(currentUsername);
    }

    /**
     * نمایش خطا و بازگشت به فرم
     */
    private void setError(HttpServletRequest req, HttpServletResponse resp, String message)
            throws ServletException, IOException {
        req.setAttribute("error", message);
        doGet(req, resp);
    }

    /**
     * هدایت به صفحه جزئیات تراکنش
     */
    private void redirectToDetail(HttpServletResponse resp, String contextPath,
                                  Long transactionId, String message) throws IOException {
        resp.sendRedirect(contextPath + "/transactions/detail?id=" +
                transactionId + "&message=" + message);
    }
}
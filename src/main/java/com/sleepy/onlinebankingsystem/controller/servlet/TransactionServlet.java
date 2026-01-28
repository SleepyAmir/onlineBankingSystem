package com.sleepy.onlinebankingsystem.controller.servlet;

import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.entity.Card;
import com.sleepy.onlinebankingsystem.model.entity.Transaction;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.model.enums.UserRole;
import com.sleepy.onlinebankingsystem.service.AccountService;
import com.sleepy.onlinebankingsystem.service.CardService;
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
import java.util.stream.Collectors;

@Slf4j
@WebServlet("/transactions")
public class TransactionServlet extends HttpServlet {

    @Inject
    private TransactionService transactionService;

    @Inject
    private AccountService accountService;

    @Inject
    private UserService userService;

    @Inject
    private CardService cardService;

    /**
     * نمایش فرم تراکنش
     */
    @Override
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

            // ✅ استفاده از متد جدید که Account را با User لود می‌کند
            List<Account> accounts = accountService.findByUserWithUser(user);

            // ✅ Force initialize کردن Enum ها
            accounts.forEach(account -> {
                account.getType(); // این باعث می‌شود Hibernate Enum را لود کند
            });

            req.setAttribute("accounts", accounts);

            // ✅ دریافت کارت‌های فعال با JOIN FETCH
            List<Card> activeCards = cardService.findByUserWithAccountAndUser(user.getId())
                    .stream()
                    .filter(Card::isActive)
                    .collect(Collectors.toList());

            req.setAttribute("activeCards", activeCards);

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

        // ✅ دریافت شماره حساب با User
        Account account = accountService.findByIdWithUser(Long.parseLong(accountId))
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

        // ✅ دریافت شماره حساب با User
        Account account = accountService.findByIdWithUser(Long.parseLong(accountId))
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
     * انتقال وجه با شماره کارت
     */
    private void handleTransfer(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {

        String fromCardId = req.getParameter("fromCardId");
        String toCardNumber = req.getParameter("toCardNumber");
        String amountParam = req.getParameter("amount");
        String description = req.getParameter("description");

        // اعتبارسنجی ورودی
        if (fromCardId == null || fromCardId.isBlank()) {
            setError(req, resp, "انتخاب کارت مبدأ الزامی است");
            return;
        }
        if (toCardNumber == null || toCardNumber.isBlank()) {
            setError(req, resp, "شماره کارت مقصد الزامی است");
            return;
        }

        // اعتبارسنجی فرمت شماره کارت
        if (!toCardNumber.matches("\\d{16}")) {
            setError(req, resp, "شماره کارت باید 16 رقم باشد");
            return;
        }

        BigDecimal amount = validateAndParseAmount(amountParam);
        if (amount == null) {
            setError(req, resp, "مبلغ نامعتبر است");
            return;
        }

        // ✅ دریافت کارت مبدأ با JOIN FETCH
        Card fromCard = cardService.findByIdWithAccount(Long.parseLong(fromCardId))
                .orElseThrow(() -> new IllegalArgumentException("کارت مبدأ یافت نشد"));

        // بررسی دسترسی
        HttpSession session = req.getSession(false);
        String currentUsername = (String) session.getAttribute("username");

        if (!fromCard.getAccount().getUser().getUsername().equals(currentUsername)) {
            setError(req, resp, "شما فقط می‌توانید از کارت خودتان انتقال دهید");
            return;
        }

        // بررسی فعال بودن کارت مبدأ
        if (!fromCard.isActive()) {
            setError(req, resp, "کارت مبدأ غیرفعال است");
            return;
        }

        // فراخوانی Service
        Transaction transaction = transactionService.processCardTransfer(
                fromCard.getCardNumber(),
                toCardNumber,
                amount,
                description
        );

        // هدایت به صفحه موفقیت
        redirectToDetail(resp, req.getContextPath(), transaction.getId(), "transfer_success");
    }

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

        if (roles.contains(UserRole.ADMIN) || roles.contains(UserRole.MANAGER)) {
            return true;
        }

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
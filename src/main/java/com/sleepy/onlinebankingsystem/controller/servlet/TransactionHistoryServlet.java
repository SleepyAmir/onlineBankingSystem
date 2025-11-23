// ✅ TransactionHistoryServlet.java

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
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@WebServlet("/transactions/history")
public class TransactionHistoryServlet extends HttpServlet {

    @Inject
    private TransactionService transactionService;

    @Inject
    private AccountService accountService;

    @Inject
    private UserService userService;

    private static final int PAGE_SIZE = 20;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            HttpSession session = req.getSession(false);
            String currentUsername = (String) session.getAttribute("username");

            @SuppressWarnings("unchecked")
            Set<UserRole> userRoles = (Set<UserRole>) session.getAttribute("roles");

            // 1️⃣ دریافت شماره صفحه
            String pageParam = req.getParameter("page");
            int page = 0;

            if (pageParam != null) {
                try {
                    page = Integer.parseInt(pageParam);
                    if (page < 0) page = 0;
                } catch (NumberFormatException e) {
                    log.warn("Invalid page parameter: {}", pageParam);
                    page = 0;
                }
            }

            // 2️⃣ دریافت فیلترها
            String accountIdParam = req.getParameter("accountId");
            String startDateParam = req.getParameter("startDate");
            String endDateParam = req.getParameter("endDate");

            List<Transaction> transactions;

            // 3️⃣ اگر Admin یا Manager است، همه تراکنش‌ها را نمایش بده
            if (userRoles.contains(UserRole.ADMIN) || userRoles.contains(UserRole.MANAGER)) {

                if (startDateParam != null && endDateParam != null &&
                        !startDateParam.isBlank() && !endDateParam.isBlank()) {
                    // ✅ فیلتر بر اساس بازه زمانی با JOIN FETCH
                    LocalDateTime startDate = LocalDateTime.parse(startDateParam + "T00:00:00");
                    LocalDateTime endDate = LocalDateTime.parse(endDateParam + "T23:59:59");
                    transactions = transactionService.findByDateRangeWithAccounts(startDate, endDate);

                } else if (accountIdParam != null && !accountIdParam.isBlank()) {
                    // ✅ فیلتر بر اساس حساب خاص با JOIN FETCH
                    Long accountId = Long.parseLong(accountIdParam);
                    Optional<Account> accountOpt = accountService.findById(accountId);

                    if (accountOpt.isPresent()) {
                        transactions = transactionService.findByAccountWithAccounts(accountOpt.get());
                    } else {
                        transactions = transactionService.findAllWithAccounts(page, PAGE_SIZE);
                    }
                } else {
                    // ✅ همه تراکنش‌ها با JOIN FETCH
                    transactions = transactionService.findAllWithAccounts(page, PAGE_SIZE);
                }

            } else {
                // 4️⃣ کاربر عادی فقط تراکنش‌های خودش را می‌بیند
                Optional<User> userOpt = userService.findByUsername(currentUsername);

                if (userOpt.isEmpty()) {
                    resp.sendRedirect(req.getContextPath() + "/auth/login?error=user_not_found");
                    return;
                }

                User user = userOpt.get();

                if (accountIdParam != null && !accountIdParam.isBlank()) {
                    // ✅ فیلتر بر اساس حساب خاص کاربر با JOIN FETCH
                    Long accountId = Long.parseLong(accountIdParam);
                    Optional<Account> accountOpt = accountService.findById(accountId);

                    if (accountOpt.isPresent() &&
                            accountOpt.get().getUser().getId().equals(user.getId())) {
                        transactions = transactionService.findByAccountWithAccounts(accountOpt.get());
                    } else {
                        transactions = transactionService.findByUserWithAccounts(user);
                    }
                } else {
                    // ✅ همه تراکنش‌های کاربر با JOIN FETCH
                    transactions = transactionService.findByUserWithAccounts(user);
                }

                // دریافت حساب‌های کاربر برای فیلتر
                List<Account> userAccounts = accountService.findByUser(user);
                req.setAttribute("userAccounts", userAccounts);
            }

            // 5️⃣ ارسال اطلاعات به JSP
            req.setAttribute("transactions", transactions);
            req.setAttribute("currentPage", page);
            req.setAttribute("pageSize", PAGE_SIZE);

            log.info("Fetched {} transactions for user: {}", transactions.size(), currentUsername);

            // 6️⃣ نمایش JSP
            req.getRequestDispatcher("/views/transactions/history.jsp").forward(req, resp);

        } catch (Exception e) {
            log.error("Error fetching transaction history", e);
            req.setAttribute("error", "خطا در دریافت تاریخچه تراکنش‌ها: " + e.getMessage());
            req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
        }
    }
}
package com.sleepy.onlinebankingsystem.controller.servlet;

import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.entity.Loan;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.model.enums.AccountStatus;
import com.sleepy.onlinebankingsystem.service.AccountService;
import com.sleepy.onlinebankingsystem.service.LoanService;
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
import java.util.stream.Collectors;

@Slf4j
@WebServlet("/loans/apply")
public class LoanApplicationServlet extends HttpServlet {

    @Inject
    private LoanService loanService;

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

            // دریافت حساب‌های کاربر
            Optional<User> userOpt = userService.findByUsername(currentUsername);
            if (userOpt.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/auth/login?error=user_not_found");
                return;
            }

            User user = userOpt.get();
            List<Account> userAccounts = accountService.findByUser(user);

            // فیلتر کردن حساب‌های فعال
            List<Account> activeAccounts = userAccounts.stream()
                    .filter(acc -> acc.getStatus() == AccountStatus.ACTIVE)
                    .collect(Collectors.toList());

            if (activeAccounts.isEmpty()) {
                req.setAttribute("error", "شما حساب فعالی برای دریافت وام ندارید");
                req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
                return;
            }

            req.setAttribute("accounts", activeAccounts);
            req.getRequestDispatcher("/views/loans/apply.jsp").forward(req, resp);

        } catch (Exception e) {
            log.error("Error loading loan application form", e);
            req.setAttribute("error", "خطا در بارگذاری فرم: " + e.getMessage());
            req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
        }
    }

// در LoanApplicationServlet.java - متد doPost را اصلاح کنید:

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            HttpSession session = req.getSession(false);
            String currentUsername = (String) session.getAttribute("username");

            // دریافت پارامترها
            String accountIdParam = req.getParameter("accountId");
            String principalParam = req.getParameter("principal");
            String interestRateParam = req.getParameter("interestRate");
            String durationParam = req.getParameter("duration");

            // اعتبارسنجی ساده
            if (accountIdParam == null || accountIdParam.isBlank()) {
                setError(req, resp, "انتخاب حساب الزامی است");
                return;
            }

            Long accountId = Long.parseLong(accountIdParam);
            BigDecimal principal = new BigDecimal(principalParam);
            BigDecimal interestRate = new BigDecimal(interestRateParam);
            Integer duration = Integer.parseInt(durationParam);

            // ✅ استفاده از findByIdWithUser به جای findById
            Account account = accountService.findByIdWithUser(accountId)
                    .orElseThrow(() -> new IllegalArgumentException("حساب یافت نشد"));

            // ✅ حالا می‌توانیم به User دسترسی داشته باشیم
            if (!account.getUser().getUsername().equals(currentUsername)) {
                setError(req, resp, "شما فقط می‌توانید برای حساب خودتان درخواست وام دهید");
                return;
            }

            // فراخوانی Service
            Loan loan = loanService.applyForLoan(
                    account.getAccountNumber(),
                    principal,
                    interestRate,
                    duration
            );

            log.info("Loan application submitted: {} by user: {}",
                    loan.getLoanNumber(), currentUsername);

            // هدایت به صفحه جزئیات
            resp.sendRedirect(req.getContextPath() + "/loans/detail?id=" +
                    loan.getId() + "&message=application_submitted");

        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("Validation/Business error in loan application: {}", e.getMessage());
            setError(req, resp, e.getMessage());
        } catch (Exception e) {
            log.error("Error processing loan application", e);
            setError(req, resp, "خطا در ثبت درخواست وام: " + e.getMessage());
        }
    }
    private void setError(HttpServletRequest req, HttpServletResponse resp, String message)
            throws ServletException, IOException {
        req.setAttribute("error", message);
        doGet(req, resp);
    }
}
package com.sleepy.onlinebankingsystem.controller.servlet;

import com.sleepy.onlinebankingsystem.model.entity.*;
import com.sleepy.onlinebankingsystem.model.enums.LoanStatus;
import com.sleepy.onlinebankingsystem.service.*;
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
import java.util.stream.Collectors;

@Slf4j
@WebServlet("/customer/dashboard")
public class CustomerDashboardServlet extends HttpServlet {

    @Inject
    private UserService userService;

    @Inject
    private AccountService accountService;

    @Inject
    private TransactionService transactionService;

    @Inject
    private LoanService loanService;

    @Inject
    private CardService cardService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        try {
            HttpSession session = req.getSession(false);
            String username = (String) session.getAttribute("username");

            log.info("Customer dashboard accessed by: {}", username);

            // 1️⃣ دریافت اطلاعات کاربر
            Optional<User> userOpt = userService.findByUsername(username);
            
            if (userOpt.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/auth/login?error=user_not_found");
                return;
            }

            User user = userOpt.get();

            // 2️⃣ حساب‌های کاربر
            List<Account> userAccounts = accountService.findByUser(user);
            
            // محاسبه کل موجودی
            BigDecimal totalBalance = userAccounts.stream()
                    .map(Account::getBalance)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // حساب‌های فعال
            long activeAccountCount = userAccounts.stream()
                    .filter(acc -> acc.getStatus() == com.sleepy.onlinebankingsystem.model.enums.AccountStatus.ACTIVE)
                    .count();

            // 3️⃣ تراکنش‌های کاربر
            List<Transaction> userTransactions = transactionService.findByUser(user);
            
            // آخرین 10 تراکنش
            List<Transaction> recentTransactions = userTransactions.stream()
                    .sorted((t1, t2) -> t2.getTransactionDate().compareTo(t1.getTransactionDate()))
                    .limit(10)
                    .collect(Collectors.toList());

            // تراکنش‌های این ماه
            LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime now = LocalDateTime.now();
            List<Transaction> monthTransactions = transactionService.findByDateRange(monthStart, now).stream()
                    .filter(t -> (t.getFromAccount() != null && t.getFromAccount().getUser().getId().equals(user.getId())) ||
                                 (t.getToAccount() != null && t.getToAccount().getUser().getId().equals(user.getId())))
                    .collect(Collectors.toList());

            // حجم تراکنش‌های این ماه
            BigDecimal monthTransactionVolume = monthTransactions.stream()
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 4️⃣ وام‌های کاربر
            List<Loan> userLoans = loanService.findByUser(user);
            
            // وام‌های فعال
            List<Loan> activeLoans = userLoans.stream()
                    .filter(loan -> loan.getStatus() == LoanStatus.ACTIVE || 
                                   loan.getStatus() == LoanStatus.APPROVED)
                    .collect(Collectors.toList());

            // وام‌های در انتظار
            List<Loan> pendingLoans = userLoans.stream()
                    .filter(loan -> loan.getStatus() == LoanStatus.PENDING)
                    .collect(Collectors.toList());

            // کل بدهی وام‌ها
            BigDecimal totalLoanDebt = activeLoans.stream()
                    .map(Loan::getPrincipal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 5️⃣ کارت‌های کاربر
            List<Card> userCards = cardService.findByUser(user);
            
            // کارت‌های فعال
            List<Card> activeCards = userCards.stream()
                    .filter(Card::isActive)
                    .collect(Collectors.toList());

            // 6️⃣ خلاصه مالی
            BigDecimal netWorth = totalBalance.subtract(totalLoanDebt); // خالص دارایی

            // 7️⃣ اعلان‌ها و هشدارها
            StringBuilder notifications = new StringBuilder();
            
            // هشدار برای وام‌های در انتظار
            if (!pendingLoans.isEmpty()) {
                notifications.append("شما ").append(pendingLoans.size())
                           .append(" درخواست وام در انتظار تأیید دارید. ");
            }

            // هشدار برای موجودی کم
            long lowBalanceAccounts = userAccounts.stream()
                    .filter(acc -> acc.getBalance().compareTo(new BigDecimal("100000")) < 0)
                    .count();
            
            if (lowBalanceAccounts > 0) {
                notifications.append(lowBalanceAccounts)
                           .append(" حساب شما موجودی کمتر از 100,000 ریال دارد. ");
            }

            // هشدار برای کارت‌های منقضی شده یا نزدیک به انقضا
            long expiringCards = userCards.stream()
                    .filter(card -> card.getExpiryDate().isBefore(LocalDateTime.now().toLocalDate().plusMonths(3)))
                    .count();
            
            if (expiringCards > 0) {
                notifications.append(expiringCards)
                           .append(" کارت شما تا 3 ماه آینده منقضی می‌شود. ");
            }

            // 8️⃣ ارسال اطلاعات به JSP
            // اطلاعات کاربر
            req.setAttribute("user", user);
            req.setAttribute("fullName", user.getFirstName() + " " + user.getLastName());

            // آمار حساب‌ها
            req.setAttribute("userAccounts", userAccounts);
            req.setAttribute("totalBalance", totalBalance);
            req.setAttribute("activeAccountCount", activeAccountCount);

            // آمار تراکنش‌ها
            req.setAttribute("totalTransactions", userTransactions.size());
            req.setAttribute("recentTransactions", recentTransactions);
            req.setAttribute("monthTransactions", monthTransactions.size());
            req.setAttribute("monthTransactionVolume", monthTransactionVolume);

            // آمار وام‌ها
            req.setAttribute("totalLoans", userLoans.size());
            req.setAttribute("activeLoans", activeLoans);
            req.setAttribute("pendingLoans", pendingLoans);
            req.setAttribute("totalLoanDebt", totalLoanDebt);

            // آمار کارت‌ها
            req.setAttribute("totalCards", userCards.size());
            req.setAttribute("activeCards", activeCards);

            // خلاصه مالی
            req.setAttribute("netWorth", netWorth);

            // اعلان‌ها
            req.setAttribute("notifications", notifications.toString());

            // 9️⃣ نمایش JSP
            req.getRequestDispatcher("/views/dashboard/customer.jsp").forward(req, resp);

        } catch (Exception e) {
            log.error("Error loading customer dashboard", e);
            req.setAttribute("error", "خطا در بارگذاری داشبورد: " + e.getMessage());
            req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
        }
    }
}
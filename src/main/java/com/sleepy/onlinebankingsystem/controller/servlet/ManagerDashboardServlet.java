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
import java.util.stream.Collectors;

@Slf4j
@WebServlet("/manager/dashboard")
public class ManagerDashboardServlet extends HttpServlet {

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

            log.info("Manager dashboard accessed by: {}", username);

            // 1️⃣ آمار کلی
            List<User> activeUsers = userService.findActiveUsers();
            List<Account> allAccounts = accountService.findAll(0, 1000);
            List<Transaction> allTransactions = transactionService.findAll(0, 1000);
            List<Loan> allLoans = loanService.findAll(0, 1000);
            List<Card> activeCards = cardService.findActiveCards();

            // 2️⃣ وام‌های در انتظار تأیید (مهم‌ترین بخش برای مدیر)
            List<Loan> pendingLoans = loanService.findByStatus(LoanStatus.PENDING);
            List<Loan> recentPendingLoans = pendingLoans.stream()
                    .sorted((l1, l2) -> l2.getCreatedAt().compareTo(l1.getCreatedAt()))
                    .limit(10)
                    .collect(Collectors.toList());

            // محاسبه کل مبلغ وام‌های در انتظار
            BigDecimal totalPendingLoanAmount = pendingLoans.stream()
                    .map(Loan::getPrincipal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 3️⃣ آمار وام‌ها
            List<Loan> activeLoans = loanService.findActiveLoans();
            List<Loan> approvedLoans = loanService.findByStatus(LoanStatus.APPROVED);
            
            BigDecimal totalActiveLoanAmount = activeLoans.stream()
                    .map(Loan::getPrincipal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 4️⃣ تراکنش‌های امروز
            LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            LocalDateTime todayEnd = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
            List<Transaction> todayTransactions = transactionService.findByDateRange(todayStart, todayEnd);

            BigDecimal todayTransactionVolume = todayTransactions.stream()
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 5️⃣ آخرین تراکنش‌ها
            List<Transaction> recentTransactions = allTransactions.stream()
                    .sorted((t1, t2) -> t2.getTransactionDate().compareTo(t1.getTransactionDate()))
                    .limit(10)
                    .collect(Collectors.toList());

            // 6️⃣ آخرین کاربران ثبت‌نام شده
            List<User> recentUsers = activeUsers.stream()
                    .sorted((u1, u2) -> u2.getCreatedAt().compareTo(u1.getCreatedAt()))
                    .limit(5)
                    .collect(Collectors.toList());

            // 7️⃣ کل موجودی بانک
            BigDecimal totalBankBalance = allAccounts.stream()
                    .map(Account::getBalance)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 8️⃣ ارسال اطلاعات به JSP
            req.setAttribute("totalUsers", activeUsers.size());
            req.setAttribute("totalAccounts", allAccounts.size());
            req.setAttribute("totalTransactions", allTransactions.size());
            req.setAttribute("totalLoans", allLoans.size());
            req.setAttribute("activeCards", activeCards.size());
            req.setAttribute("totalBankBalance", totalBankBalance);

            // وام‌های در انتظار
            req.setAttribute("pendingLoansCount", pendingLoans.size());
            req.setAttribute("pendingLoans", recentPendingLoans);
            req.setAttribute("totalPendingLoanAmount", totalPendingLoanAmount);

            // وام‌های فعال
            req.setAttribute("activeLoansCount", activeLoans.size());
            req.setAttribute("approvedLoansCount", approvedLoans.size());
            req.setAttribute("totalActiveLoanAmount", totalActiveLoanAmount);

            // تراکنش‌های امروز
            req.setAttribute("todayTransactionsCount", todayTransactions.size());
            req.setAttribute("todayTransactionVolume", todayTransactionVolume);

            // آخرین فعالیت‌ها
            req.setAttribute("recentTransactions", recentTransactions);
            req.setAttribute("recentUsers", recentUsers);

            // 9️⃣ نمایش JSP
            req.getRequestDispatcher("/WEB-INF/views/dashboard/manager.jsp").forward(req, resp);

        } catch (Exception e) {
            log.error("Error loading manager dashboard", e);
            req.setAttribute("error", "خطا در بارگذاری داشبورد: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(req, resp);
        }
    }
}
package com.sleepy.onlinebankingsystem.controller.servlet;

import com.sleepy.onlinebankingsystem.model.entity.*;
import com.sleepy.onlinebankingsystem.model.enums.AccountStatus;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@WebServlet("/admin/admin-dashboard")
public class AdminDashboardServlet extends HttpServlet {

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

            log.info("Admin dashboard accessed by: {}", username);

            // آمار کاربران
            List<User> allUsers = userService.findAll(0, 1000);
            List<User> activeUsers = userService.findActiveUsers();
            int totalUsers = allUsers.size();
            int activeUserCount = activeUsers.size();
            int inactiveUserCount = totalUsers - activeUserCount;

            // آمار حساب ها
            List<Account> allAccounts = accountService.findAll(0, 1000);
            List<Account> activeAccounts = accountService.findByStatus(AccountStatus.ACTIVE);
            List<Account> frozenAccounts = accountService.findByStatus(AccountStatus.FROZEN);
            List<Account> closedAccounts = accountService.findByStatus(AccountStatus.CLOSED);

            int totalAccounts = allAccounts.size();
            int activeAccountCount = activeAccounts.size();
            int frozenAccountCount = frozenAccounts.size();
            int closedAccountCount = closedAccounts.size();

            BigDecimal totalBankBalance = allAccounts.stream()
                    .map(Account::getBalance)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // آمار تراکنش ها
            List<Transaction> allTransactions = transactionService.findAll(0, 1000);
            int totalTransactions = allTransactions.size();

            LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            LocalDateTime todayEnd = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
            List<Transaction> todayTransactions = transactionService.findByDateRange(todayStart, todayEnd);
            int todayTransactionCount = todayTransactions.size();

            BigDecimal todayTransactionVolume = todayTransactions.stream()
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // آمار وام ها
            List<Loan> allLoans = loanService.findAll(0, 1000);
            List<Loan> pendingLoans = loanService.findByStatus(LoanStatus.PENDING);
            List<Loan> approvedLoans = loanService.findByStatus(LoanStatus.APPROVED);
            List<Loan> activeLoans = loanService.findActiveLoans();
            List<Loan> rejectedLoans = loanService.findByStatus(LoanStatus.REJECTED);

            int totalLoans = allLoans.size();
            int pendingLoanCount = pendingLoans.size();
            int approvedLoanCount = approvedLoans.size();
            int activeLoanCount = activeLoans.size();
            int rejectedLoanCount = rejectedLoans.size();

            BigDecimal totalActiveLoanAmount = activeLoans.stream()
                    .map(Loan::getPrincipal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // آمار کارت ها
            List<Card> allCards = cardService.findAll(0, 1000);
            List<Card> activeCards = cardService.findActiveCards();
            int totalCards = allCards.size();
            int activeCardCount = activeCards.size();
            int blockedCardCount = totalCards - activeCardCount;

            // ✅ آخرین کاربران (با مرتب‌سازی)
            List<User> recentUsers = allUsers.stream()
                    .sorted((u1, u2) -> u2.getCreatedAt().compareTo(u1.getCreatedAt()))
                    .limit(5)
                    .collect(Collectors.toList());

            // ✅ آخرین تراکنش‌ها
            List<Transaction> recentTransactions = allTransactions.stream()
                    .sorted((t1, t2) -> t2.getTransactionDate().compareTo(t1.getTransactionDate()))
                    .limit(10)
                    .collect(Collectors.toList());

            // ✅ وام‌های در انتظار (مهم: باید User را هم داشته باشند)
            // برای جلوگیری از LazyInitializationException، User را force load می‌کنیم
            List<Loan> recentLoans = pendingLoans.stream()
                    .sorted((l1, l2) -> l2.getCreatedAt().compareTo(l1.getCreatedAt()))
                    .limit(5)
                    .peek(loan -> {
                        // Force initialize User
                        if (loan.getUser() != null) {
                            loan.getUser().getFirstName();
                            loan.getUser().getLastName();
                        }
                    })
                    .collect(Collectors.toList());

            // آمار سیستمی
            Map<String, Object> systemStats = new HashMap<>();
            systemStats.put("serverTime", LocalDateTime.now());
            systemStats.put("activeSessionCount", allUsers.size());

            // ارسال به JSP
            req.setAttribute("totalUsers", totalUsers);
            req.setAttribute("activeUsers", activeUserCount);
            req.setAttribute("inactiveUsers", inactiveUserCount);

            req.setAttribute("totalAccounts", totalAccounts);
            req.setAttribute("activeAccounts", activeAccountCount);
            req.setAttribute("frozenAccounts", frozenAccountCount);
            req.setAttribute("closedAccounts", closedAccountCount);
            req.setAttribute("totalBankBalance", totalBankBalance);

            req.setAttribute("totalTransactions", totalTransactions);
            req.setAttribute("todayTransactions", todayTransactionCount);
            req.setAttribute("todayTransactionVolume", todayTransactionVolume);

            req.setAttribute("totalLoans", totalLoans);
            req.setAttribute("pendingLoans", pendingLoanCount);
            req.setAttribute("approvedLoans", approvedLoanCount);
            req.setAttribute("activeLoans", activeLoanCount);
            req.setAttribute("rejectedLoans", rejectedLoanCount);
            req.setAttribute("totalActiveLoanAmount", totalActiveLoanAmount);

            req.setAttribute("totalCards", totalCards);
            req.setAttribute("activeCards", activeCardCount);
            req.setAttribute("blockedCards", blockedCardCount);

            req.setAttribute("recentUsers", recentUsers);
            req.setAttribute("recentTransactions", recentTransactions);
            req.setAttribute("recentLoans", recentLoans);  // ✅ وام‌های در انتظار با دکمه عملیاتی

            req.setAttribute("systemStats", systemStats);

            req.getRequestDispatcher("/views/dashboard/admin.jsp").forward(req, resp);

        } catch (Exception e) {
            log.error("Error loading admin dashboard", e);
            req.setAttribute("error", "خطا در بارگذاری داشبورد: " + e.getMessage());
            req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
        }
    }
}
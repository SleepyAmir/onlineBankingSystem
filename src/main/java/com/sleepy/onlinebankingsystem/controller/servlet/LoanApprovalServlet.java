package com.sleepy.onlinebankingsystem.controller.servlet;

import com.sleepy.onlinebankingsystem.exception.BankException;
import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.entity.Loan;
import com.sleepy.onlinebankingsystem.model.entity.Role;
import com.sleepy.onlinebankingsystem.model.enums.LoanStatus;
import com.sleepy.onlinebankingsystem.service.AccountService;
import com.sleepy.onlinebankingsystem.service.LoanService;
import jakarta.inject.Inject;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import java.time.LocalDate;
import java.util.List;

@WebServlet("/admin/loan-approval")
public class LoanApprovalServlet extends HttpServlet {

    @Inject
    private LoanService loanService;

    @Inject
    private AccountService accountService;

    // نمایش لیست وام‌های در انتظار تأیید
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !hasRole(session, "ADMIN", "MANAGER")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        List<Loan> pendingLoans = null;
        try {
            pendingLoans = loanService.findByStatus(LoanStatus.PENDING);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        request.setAttribute("pendingLoans", pendingLoans);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/admin-loan-approval.jsp");
        dispatcher.forward(request, response);
    }

    // تأیید یا رد وام
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !hasRole(session, "ADMIN", "MANAGER")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String action = request.getParameter("action");
        Long loanId = Long.parseLong(request.getParameter("loanId"));

        try {
            Loan loan = loanService.findById(loanId)
                    .orElseThrow(() -> new BankException("Loan not found"));

            if ("approve".equals(action)) {
                // انتقال پول به حساب
                Account account = loan.getAccount();
                account.setBalance(account.getBalance().add(loan.getPrincipal()));
                accountService.update(account);

                loan.setStatus(LoanStatus.ACTIVE);
                loan.setStartDate(LocalDate.now());

            } else if ("reject".equals(action)) {
                loan.setStatus(LoanStatus.REJECTED);
            }

            loanService.update(loan);
            response.sendRedirect("/jsp/admin/loan-approval?msg=Loan " + action + "ed");

        } catch (Exception e) {
            request.setAttribute("error", "Operation failed: " + e.getMessage());
            doGet(request, response);
        }
    }

    private boolean hasRole(HttpSession session, String... roles) {
        var user = (com.sleepy.onlinebankingsystem.model.entity.User) session.getAttribute("user");
        if (user == null) return false;
        return user.getRoles().stream()
                .map(Role::getRole)
                .anyMatch(role -> java.util.Arrays.asList(roles).contains(role));
    }
}
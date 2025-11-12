package com.sleepy.onlinebankingsystem.controller.servlet;

import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.entity.Loan;
import com.sleepy.onlinebankingsystem.model.enums.LoanStatus;
import com.sleepy.onlinebankingsystem.model.enums.UserRole;
import com.sleepy.onlinebankingsystem.service.AccountService;
import com.sleepy.onlinebankingsystem.service.LoanService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@Slf4j
@WebServlet("/loans/approve")
public class LoanApprovalServlet extends HttpServlet {

    @Inject
    private LoanService loanService;

    @Inject
    private AccountService accountService;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        try {
            // 1️⃣ بررسی دسترسی (فقط Admin و Manager)
            HttpSession session = req.getSession(false);
            
            @SuppressWarnings("unchecked")
            Set<UserRole> userRoles = (Set<UserRole>) session.getAttribute("roles");

            if (!userRoles.contains(UserRole.ADMIN) && !userRoles.contains(UserRole.MANAGER)) {
                log.warn("Unauthorized loan approval attempt");
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "فقط مدیر می‌تواند وام را تأیید کند");
                return;
            }

            // 2️⃣ دریافت ID وام
            String idParam = req.getParameter("id");
            
            if (idParam == null || idParam.isBlank()) {
                resp.sendRedirect(req.getContextPath() + "/loans/list?error=missing_id");
                return;
            }

            Long loanId = Long.parseLong(idParam);

            // 3️⃣ پیدا کردن وام
            Optional<Loan> loanOpt = loanService.findById(loanId);
            
            if (loanOpt.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/loans/list?error=not_found");
                return;
            }

            Loan loan = loanOpt.get();

            // 4️⃣ بررسی وضعیت وام (باید PENDING باشد)
            if (loan.getStatus() != LoanStatus.PENDING) {
                log.warn("Attempt to approve non-pending loan: {}", loan.getLoanNumber());
                resp.sendRedirect(req.getContextPath() + "/loans/detail?id=" + 
                        loanId + "&error=not_pending");
                return;
            }

            // 5️⃣ تغییر وضعیت وام به APPROVED
            loan.setStatus(LoanStatus.APPROVED);
            loanService.update(loan);

            // 6️⃣ واریز مبلغ وام به حساب
            Account account = loan.getAccount();
            account.setBalance(account.getBalance().add(loan.getPrincipal()));
            accountService.update(account);

            log.info("Loan approved: {} for user: {} by manager: {}", 
                    loan.getLoanNumber(), loan.getUser().getUsername(), 
                    session.getAttribute("username"));

            // 7️⃣ هدایت به صفحه جزئیات
            resp.sendRedirect(req.getContextPath() + "/loans/detail?id=" + 
                    loanId + "&message=approved");

        } catch (Exception e) {
            log.error("Error approving loan", e);
            resp.sendRedirect(req.getContextPath() + "/loans/list?error=approval_failed");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "از متد POST استفاده کنید");
    }
}
package com.sleepy.onlinebankingsystem.controller.servlet;

import com.sleepy.onlinebankingsystem.model.entity.Loan;
import com.sleepy.onlinebankingsystem.model.enums.UserRole;
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
import java.util.Set;

@Slf4j
@WebServlet("/loans/reject")
public class LoanRejectServlet extends HttpServlet {

    @Inject
    private LoanService loanService;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            // بررسی دسترسی (فقط Admin و Manager)
            HttpSession session = req.getSession(false);
            @SuppressWarnings("unchecked")
            Set<UserRole> userRoles = (Set<UserRole>) session.getAttribute("roles");

            if (!userRoles.contains(UserRole.ADMIN) && !userRoles.contains(UserRole.MANAGER)) {
                log.warn("Unauthorized loan rejection attempt");
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "فقط مدیر می‌تواند وام را رد کند");
                return;
            }

            // دریافت ID وام
            String idParam = req.getParameter("id");
            if (idParam == null || idParam.isBlank()) {
                resp.sendRedirect(req.getContextPath() + "/loans/list?error=missing_id");
                return;
            }

            Long loanId = Long.parseLong(idParam);

            // فراخوانی Service
            Loan loan = loanService.rejectLoan(loanId);

            log.info("Loan rejected: {} by manager: {}",
                    loan.getLoanNumber(), session.getAttribute("username"));

            // هدایت به صفحه جزئیات
            resp.sendRedirect(req.getContextPath() + "/loans/detail?id=" +
                    loanId + "&message=rejected");

        } catch (IllegalArgumentException e) {
            log.warn("Validation error in loan rejection: {}", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/loans/list?error=not_found");
        } catch (IllegalStateException e) {
            log.warn("Business error in loan rejection: {}", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/loans/list?error=" + e.getMessage());
        } catch (Exception e) {
            log.error("Error rejecting loan", e);
            resp.sendRedirect(req.getContextPath() + "/loans/list?error=rejection_failed");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "از متد POST استفاده کنید");
    }
}
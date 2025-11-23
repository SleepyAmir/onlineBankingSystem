// ✅ LoanPaymentServlet.java

package com.sleepy.onlinebankingsystem.controller.servlet;

import com.sleepy.onlinebankingsystem.model.entity.Loan;
import com.sleepy.onlinebankingsystem.service.LoanCalculationService;
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
import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@WebServlet("/loans/payment")
public class LoanPaymentServlet extends HttpServlet {

    @Inject
    private LoanService loanService;

    // ✅ Inject کردن Service جدید
    @Inject
    private LoanCalculationService loanCalculationService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            String idParam = req.getParameter("id");
            if (idParam == null || idParam.isBlank()) {
                resp.sendRedirect(req.getContextPath() + "/loans/list?error=missing_id");
                return;
            }

            Long loanId = Long.parseLong(idParam);
            Optional<Loan> loanOpt = loanService.findByIdForPayment(loanId);

            if (loanOpt.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/loans/list?error=not_found");
                return;
            }

            Loan loan = loanOpt.get();

            HttpSession session = req.getSession(false);
            String currentUsername = (String) session.getAttribute("username");

            if (!loan.getUser().getUsername().equals(currentUsername)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "دسترسی غیرمجاز");
                return;
            }

            // ✅ محاسبات از طریق Service
            BigDecimal remainingBalance = loanCalculationService.calculateRemainingBalance(loan);
            BigDecimal paidAmount = loanCalculationService.calculatePaidAmount(loan);
            int paymentProgress = loanCalculationService.calculatePaymentProgress(loan);
            int remainingInstallments = loanCalculationService.calculateRemainingInstallments(loan);
            int paidInstallments = loanCalculationService.calculatePaidInstallments(loan);

            req.setAttribute("loan", loan);
            req.setAttribute("account", loan.getAccount());
            req.setAttribute("remainingBalance", remainingBalance);
            req.setAttribute("paidAmount", paidAmount);
            req.setAttribute("paymentProgress", paymentProgress);
            req.setAttribute("remainingInstallments", remainingInstallments);
            req.setAttribute("paidInstallments", paidInstallments);

            req.getRequestDispatcher("/views/loans/payment.jsp").forward(req, resp);

        } catch (Exception e) {
            log.error("Error loading loan payment form", e);
            req.setAttribute("error", "خطا در بارگذاری فرم: " + e.getMessage());
            req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            HttpSession session = req.getSession(false);
            String currentUsername = (String) session.getAttribute("username");

            String loanIdParam = req.getParameter("loanId");
            String paymentAmountParam = req.getParameter("paymentAmount");

            if (loanIdParam == null || loanIdParam.isBlank()) {
                setError(req, resp, "شناسه وام الزامی است");
                return;
            }

            Long loanId = Long.parseLong(loanIdParam);
            BigDecimal paymentAmount = null;

            if (paymentAmountParam != null && !paymentAmountParam.isBlank()) {
                paymentAmount = new BigDecimal(paymentAmountParam);
            }

            Optional<Loan> loanOpt = loanService.findByIdForPayment(loanId);

            if (loanOpt.isEmpty()) {
                setError(req, resp, "وام یافت نشد");
                return;
            }

            Loan loan = loanOpt.get();

            if (!loan.getUser().getUsername().equals(currentUsername)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "دسترسی غیرمجاز");
                return;
            }

            loanService.payLoanInstallment(loanId, paymentAmount);

            log.info("Loan installment paid: {} by user: {}",
                    loan.getLoanNumber(), currentUsername);

            resp.sendRedirect(req.getContextPath() + "/loans/detail?id=" +
                    loanId + "&message=payment_success");

        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("Validation/Business error in loan payment: {}", e.getMessage());
            setError(req, resp, e.getMessage());
        } catch (Exception e) {
            log.error("Error processing loan payment", e);
            setError(req, resp, "خطا در پردازش پرداخت: " + e.getMessage());
        }
    }

    private void setError(HttpServletRequest req, HttpServletResponse resp, String message)
            throws ServletException, IOException {
        req.setAttribute("error", message);
        doGet(req, resp);
    }
}
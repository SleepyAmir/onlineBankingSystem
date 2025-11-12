package com.sleepy.onlinebankingsystem.controller.servlet;

import com.sleepy.onlinebankingsystem.model.entity.Transaction;
import com.sleepy.onlinebankingsystem.model.enums.UserRole;
import com.sleepy.onlinebankingsystem.service.TransactionService;
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
import java.util.Optional;
import java.util.Set;

@Slf4j
@WebServlet("/transactions/detail")
public class TransactionDetailServlet extends HttpServlet {

    @Inject
    private TransactionService transactionService;

    @Override
    @Transactional
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        try {
            // 1️⃣ دریافت شناسه تراکنش
            String idParam = req.getParameter("id");
            String transactionIdParam = req.getParameter("transactionId");

            if ((idParam == null || idParam.isBlank()) && 
                (transactionIdParam == null || transactionIdParam.isBlank())) {
                log.warn("Transaction detail requested without identifier");
                resp.sendRedirect(req.getContextPath() + "/transactions/history?error=missing_id");
                return;
            }

            // 2️⃣ پیدا کردن تراکنش
            Optional<Transaction> transactionOpt;
            
            if (transactionIdParam != null && !transactionIdParam.isBlank()) {
                transactionOpt = transactionService.findByTransactionId(transactionIdParam);
            } else {
                Long id = Long.parseLong(idParam);
                transactionOpt = transactionService.findById(id);
            }

            if (transactionOpt.isEmpty()) {
                log.warn("Transaction not found");
                resp.sendRedirect(req.getContextPath() + "/transactions/history?error=not_found");
                return;
            }

            Transaction transaction = transactionOpt.get();

            // 3️⃣ بررسی دسترسی (کاربر فقط تراکنش‌های خودش را ببیند)
            HttpSession session = req.getSession(false);
            String currentUsername = (String) session.getAttribute("username");
            
            @SuppressWarnings("unchecked")
            Set<UserRole> userRoles = (Set<UserRole>) session.getAttribute("roles");

            if (!userRoles.contains(UserRole.ADMIN) && !userRoles.contains(UserRole.MANAGER)) {
                boolean hasAccess = false;

                if (transaction.getFromAccount() != null && 
                    transaction.getFromAccount().getUser().getUsername().equals(currentUsername)) {
                    hasAccess = true;
                }

                if (transaction.getToAccount() != null && 
                    transaction.getToAccount().getUser().getUsername().equals(currentUsername)) {
                    hasAccess = true;
                }

                if (!hasAccess) {
                    log.warn("Unauthorized access to transaction {} by user {}", 
                            transaction.getTransactionId(), currentUsername);
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "دسترسی غیرمجاز");
                    return;
                }
            }

            // 4️⃣ ارسال اطلاعات به JSP
            req.setAttribute("transaction", transaction);

            log.info("Fetched details for transaction: {}", transaction.getTransactionId());

            // 5️⃣ نمایش JSP
            req.getRequestDispatcher("/views/transactions/detail.jsp").forward(req, resp);

        } catch (Exception e) {
            log.error("Error fetching transaction details", e);
            req.setAttribute("error", "خطا در دریافت جزئیات تراکنش: " + e.getMessage());
            req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
        }
    }
}
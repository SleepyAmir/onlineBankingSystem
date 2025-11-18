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
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@Slf4j
@WebServlet("/loans/detail")
public class LoanDetailServlet extends HttpServlet {

    @Inject
    private LoanService loanService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        try {
            // 1️⃣ دریافت شناسه وام
            String idParam = req.getParameter("id");
            String loanNumberParam = req.getParameter("loanNumber");

            if ((idParam == null || idParam.isBlank()) && 
                (loanNumberParam == null || loanNumberParam.isBlank())) {
                log.warn("Loan detail requested without identifier");
                resp.sendRedirect(req.getContextPath() + "/loans/list?error=missing_id");
                return;
            }

            // 2️⃣ پیدا کردن وام
            Optional<Loan> loanOpt;

            // تغییر این قسمت:
            if (loanNumberParam != null && !loanNumberParam.isBlank()) {
                loanOpt = loanService.findByLoanNumberWithUserAndAccount(loanNumberParam);
            } else {
                Long loanId = Long.parseLong(idParam);
                loanOpt = loanService.findByIdWithUserAndAccount(loanId);
            }

            if (loanOpt.isEmpty()) {
                log.warn("Loan not found");
                resp.sendRedirect(req.getContextPath() + "/loans/list?error=not_found");
                return;
            }

            Loan loan = loanOpt.get();

            // 3️⃣ بررسی دسترسی (کاربر فقط وام‌های خودش را ببیند)
            HttpSession session = req.getSession(false);
            String currentUsername = (String) session.getAttribute("username");
            
            @SuppressWarnings("unchecked")
            Set<UserRole> userRoles = (Set<UserRole>) session.getAttribute("roles");

            if (!userRoles.contains(UserRole.ADMIN) && !userRoles.contains(UserRole.MANAGER)) {
                if (!loan.getUser().getUsername().equals(currentUsername)) {
                    log.warn("Unauthorized access to loan {} by user {}", 
                            loan.getLoanNumber(), currentUsername);
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "دسترسی غیرمجاز");
                    return;
                }
            }

            // 4️⃣ ارسال اطلاعات به JSP
            req.setAttribute("loan", loan);

            log.info("Fetched details for loan: {}", loan.getLoanNumber());

            // 5️⃣ نمایش JSP
            req.getRequestDispatcher("/views/loans/detail.jsp").forward(req, resp);

        } catch (Exception e) {
            log.error("Error fetching loan details", e);
            req.setAttribute("error", "خطا در دریافت جزئیات وام: " + e.getMessage());
            req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
        }
    }
}
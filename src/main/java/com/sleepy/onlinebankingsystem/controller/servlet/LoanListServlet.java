package com.sleepy.onlinebankingsystem.controller.servlet;

import com.sleepy.onlinebankingsystem.model.entity.Loan;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.model.enums.LoanStatus;
import com.sleepy.onlinebankingsystem.model.enums.UserRole;
import com.sleepy.onlinebankingsystem.service.LoanService;
import com.sleepy.onlinebankingsystem.service.UserService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@WebServlet("/loans/list")
public class LoanListServlet extends HttpServlet {

    @Inject
    private LoanService loanService;

    @Inject
    private UserService userService;

    private static final int PAGE_SIZE = 10;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        try {
            HttpSession session = req.getSession(false);
            String currentUsername = (String) session.getAttribute("username");
            
            @SuppressWarnings("unchecked")
            Set<UserRole> userRoles = (Set<UserRole>) session.getAttribute("roles");

            // 1️⃣ دریافت شماره صفحه
            String pageParam = req.getParameter("page");
            int page = 0;
            
            if (pageParam != null) {
                try {
                    page = Integer.parseInt(pageParam);
                    if (page < 0) page = 0;
                } catch (NumberFormatException e) {
                    log.warn("Invalid page parameter: {}", pageParam);
                    page = 0;
                }
            }

            // 2️⃣ دریافت فیلتر وضعیت
            String statusParam = req.getParameter("status");
            LoanStatus filterStatus = null;
            
            if (statusParam != null && !statusParam.isBlank()) {
                try {
                    filterStatus = LoanStatus.valueOf(statusParam);
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid status parameter: {}", statusParam);
                }
            }

            List<Loan> loans;

            // 3️⃣ اگر Admin یا Manager است، همه وام‌ها را نمایش بده
            if (userRoles.contains(UserRole.ADMIN) || userRoles.contains(UserRole.MANAGER)) {
                
                String userIdParam = req.getParameter("userId");
                
                if (userIdParam != null && !userIdParam.isBlank()) {
                    // وام‌های یک کاربر خاص
                    Long userId = Long.parseLong(userIdParam);
                    Optional<User> userOpt = userService.findById(userId);
                    
                    if (userOpt.isPresent()) {
                        loans = loanService.findByUser(userOpt.get());
                        req.setAttribute("selectedUser", userOpt.get());
                    } else {
                        loans = filterStatus != null ? 
                                loanService.findByStatus(filterStatus) : 
                                loanService.findAll(page, PAGE_SIZE);
                    }
                } else if (filterStatus != null) {
                    // فیلتر بر اساس وضعیت
                    loans = loanService.findByStatus(filterStatus);
                } else {
                    // همه وام‌ها
                    loans = loanService.findAll(page, PAGE_SIZE);
                }

                // ارسال لیست کاربران برای فیلتر
                req.setAttribute("users", userService.findActiveUsers());
            } else {
                // 4️⃣ کاربر عادی فقط وام‌های خودش را می‌بیند
                Optional<User> userOpt = userService.findByUsername(currentUsername);
                
                if (userOpt.isEmpty()) {
                    resp.sendRedirect(req.getContextPath() + "/auth/login?error=user_not_found");
                    return;
                }
                
                loans = loanService.findByUser(userOpt.get());

                // فیلتر بر اساس وضعیت (اختیاری)
                if (filterStatus != null) {
                    final LoanStatus status = filterStatus;
                    loans = loans.stream()
                            .filter(loan -> loan.getStatus() == status)
                            .collect(Collectors.toList());
                }
            }

            // 5️⃣ ارسال اطلاعات به JSP
            req.setAttribute("loans", loans);
            req.setAttribute("currentPage", page);
            req.setAttribute("pageSize", PAGE_SIZE);
            req.setAttribute("loanStatuses", LoanStatus.values());
            req.setAttribute("selectedStatus", filterStatus);

            log.info("Fetched {} loans for user: {}", loans.size(), currentUsername);

            // 6️⃣ نمایش JSP
            req.getRequestDispatcher("/views/loans/list.jsp").forward(req, resp);

        } catch (Exception e) {
            log.error("Error fetching loan list", e);
            req.setAttribute("error", "خطا در دریافت لیست وام‌ها: " + e.getMessage());
            req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
        }
    }
}
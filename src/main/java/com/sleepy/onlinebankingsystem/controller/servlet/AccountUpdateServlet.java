package com.sleepy.onlinebankingsystem.controller.servlet;

import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.enums.AccountStatus;
import com.sleepy.onlinebankingsystem.model.enums.AccountType;
import com.sleepy.onlinebankingsystem.model.enums.UserRole;
import com.sleepy.onlinebankingsystem.service.AccountService;
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
import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

@Slf4j
@WebServlet("/accounts/update")
public class AccountUpdateServlet extends HttpServlet {

    @Inject
    private AccountService accountService;

    @Override
    @Transactional
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        try {
            // 1️⃣ دریافت ID حساب
            String idParam = req.getParameter("id");
            
            if (idParam == null || idParam.isBlank()) {
                resp.sendRedirect(req.getContextPath() + "/accounts/list?error=missing_id");
                return;
            }

            Long accountId = Long.parseLong(idParam);

            // 2️⃣ پیدا کردن حساب
            Optional<Account> accountOpt = accountService.findById(accountId);
            
            if (accountOpt.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/accounts/list?error=not_found");
                return;
            }

            Account account = accountOpt.get();

            // 3️⃣ بررسی دسترسی
            HttpSession session = req.getSession(false);
            
            @SuppressWarnings("unchecked")
            Set<UserRole> userRoles = (Set<UserRole>) session.getAttribute("roles");

            // فقط ادمین و مدیر می‌توانند حساب را ویرایش کنند
            if (!userRoles.contains(UserRole.ADMIN) && !userRoles.contains(UserRole.MANAGER)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "دسترسی غیرمجاز");
                return;
            }

            // 4️⃣ ارسال اطلاعات به JSP
            req.setAttribute("account", account);
            req.setAttribute("accountTypes", AccountType.values());
            req.setAttribute("accountStatuses", AccountStatus.values());

            // 5️⃣ نمایش فرم ویرایش
            req.getRequestDispatcher("/views/accounts/update.jsp").forward(req, resp);

        } catch (Exception e) {
            log.error("Error loading update form", e);
            req.setAttribute("error", "خطا در بارگذاری فرم: " + e.getMessage());
            req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        try {
            // 1️⃣ دریافت ID حساب
            String idParam = req.getParameter("id");
            
            if (idParam == null || idParam.isBlank()) {
                resp.sendRedirect(req.getContextPath() + "/accounts/list?error=missing_id");
                return;
            }

            Long accountId = Long.parseLong(idParam);

            // 2️⃣ پیدا کردن حساب
            Optional<Account> accountOpt = accountService.findById(accountId);
            
            if (accountOpt.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/accounts/list?error=not_found");
                return;
            }

            Account account = accountOpt.get();

            // 3️⃣ دریافت پارامترهای فرم
            String accountTypeParam = req.getParameter("accountType");
            String accountStatusParam = req.getParameter("accountStatus");
            String balanceParam = req.getParameter("balance");

            // 4️⃣ اعتبارسنجی و به‌روزرسانی نوع حساب
            if (accountTypeParam != null && !accountTypeParam.isBlank()) {
                try {
                    AccountType accountType = AccountType.valueOf(accountTypeParam);
                    account.setType(accountType);
                } catch (IllegalArgumentException e) {
                    req.setAttribute("error", "نوع حساب نامعتبر است");
                    req.setAttribute("account", account);
                    req.setAttribute("accountTypes", AccountType.values());
                    req.setAttribute("accountStatuses", AccountStatus.values());
                    req.getRequestDispatcher("/views/accounts/update.jsp").forward(req, resp);
                    return;
                }
            }

            // 5️⃣ اعتبارسنجی و به‌روزرسانی وضعیت حساب
            if (accountStatusParam != null && !accountStatusParam.isBlank()) {
                try {
                    AccountStatus accountStatus = AccountStatus.valueOf(accountStatusParam);
                    account.setStatus(accountStatus);
                } catch (IllegalArgumentException e) {
                    req.setAttribute("error", "وضعیت حساب نامعتبر است");
                    req.setAttribute("account", account);
                    req.setAttribute("accountTypes", AccountType.values());
                    req.setAttribute("accountStatuses", AccountStatus.values());
                    req.getRequestDispatcher("/views/accounts/update.jsp").forward(req, resp);
                    return;
                }
            }

            // 6️⃣ اعتبارسنجی و به‌روزرسانی موجودی (فقط توسط ادمین)
            HttpSession session = req.getSession(false);
            
            @SuppressWarnings("unchecked")
            Set<UserRole> userRoles = (Set<UserRole>) session.getAttribute("roles");

            if (userRoles.contains(UserRole.ADMIN)) {
                if (balanceParam != null && !balanceParam.isBlank()) {
                    try {
                        BigDecimal balance = new BigDecimal(balanceParam);
                        if (balance.compareTo(BigDecimal.ZERO) < 0) {
                            req.setAttribute("error", "موجودی نمی‌تواند منفی باشد");
                            req.setAttribute("account", account);
                            req.setAttribute("accountTypes", AccountType.values());
                            req.setAttribute("accountStatuses", AccountStatus.values());
                            req.getRequestDispatcher("/views/accounts/update.jsp").forward(req, resp);
                            return;
                        }
                        account.setBalance(balance);
                    } catch (NumberFormatException e) {
                        req.setAttribute("error", "موجودی نامعتبر است");
                        req.setAttribute("account", account);
                        req.setAttribute("accountTypes", AccountType.values());
                        req.setAttribute("accountStatuses", AccountStatus.values());
                        req.getRequestDispatcher("/views/accounts/update.jsp").forward(req, resp);
                        return;
                    }
                }
            }

            // 7️⃣ ذخیره تغییرات
            accountService.update(account);

            log.info("Account updated successfully: {}", account.getAccountNumber());

            // 8️⃣ هدایت به صفحه جزئیات
            resp.sendRedirect(req.getContextPath() + "/accounts/detail?id=" + 
                    accountId + "&message=updated");

        } catch (Exception e) {
            log.error("Error updating account", e);
            req.setAttribute("error", "خطا در به‌روزرسانی حساب: " + e.getMessage());
            req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
        }
    }
}
package com.sleepy.onlinebankingsystem.controller.servlet;

import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.enums.UserRole;
import com.sleepy.onlinebankingsystem.service.AccountService;
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
import java.util.Set;

@Slf4j
@WebServlet("/accounts/delete")
public class AccountDeleteServlet extends HttpServlet {

    @Inject
    private AccountService accountService;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        try {
            // 1️⃣ بررسی دسترسی (فقط ادمین)
            HttpSession session = req.getSession(false);
            
            @SuppressWarnings("unchecked")
            Set<UserRole> userRoles = (Set<UserRole>) session.getAttribute("roles");

            if (!userRoles.contains(UserRole.ADMIN)) {
                log.warn("Unauthorized delete attempt by non-admin user");
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "فقط ادمین می‌تواند حساب را حذف کند");
                return;
            }

            // 2️⃣ دریافت ID حساب
            String idParam = req.getParameter("id");
            
            if (idParam == null || idParam.isBlank()) {
                resp.sendRedirect(req.getContextPath() + "/accounts/list?error=missing_id");
                return;
            }

            Long accountId = Long.parseLong(idParam);

            // 3️⃣ بررسی وجود حساب
            Optional<Account> accountOpt = accountService.findById(accountId);
            
            if (accountOpt.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/accounts/list?error=not_found");
                return;
            }

            Account account = accountOpt.get();

            // 4️⃣ بررسی موجودی (نباید حساب با موجودی مثبت حذف شود)
            if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
                log.warn("Attempt to delete account with positive balance: {}", 
                        account.getAccountNumber());
                resp.sendRedirect(req.getContextPath() + "/accounts/detail?id=" + 
                        accountId + "&error=has_balance");
                return;
            }

            // 5️⃣ حذف نرم (Soft Delete)
            accountService.softDelete(accountId);

            log.info("Account soft-deleted successfully: {} by admin: {}", 
                    account.getAccountNumber(), session.getAttribute("username"));

            // 6️⃣ هدایت به لیست با پیام موفقیت
            resp.sendRedirect(req.getContextPath() + "/accounts/list?message=deleted");

        } catch (Exception e) {
            log.error("Error deleting account", e);
            resp.sendRedirect(req.getContextPath() + "/accounts/list?error=delete_failed");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // حذف فقط با POST مجاز است
        resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "از متد POST استفاده کنید");
    }
}
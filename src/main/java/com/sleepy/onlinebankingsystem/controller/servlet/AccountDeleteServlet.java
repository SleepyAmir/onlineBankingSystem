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
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
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
                resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "فقط ادمین می‌تواند حساب را حذف کند");
                return;
            }

            // 2️⃣ دریافت ID
            String idParam = req.getParameter("id");
            if (idParam == null || idParam.isBlank()) {
                resp.sendRedirect(req.getContextPath() +
                        "/accounts/list?error=missing_id");
                return;
            }

            Long accountId = Long.parseLong(idParam);

            // 3️⃣ پیدا کردن حساب (برای لاگ)
            Optional<Account> accountOpt = accountService.findById(accountId);
            if (accountOpt.isEmpty()) {
                resp.sendRedirect(req.getContextPath() +
                        "/accounts/list?error=not_found");
                return;
            }

            Account account = accountOpt.get();
            String accountNumber = account.getAccountNumber();

            log.info("Attempting to delete account: {}", accountNumber);

            // 4️⃣ اعتبارسنجی برای حذف (در Service)
            accountService.validateAccountForDeletion(accountId);

            // 5️⃣ حذف نرم
            accountService.softDelete(accountId);

            log.info("Account soft-deleted: {} by admin: {}",
                    accountNumber, session.getAttribute("username"));

            // 6️⃣ هدایت با پیام موفقیت
            resp.sendRedirect(req.getContextPath() +
                    "/accounts/list?message=deleted");

        } catch (IllegalArgumentException e) {
            log.warn("Validation error in account deletion: {}", e.getMessage());
            resp.sendRedirect(req.getContextPath() +
                    "/accounts/list?error=not_found");
        } catch (IllegalStateException e) {
            log.warn("Business error in account deletion: {}", e.getMessage());
            String idParam = req.getParameter("id");
            resp.sendRedirect(req.getContextPath() + "/accounts/detail?id=" +
                    idParam + "&error=cannot_delete");
        } catch (Exception e) {
            log.error("Error deleting account", e);
            resp.sendRedirect(req.getContextPath() +
                    "/accounts/list?error=delete_failed");
        }
    }

    @Override
    @Transactional
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                "از متد POST استفاده کنید");
    }
}
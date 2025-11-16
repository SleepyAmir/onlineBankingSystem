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

/**
 * سرولت به‌روزرسانی حساب
 * تمام بیزنس لاجیک در AccountService
 */
@Slf4j
@WebServlet("/accounts/update")
public class AccountUpdateServlet extends HttpServlet {

    @Inject
    private AccountService accountService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            // 1️⃣ بررسی دسترسی
            HttpSession session = req.getSession(false);

            @SuppressWarnings("unchecked")
            Set<UserRole> userRoles = (Set<UserRole>) session.getAttribute("roles");

            if (!userRoles.contains(UserRole.ADMIN) &&
                    !userRoles.contains(UserRole.MANAGER)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "دسترسی غیرمجاز");
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

            // 3️⃣ پیدا کردن حساب
            Optional<Account> accountOpt = accountService.findById(accountId);
            if (accountOpt.isEmpty()) {
                resp.sendRedirect(req.getContextPath() +
                        "/accounts/list?error=not_found");
                return;
            }

            Account account = accountOpt.get();

            // 4️⃣ ارسال به JSP
            req.setAttribute("account", account);
            req.setAttribute("accountTypes", AccountType.values());
            req.setAttribute("accountStatuses", AccountStatus.values());

            req.getRequestDispatcher("/views/accounts/update.jsp").forward(req, resp);

        } catch (Exception e) {
            log.error("Error loading update form", e);
            setError(req, resp, "خطا در بارگذاری فرم: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            HttpSession session = req.getSession(false);

            @SuppressWarnings("unchecked")
            Set<UserRole> userRoles = (Set<UserRole>) session.getAttribute("roles");

            // 1️⃣ دریافت ID
            String idParam = req.getParameter("id");
            if (idParam == null || idParam.isBlank()) {
                resp.sendRedirect(req.getContextPath() +
                        "/accounts/list?error=missing_id");
                return;
            }

            Long accountId = Long.parseLong(idParam);

            // 2️⃣ پیدا کردن حساب
            Optional<Account> accountOpt = accountService.findById(accountId);
            if (accountOpt.isEmpty()) {
                resp.sendRedirect(req.getContextPath() +
                        "/accounts/list?error=not_found");
                return;
            }

            Account account = accountOpt.get();

            // 3️⃣ دریافت پارامترها
            String accountTypeParam = req.getParameter("accountType");
            String accountStatusParam = req.getParameter("accountStatus");
            String balanceParam = req.getParameter("balance");

            log.info("Updating account: {}", account.getAccountNumber());

            // 4️⃣ به‌روزرسانی نوع حساب
            if (accountTypeParam != null && !accountTypeParam.isBlank()) {
                try {
                    AccountType accountType = AccountType.valueOf(accountTypeParam);
                    account.setType(accountType);
                } catch (IllegalArgumentException e) {
                    setErrorWithAccount(req, resp, account, "نوع حساب نامعتبر است");
                    return;
                }
            }

            // 5️⃣ به‌روزرسانی وضعیت (از طریق Service)
            if (accountStatusParam != null && !accountStatusParam.isBlank()) {
                try {
                    AccountStatus newStatus = AccountStatus.valueOf(accountStatusParam);
                    // استفاده از Service برای تغییر وضعیت (با اعتبارسنجی)
                    account = accountService.changeAccountStatus(accountId, newStatus);
                } catch (IllegalArgumentException e) {
                    setErrorWithAccount(req, resp, account, "وضعیت حساب نامعتبر است");
                    return;
                } catch (IllegalStateException e) {
                    setErrorWithAccount(req, resp, account, e.getMessage());
                    return;
                }
            }

            // 6️⃣ به‌روزرسانی موجودی (فقط ادمین)
            if (userRoles.contains(UserRole.ADMIN)) {
                if (balanceParam != null && !balanceParam.isBlank()) {
                    try {
                        BigDecimal balance = new BigDecimal(balanceParam);
                        if (balance.compareTo(BigDecimal.ZERO) < 0) {
                            setErrorWithAccount(req, resp, account,
                                    "موجودی نمی‌تواند منفی باشد");
                            return;
                        }
                        account.setBalance(balance);
                    } catch (NumberFormatException e) {
                        setErrorWithAccount(req, resp, account, "موجودی نامعتبر است");
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
            setError(req, resp, "خطا در به‌روزرسانی: " + e.getMessage());
        }
    }

    /**
     * نمایش خطا با اطلاعات حساب
     */
    private void setErrorWithAccount(HttpServletRequest req, HttpServletResponse resp,
                                     Account account, String message)
            throws ServletException, IOException {
        req.setAttribute("error", message);
        req.setAttribute("account", account);
        req.setAttribute("accountTypes", AccountType.values());
        req.setAttribute("accountStatuses", AccountStatus.values());
        req.getRequestDispatcher("/views/accounts/update.jsp").forward(req, resp);
    }

    /**
     * نمایش خطا عمومی
     */
    private void setError(HttpServletRequest req, HttpServletResponse resp, String message)
            throws ServletException, IOException {
        req.setAttribute("error", message);
        req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
    }
}
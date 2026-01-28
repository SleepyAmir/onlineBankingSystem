package com.sleepy.onlinebankingsystem.controller.servlet;

import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.model.enums.UserRole;
import com.sleepy.onlinebankingsystem.service.AccountService;
import com.sleepy.onlinebankingsystem.service.UserService;
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
@WebServlet("/accounts/detail")
public class AccountDetailServlet extends HttpServlet {

    @Inject
    private AccountService accountService;

    @Inject
    private UserService userService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        try {
            String accountNumber = req.getParameter("accountNumber");
            String idParam = req.getParameter("id");

            if ((accountNumber == null || accountNumber.isBlank()) && 
                (idParam == null || idParam.isBlank())) {
                log.warn("Account detail requested without identifier");
                resp.sendRedirect(req.getContextPath() + "/accounts/list?error=missing_id");
                return;
            }

            Optional<Account> accountOpt;

            if (accountNumber != null && !accountNumber.isBlank()) {
                accountOpt = accountService.findByAccountNumberWithUser(accountNumber);
            } else {
                Long accountId = Long.parseLong(idParam);
                accountOpt = accountService.findByIdWithUser(accountId);
            }

            if (accountOpt.isEmpty()) {
                log.warn("Account not found");
                resp.sendRedirect(req.getContextPath() + "/accounts/list?error=not_found");
                return;
            }

            Account account = accountOpt.get();

            HttpSession session = req.getSession(false);
            String currentUsername = (String) session.getAttribute("username");
            
            @SuppressWarnings("unchecked")
            Set<UserRole> userRoles = (Set<UserRole>) session.getAttribute("roles");

            if (!userRoles.contains(UserRole.ADMIN) && !userRoles.contains(UserRole.MANAGER)) {
                if (!account.getUser().getUsername().equals(currentUsername)) {
                    log.warn("Unauthorized access to account {} by user {}", 
                            account.getAccountNumber(), currentUsername);
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "دسترسی غیرمجاز");
                    return;
                }
            }

            req.setAttribute("account", account);

            log.info("Fetched details for account: {}", account.getAccountNumber());

            req.getRequestDispatcher("/views/accounts/detail.jsp").forward(req, resp);

        } catch (Exception e) {
            log.error("Error fetching account details", e);
            req.setAttribute("error", "خطا در دریافت جزئیات حساب: " + e.getMessage());
            req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
        }
    }
}
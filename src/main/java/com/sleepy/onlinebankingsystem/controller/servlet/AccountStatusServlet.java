package com.sleepy.onlinebankingsystem.controller.servlet;

import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.enums.AccountStatus;
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
import java.util.Optional;
import java.util.Set;

@Slf4j
@WebServlet("/accounts/status")
public class AccountStatusServlet extends HttpServlet {

    @Inject
    private AccountService accountService;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        try {

            HttpSession session = req.getSession(false);
            
            @SuppressWarnings("unchecked")
            Set<UserRole> userRoles = (Set<UserRole>) session.getAttribute("roles");

            if (!userRoles.contains(UserRole.ADMIN) && !userRoles.contains(UserRole.MANAGER)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "دسترسی غیرمجاز");
                return;
            }


            String idParam = req.getParameter("id");
            String statusParam = req.getParameter("status");
            
            if (idParam == null || idParam.isBlank() || 
                statusParam == null || statusParam.isBlank()) {
                resp.sendRedirect(req.getContextPath() + "/accounts/list?error=missing_params");
                return;
            }

            Long accountId = Long.parseLong(idParam);
            AccountStatus newStatus;
            
            try {
                newStatus = AccountStatus.valueOf(statusParam);
            } catch (IllegalArgumentException e) {
                resp.sendRedirect(req.getContextPath() + "/accounts/detail?id=" + 
                        accountId + "&error=invalid_status");
                return;
            }


            Optional<Account> accountOpt = accountService.findById(accountId);
            
            if (accountOpt.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/accounts/list?error=not_found");
                return;
            }

            Account account = accountOpt.get();


            AccountStatus oldStatus = account.getStatus();
            account.setStatus(newStatus);
            
            accountService.update(account);

            log.info("Account status changed: {} from {} to {} by {}", 
                    account.getAccountNumber(), oldStatus, newStatus, 
                    session.getAttribute("username"));


            resp.sendRedirect(req.getContextPath() + "/accounts/detail?id=" + 
                    accountId + "&message=status_updated");

        } catch (Exception e) {
            log.error("Error changing account status", e);
            resp.sendRedirect(req.getContextPath() + "/accounts/list?error=status_update_failed");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "از متد POST استفاده کنید");
    }
}
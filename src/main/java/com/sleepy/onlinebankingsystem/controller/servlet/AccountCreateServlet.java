package com.sleepy.onlinebankingsystem.controller.servlet;

import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.model.enums.AccountType;
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
import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;


@Slf4j
@WebServlet("/accounts/create")
public class AccountCreateServlet extends HttpServlet {

    @Inject
    private AccountService accountService;

    @Inject
    private UserService userService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            HttpSession session = req.getSession(false);
            String currentUsername = (String) session.getAttribute("username");

            @SuppressWarnings("unchecked")
            Set<UserRole> userRoles = (Set<UserRole>) session.getAttribute("roles");

            if (userRoles.contains(UserRole.ADMIN) || userRoles.contains(UserRole.MANAGER)) {
                req.setAttribute("users", userService.findActiveUsers());
            }

            req.setAttribute("accountTypes", AccountType.values());

            req.getRequestDispatcher("/views/accounts/create.jsp").forward(req, resp);

        } catch (Exception e) {
            log.error("Error loading create account form", e);
            setError(req, resp, "خطا در بارگذاری فرم: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            HttpSession session = req.getSession(false);
            String currentUsername = (String) session.getAttribute("username");

            @SuppressWarnings("unchecked")
            Set<UserRole> userRoles = (Set<UserRole>) session.getAttribute("roles");

            String accountTypeParam = req.getParameter("accountType");
            String userIdParam = req.getParameter("userId");
            String initialBalanceParam = req.getParameter("initialBalance");

            if (accountTypeParam == null || accountTypeParam.isBlank()) {
                setError(req, resp, "نوع حساب الزامی است");
                return;
            }

            AccountType accountType;
            try {
                accountType = AccountType.valueOf(accountTypeParam);
            } catch (IllegalArgumentException e) {
                setError(req, resp, "نوع حساب نامعتبر است");
                return;
            }

            Long targetUserId;

            if (userRoles.contains(UserRole.ADMIN) || userRoles.contains(UserRole.MANAGER)) {
                if (userIdParam != null && !userIdParam.isBlank()) {
                    targetUserId = Long.parseLong(userIdParam);
                } else {
                    Optional<User> currentUserOpt = userService.findByUsername(currentUsername);
                    targetUserId = currentUserOpt.orElseThrow().getId();
                }
            } else {
                Optional<User> currentUserOpt = userService.findByUsername(currentUsername);
                targetUserId = currentUserOpt.orElseThrow().getId();
            }

            BigDecimal initialBalance = null;

            if (userRoles.contains(UserRole.ADMIN)) {
                if (initialBalanceParam != null && !initialBalanceParam.isBlank()) {
                    try {
                        initialBalance = new BigDecimal(initialBalanceParam);
                        if (initialBalance.compareTo(BigDecimal.ZERO) < 0) {
                            setError(req, resp, "موجودی اولیه نمی‌تواند منفی باشد");
                            return;
                        }
                    } catch (NumberFormatException e) {
                        setError(req, resp, "موجودی اولیه نامعتبر است");
                        return;
                    }
                }
            } else {
                initialBalance = BigDecimal.ZERO;
            }

            log.info("Creating account - User: {}, Type: {}, Balance: {}",
                    targetUserId, accountType, initialBalance);

            Account savedAccount = accountService.createAccount(
                    targetUserId,
                    accountType,
                    initialBalance
            );

            log.info("Account created successfully: {} by: {}",
                    savedAccount.getAccountNumber(), currentUsername);

            resp.sendRedirect(req.getContextPath() + "/accounts/detail?id=" +
                    savedAccount.getId() + "&message=created");

        } catch (IllegalArgumentException e) {
            log.warn("Validation error in account creation: {}", e.getMessage());
            setError(req, resp, e.getMessage());
        } catch (IllegalStateException e) {
            log.warn("Business error in account creation: {}", e.getMessage());
            setError(req, resp, e.getMessage());
        } catch (Exception e) {
            log.error("Error creating account", e);
            setError(req, resp, "خطا در ایجاد حساب: " + e.getMessage());
        }
    }

    /**
     * نمایش خطا و بازگشت به فرم
     */
    private void setError(HttpServletRequest req, HttpServletResponse resp, String message)
            throws ServletException, IOException {
        req.setAttribute("error", message);
        req.setAttribute("accountTypes", AccountType.values());

        try {
            HttpSession session = req.getSession(false);
            @SuppressWarnings("unchecked")
            Set<UserRole> userRoles = (Set<UserRole>) session.getAttribute("roles");

            if (userRoles.contains(UserRole.ADMIN) || userRoles.contains(UserRole.MANAGER)) {
                req.setAttribute("users", userService.findActiveUsers());
            }
        } catch (Exception e) {
            log.error("Error loading users for error display", e);
        }

        req.getRequestDispatcher("/views/accounts/create.jsp").forward(req, resp);
    }
}
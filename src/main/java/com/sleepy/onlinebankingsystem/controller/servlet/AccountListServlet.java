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
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@WebServlet("/accounts/list")
public class AccountListServlet extends HttpServlet {

    @Inject
    private AccountService accountService;

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

            List<Account> accounts;

            if (userRoles.contains(UserRole.ADMIN) || userRoles.contains(UserRole.MANAGER)) {
                String userIdParam = req.getParameter("userId");

                if (userIdParam != null && !userIdParam.isBlank()) {
                    Long userId = Long.parseLong(userIdParam);
                    Optional<User> userOpt = userService.findById(userId);

                    if (userOpt.isPresent()) {
                        accounts = accountService.findByUserWithUser(userOpt.get());
                        req.setAttribute("selectedUser", userOpt.get());
                    } else {
                        accounts = accountService.findAllWithUsers(page, PAGE_SIZE);
                    }
                } else {
                    accounts = accountService.findAllWithUsers(page, PAGE_SIZE);
                }
            } else {
                Optional<User> userOpt = userService.findByUsername(currentUsername);

                if (userOpt.isEmpty()) {
                    log.error("Current user not found: {}", currentUsername);
                    resp.sendRedirect(req.getContextPath() + "/auth/login?error=user_not_found");
                    return;
                }

                accounts = accountService.findByUserWithUser(userOpt.get());
            }

            req.setAttribute("accounts", accounts);
            req.setAttribute("currentPage", page);
            req.setAttribute("pageSize", PAGE_SIZE);

            log.info("Fetched {} accounts for user: {}", accounts.size(), currentUsername);

            req.getRequestDispatcher("/views/accounts/list.jsp").forward(req, resp);

        } catch (Exception e) {
            log.error("Error fetching account list", e);
            req.setAttribute("error", "خطا در دریافت لیست حساب‌ها: " + e.getMessage());
            req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
        }
    }
}
package com.sleepy.onlinebankingsystem.controller.servlet;

import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.entity.Card;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.model.enums.AccountStatus;
import com.sleepy.onlinebankingsystem.model.enums.CardType;
import com.sleepy.onlinebankingsystem.model.enums.UserRole;
import com.sleepy.onlinebankingsystem.service.AccountService;
import com.sleepy.onlinebankingsystem.service.CardService;
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
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@WebServlet("/cards/create")
public class CardCreateServlet extends HttpServlet {

    @Inject private CardService cardService;
    @Inject private AccountService accountService;
    @Inject private UserService userService;

    @Override
    @Transactional
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            HttpSession session = req.getSession(false);
            if (session == null) {
                resp.sendRedirect(req.getContextPath() + "/auth/login");
                return;
            }

            String currentUsername = (String) session.getAttribute("username");
            @SuppressWarnings("unchecked")
            Set<UserRole> userRoles = (Set<UserRole>) session.getAttribute("roles");

            Optional<User> userOpt = userService.findByUsername(currentUsername);
            if (userOpt.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/auth/login?error=user_not_found");
                return;
            }

            User user = userOpt.get();
            List<Account> userAccounts;

            // Admin/Manager: امکان انتخاب کاربر
            if (userRoles.contains(UserRole.ADMIN) || userRoles.contains(UserRole.MANAGER)) {
                String userIdParam = req.getParameter("userId");
                if (userIdParam != null && !userIdParam.isBlank()) {
                    Long userId = Long.parseLong(userIdParam);
                    Optional<User> targetUserOpt = userService.findById(userId);
                    if (targetUserOpt.isPresent()) {
                        user = targetUserOpt.get();
                    }
                }
                req.setAttribute("users", userService.findActiveUsers());
            }

            userAccounts = accountService.findByUser(user);

            List<Account> activeAccounts = userAccounts.stream()
                    .filter(acc -> acc.getStatus() == AccountStatus.ACTIVE)
                    .collect(Collectors.toList());

            if (activeAccounts.isEmpty()) {
                req.setAttribute("error", "حساب فعالی برای صدور کارت وجود ندارد");
                req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
                return;
            }

            req.setAttribute("accounts", activeAccounts);
            req.setAttribute("cardTypes", CardType.values());
            req.getRequestDispatcher("/views/cards/create.jsp").forward(req, resp);

        } catch (Exception e) {
            log.error("Error loading card creation form", e);
            req.setAttribute("error", "خطا در بارگذاری فرم: " + e.getMessage());
            req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            HttpSession session = req.getSession(false);
            if (session == null) {
                resp.sendRedirect(req.getContextPath() + "/auth/login");
                return;
            }

            String currentUsername = (String) session.getAttribute("username");
            @SuppressWarnings("unchecked")
            Set<UserRole> userRoles = (Set<UserRole>) session.getAttribute("roles");

            // اعتبارسنجی ورودی
            String accountIdParam = req.getParameter("accountId");
            String cardTypeParam = req.getParameter("cardType");

            if (accountIdParam == null || accountIdParam.isBlank()) {
                setError(req, resp, "انتخاب حساب الزامی است");
                return;
            }
            if (cardTypeParam == null || cardTypeParam.isBlank()) {
                setError(req, resp, "نوع کارت الزامی است");
                return;
            }

            Long accountId = Long.parseLong(accountIdParam);
            CardType cardType;
            try {
                cardType = CardType.valueOf(cardTypeParam);
            } catch (IllegalArgumentException e) {
                setError(req, resp, "نوع کارت نامعتبر است");
                return;
            }

            // دریافت حساب با User
            Account account = accountService.findByIdWithUser(accountId)
                    .orElseThrow(() -> new IllegalArgumentException("حساب یافت نشد"));

            User accountOwner = account.getUser();

            // بررسی دسترسی
            if (!userRoles.contains(UserRole.ADMIN) && !userRoles.contains(UserRole.MANAGER)) {
                if (!accountOwner.getUsername().equals(currentUsername)) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "دسترسی غیرمجاز");
                    return;
                }
            }

            // فراخوانی Service
            Card savedCard = cardService.issueCard(accountId, cardType);

            log.info("Card created successfully for account: {} by: {}",
                    account.getAccountNumber(), currentUsername);

            resp.sendRedirect(req.getContextPath() + "/cards/detail?id=" +
                    savedCard.getId() + "&message=card_created");

        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("Validation/Business error in card creation: {}", e.getMessage());
            setError(req, resp, e.getMessage());
        } catch (Exception e) {
            log.error("Error creating card", e);
            setError(req, resp, "خطا در صدور کارت: " + e.getMessage());
        }
    }

    private void setError(HttpServletRequest req, HttpServletResponse resp, String message)
            throws ServletException, IOException {
        req.setAttribute("error", message);
        doGet(req, resp);
    }
}
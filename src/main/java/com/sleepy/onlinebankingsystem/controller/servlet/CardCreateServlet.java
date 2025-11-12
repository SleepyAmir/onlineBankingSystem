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
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDate;
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

            // --- اعتبارسنجی ورودی ---
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

            // --- دریافت حساب با User لود شده (EAGER) ---
            Optional<Account> accountOpt = accountService.findByIdWithUser(accountId);
            if (accountOpt.isEmpty()) {
                setError(req, resp, "حساب یافت نشد");
                return;
            }

            Account account = accountOpt.get();
            User accountOwner = account.getUser(); // حالا بدون خطا لود میشه

            log.info("Creating card for user: {}", accountOwner.getUsername());

            // --- بررسی دسترسی ---
            if (!userRoles.contains(UserRole.ADMIN) && !userRoles.contains(UserRole.MANAGER)) {
                if (!accountOwner.getUsername().equals(currentUsername)) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "دسترسی غیرمجاز");
                    return;
                }
            }

            // --- بررسی وضعیت حساب ---
            if (account.getStatus() != AccountStatus.ACTIVE) {
                setError(req, resp, "حساب باید فعال باشد");
                return;
            }

            // --- تولید اطلاعات کارت ---
            String cardNumber = generateCardNumber();
            String cvv = generateCVV();
            LocalDate expiryDate = LocalDate.now().plusYears(3);

            // --- ساخت و ذخیره کارت ---
            Card newCard = Card.builder()
                    .account(account)
                    .cardNumber(cardNumber)
                    .cvv(cvv)
                    .expiryDate(expiryDate)
                    .type(cardType)
                    .active(true)
                    .build();

            Card savedCard = cardService.save(newCard);

            log.info("Card created successfully: {} for account: {} by: {}",
                    maskCardNumber(cardNumber), account.getAccountNumber(), currentUsername);

            resp.sendRedirect(req.getContextPath() + "/cards/detail?id=" + savedCard.getId() + "&message=card_created");

        } catch (Exception e) {
            log.error("Error creating card", e);
            setError(req, resp, "خطا در صدور کارت: " + e.getMessage());
        }
    }

    // --- متد کمکی برای خطاها ---
    private void setError(HttpServletRequest req, HttpServletResponse resp, String message)
            throws ServletException, IOException {
        req.setAttribute("error", message);
        doGet(req, resp);
    }

    // --- متدهای تولید شماره ---
    private String generateCardNumber() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder("6037");
        for (int i = 0; i < 12; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private String generateCVV() {
        SecureRandom random = new SecureRandom();
        return String.format("%03d", 100 + random.nextInt(900));
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) return "****";
        return "************" + cardNumber.substring(cardNumber.length() - 4);
    }
}
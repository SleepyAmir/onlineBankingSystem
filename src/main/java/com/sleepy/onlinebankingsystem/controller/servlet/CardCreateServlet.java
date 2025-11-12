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

    @Inject
    private CardService cardService;

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

            // 1️⃣ دریافت حساب‌های کاربر
            Optional<User> userOpt = userService.findByUsername(currentUsername);
            
            if (userOpt.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/auth/login?error=user_not_found");
                return;
            }

            User user = userOpt.get();
            List<Account> userAccounts;

            // 2️⃣ اگر Admin یا Manager است، می‌تواند برای هر کاربری کارت صادر کند
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

            // فیلتر کردن حساب‌های فعال
            List<Account> activeAccounts = userAccounts.stream()
                    .filter(acc -> acc.getStatus() == AccountStatus.ACTIVE)
                    .collect(Collectors.toList());

            if (activeAccounts.isEmpty()) {
                req.setAttribute("error", "حساب فعالی برای صدور کارت وجود ندارد");
                req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
                return;
            }

            // 3️⃣ ارسال اطلاعات به JSP
            req.setAttribute("accounts", activeAccounts);
            req.setAttribute("cardTypes", CardType.values());

            // 4️⃣ نمایش فرم صدور کارت
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
            String currentUsername = (String) session.getAttribute("username");

            // 1️⃣ دریافت پارامترهای فرم
            String accountIdParam = req.getParameter("accountId");
            String cardTypeParam = req.getParameter("cardType");

            // 2️⃣ اعتبارسنجی
            if (accountIdParam == null || accountIdParam.isBlank()) {
                req.setAttribute("error", "انتخاب حساب الزامی است");
                doGet(req, resp);
                return;
            }

            if (cardTypeParam == null || cardTypeParam.isBlank()) {
                req.setAttribute("error", "نوع کارت الزامی است");
                doGet(req, resp);
                return;
            }

            Long accountId = Long.parseLong(accountIdParam);
            CardType cardType;
            
            try {
                cardType = CardType.valueOf(cardTypeParam);
            } catch (IllegalArgumentException e) {
                req.setAttribute("error", "نوع کارت نامعتبر است");
                doGet(req, resp);
                return;
            }

            // 3️⃣ پیدا کردن حساب
            Optional<Account> accountOpt = accountService.findById(accountId);
            
            if (accountOpt.isEmpty()) {
                req.setAttribute("error", "حساب یافت نشد");
                doGet(req, resp);
                return;
            }

            Account account = accountOpt.get();

            // 4️⃣ بررسی مالکیت حساب
            @SuppressWarnings("unchecked")
            Set<UserRole> userRoles = (Set<UserRole>) session.getAttribute("roles");

            if (!userRoles.contains(UserRole.ADMIN) && !userRoles.contains(UserRole.MANAGER)) {
                if (!account.getUser().getUsername().equals(currentUsername)) {
                    req.setAttribute("error", "شما فقط می‌توانید برای حساب خودتان کارت صادر کنید");
                    doGet(req, resp);
                    return;
                }
            }

            // 5️⃣ بررسی وضعیت حساب
            if (account.getStatus() != AccountStatus.ACTIVE) {
                req.setAttribute("error", "حساب باید فعال باشد");
                doGet(req, resp);
                return;
            }

            // 6️⃣ تولید شماره کارت و CVV
            String cardNumber = generateCardNumber();
            String cvv = generateCVV();

            // 7️⃣ تعیین تاریخ انقضا (3 سال از امروز)
            LocalDate expiryDate = LocalDate.now().plusYears(3);

            // 8️⃣ ساخت کارت جدید
            Card newCard = Card.builder()
                    .account(account)
                    .cardNumber(cardNumber)
                    .cvv(cvv)
                    .expiryDate(expiryDate)
                    .type(cardType)
                    .active(true)
                    .build();

            // 9️⃣ ذخیره کارت
            Card savedCard = cardService.save(newCard);

            log.info("Card created successfully: {} for account: {} by: {}", 
                    maskCardNumber(cardNumber), account.getAccountNumber(), currentUsername);

            // 🔟 هدایت به صفحه جزئیات
            resp.sendRedirect(req.getContextPath() + "/cards/detail?id=" + 
                    savedCard.getId() + "&message=card_created");

        } catch (Exception e) {
            log.error("Error creating card", e);
            req.setAttribute("error", "خطا در صدور کارت: " + e.getMessage());
            doGet(req, resp);
        }
    }

    /**
     * تولید شماره کارت 16 رقمی یکتا
     */
    private String generateCardNumber() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(16);
        
        // 4 رقم اول (BIN - Bank Identification Number)
        sb.append("6037"); // کد بانک فرضی
        
        // 12 رقم بعدی
        for (int i = 0; i < 12; i++) {
            sb.append(random.nextInt(10));
        }
        
        return sb.toString();
    }

    /**
     * تولید CVV سه رقمی
     */
    private String generateCVV() {
        SecureRandom random = new SecureRandom();
        int cvv = 100 + random.nextInt(900); // بین 100 تا 999
        return String.valueOf(cvv);
    }

    /**
     * پنهان کردن شماره کارت (نمایش 4 رقم آخر)
     */
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        return "************" + cardNumber.substring(cardNumber.length() - 4);
    }
}
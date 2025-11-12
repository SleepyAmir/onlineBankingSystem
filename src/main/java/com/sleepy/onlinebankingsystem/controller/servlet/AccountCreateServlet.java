package com.sleepy.onlinebankingsystem.controller.servlet;

import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.model.enums.AccountStatus;
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
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.SecureRandom;
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
            
            @SuppressWarnings("unchecked")
            Set<UserRole> userRoles = (Set<UserRole>) session.getAttribute("roles");

            // 1️⃣ اگر ادمین یا مدیر است، لیست کاربران را ارسال کن
            if (userRoles.contains(UserRole.ADMIN) || userRoles.contains(UserRole.MANAGER)) {
                // ادمین می‌تواند برای هر کاربری حساب بسازد
                req.setAttribute("users", userService.findActiveUsers());
            }

            // 2️⃣ ارسال انواع حساب
            req.setAttribute("accountTypes", AccountType.values());

            // 3️⃣ نمایش فرم ایجاد حساب
            req.getRequestDispatcher("/WEB-INF/views/accounts/create.jsp").forward(req, resp);

        } catch (Exception e) {
            log.error("Error loading create account form", e);
            req.setAttribute("error", "خطا در بارگذاری فرم: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(req, resp);
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

            // 1️⃣ دریافت پارامترهای فرم
            String accountTypeParam = req.getParameter("accountType");
            String userIdParam = req.getParameter("userId");
            String initialBalanceParam = req.getParameter("initialBalance");

            // 2️⃣ اعتبارسنجی
            if (accountTypeParam == null || accountTypeParam.isBlank()) {
                req.setAttribute("error", "نوع حساب الزامی است");
                req.setAttribute("accountTypes", AccountType.values());
                req.getRequestDispatcher("/WEB-INF/views/accounts/create.jsp").forward(req, resp);
                return;
            }

            AccountType accountType;
            try {
                accountType = AccountType.valueOf(accountTypeParam);
            } catch (IllegalArgumentException e) {
                req.setAttribute("error", "نوع حساب نامعتبر است");
                req.setAttribute("accountTypes", AccountType.values());
                req.getRequestDispatcher("/WEB-INF/views/accounts/create.jsp").forward(req, resp);
                return;
            }

            // 3️⃣ تعیین کاربر صاحب حساب
            User accountOwner;
            
            if (userRoles.contains(UserRole.ADMIN) || userRoles.contains(UserRole.MANAGER)) {
                // ادمین/مدیر می‌تواند برای کاربر دیگری حساب بسازد
                if (userIdParam != null && !userIdParam.isBlank()) {
                    Long userId = Long.parseLong(userIdParam);
                    Optional<User> userOpt = userService.findById(userId);
                    
                    if (userOpt.isEmpty()) {
                        req.setAttribute("error", "کاربر مورد نظر یافت نشد");
                        req.setAttribute("accountTypes", AccountType.values());
                        req.setAttribute("users", userService.findActiveUsers());
                        req.getRequestDispatcher("/WEB-INF/views/accounts/create.jsp").forward(req, resp);
                        return;
                    }
                    
                    accountOwner = userOpt.get();
                } else {
                    // اگر userId ندادن، برای خودش حساب می‌سازد
                    Optional<User> userOpt = userService.findByUsername(currentUsername);
                    accountOwner = userOpt.orElseThrow();
                }
            } else {
                // کاربر عادی فقط برای خودش حساب می‌سازد
                Optional<User> userOpt = userService.findByUsername(currentUsername);
                accountOwner = userOpt.orElseThrow();
            }

            // 4️⃣ موجودی اولیه (پیش‌فرض صفر)
            BigDecimal initialBalance = BigDecimal.ZERO;
            if (initialBalanceParam != null && !initialBalanceParam.isBlank()) {
                try {
                    initialBalance = new BigDecimal(initialBalanceParam);
                    if (initialBalance.compareTo(BigDecimal.ZERO) < 0) {
                        req.setAttribute("error", "موجودی اولیه نمی‌تواند منفی باشد");
                        req.setAttribute("accountTypes", AccountType.values());
                        req.getRequestDispatcher("/WEB-INF/views/accounts/create.jsp").forward(req, resp);
                        return;
                    }
                } catch (NumberFormatException e) {
                    req.setAttribute("error", "موجودی اولیه نامعتبر است");
                    req.setAttribute("accountTypes", AccountType.values());
                    req.getRequestDispatcher("/WEB-INF/views/accounts/create.jsp").forward(req, resp);
                    return;
                }
            }

            // 5️⃣ تولید شماره حساب یکتا (16 رقمی)
            String accountNumber = generateAccountNumber();

            // 6️⃣ ساخت حساب جدید
            Account newAccount = Account.builder()
                    .user(accountOwner)
                    .accountNumber(accountNumber)
                    .type(accountType)
                    .balance(initialBalance)
                    .status(AccountStatus.ACTIVE)
                    .build();

            // 7️⃣ ذخیره حساب
            Account savedAccount = accountService.save(newAccount);

            log.info("Account created successfully: {} for user: {} by: {}", 
                    accountNumber, accountOwner.getUsername(), currentUsername);

            // 8️⃣ هدایت به صفحه جزئیات
            resp.sendRedirect(req.getContextPath() + "/accounts/detail?id=" + 
                    savedAccount.getId() + "&message=created");

        } catch (Exception e) {
            log.error("Error creating account", e);
            req.setAttribute("error", "خطا در ایجاد حساب: " + e.getMessage());
            req.setAttribute("accountTypes", AccountType.values());
            req.getRequestDispatcher("/WEB-INF/views/accounts/create.jsp").forward(req, resp);
        }
    }

    /**
     * تولید شماره حساب 16 رقمی یکتا
     */
    private String generateAccountNumber() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(16);
        
        // رقم اول نباید صفر باشد
        sb.append(random.nextInt(9) + 1);
        
        // 15 رقم بعدی
        for (int i = 0; i < 15; i++) {
            sb.append(random.nextInt(10));
        }
        
        return sb.toString();
    }
}
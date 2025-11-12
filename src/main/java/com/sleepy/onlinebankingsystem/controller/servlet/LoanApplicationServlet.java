package com.sleepy.onlinebankingsystem.controller.servlet;

import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.entity.Loan;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.model.enums.AccountStatus;
import com.sleepy.onlinebankingsystem.model.enums.LoanStatus;
import com.sleepy.onlinebankingsystem.service.AccountService;
import com.sleepy.onlinebankingsystem.service.LoanService;
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
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@WebServlet("/loans/apply")
public class LoanApplicationServlet extends HttpServlet {

    @Inject
    private LoanService loanService;

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

            // 1️⃣ دریافت حساب‌های کاربر
            Optional<User> userOpt = userService.findByUsername(currentUsername);
            
            if (userOpt.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/auth/login?error=user_not_found");
                return;
            }

            User user = userOpt.get();
            List<Account> userAccounts = accountService.findByUser(user);

            // فیلتر کردن حساب‌های فعال
            List<Account> activeAccounts = userAccounts.stream()
                    .filter(acc -> acc.getStatus() == AccountStatus.ACTIVE)
                    .collect(Collectors.toList());

            if (activeAccounts.isEmpty()) {
                req.setAttribute("error", "شما حساب فعالی برای دریافت وام ندارید");
                req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
                return;
            }

            // 2️⃣ ارسال اطلاعات به JSP
            req.setAttribute("accounts", activeAccounts);

            // 3️⃣ نمایش فرم درخواست وام
            req.getRequestDispatcher("/views/loans/apply.jsp").forward(req, resp);

        } catch (Exception e) {
            log.error("Error loading loan application form", e);
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
            String principalParam = req.getParameter("principal");
            String interestRateParam = req.getParameter("interestRate");
            String durationParam = req.getParameter("duration");

            // 2️⃣ اعتبارسنجی
            String validationError = validateInput(accountIdParam, principalParam, 
                    interestRateParam, durationParam);
            
            if (validationError != null) {
                req.setAttribute("error", validationError);
                doGet(req, resp);
                return;
            }

            // 3️⃣ Parse کردن مقادیر
            Long accountId = Long.parseLong(accountIdParam);
            BigDecimal principal = new BigDecimal(principalParam);
            BigDecimal interestRate = new BigDecimal(interestRateParam);
            Integer duration = Integer.parseInt(durationParam);

            // 4️⃣ پیدا کردن حساب
            Optional<Account> accountOpt = accountService.findById(accountId);
            
            if (accountOpt.isEmpty()) {
                req.setAttribute("error", "حساب یافت نشد");
                doGet(req, resp);
                return;
            }

            Account account = accountOpt.get();

            // 5️⃣ بررسی مالکیت حساب
            if (!account.getUser().getUsername().equals(currentUsername)) {
                req.setAttribute("error", "شما فقط می‌توانید برای حساب خودتان درخواست وام دهید");
                doGet(req, resp);
                return;
            }

            // 6️⃣ بررسی وضعیت حساب
            if (account.getStatus() != AccountStatus.ACTIVE) {
                req.setAttribute("error", "حساب باید فعال باشد");
                doGet(req, resp);
                return;
            }

            // 7️⃣ محاسبه قسط ماهانه
            BigDecimal monthlyPayment = calculateMonthlyPayment(principal, interestRate, duration);

            // 8️⃣ تولید شماره وام یکتا
            String loanNumber = generateLoanNumber();

            // 9️⃣ ساخت وام جدید
            Loan newLoan = Loan.builder()
                    .account(account)
                    .user(account.getUser())
                    .loanNumber(loanNumber)
                    .principal(principal)
                    .annualInterestRate(interestRate)
                    .durationMonths(duration)
                    .monthlyPayment(monthlyPayment)
                    .startDate(LocalDate.now())
                    .status(LoanStatus.PENDING)
                    .build();

            // 🔟 ذخیره وام
            Loan savedLoan = loanService.save(newLoan);

            log.info("Loan application submitted: {} for user: {} with principal: {}", 
                    loanNumber, currentUsername, principal);

            // 1️⃣1️⃣ هدایت به صفحه جزئیات
            resp.sendRedirect(req.getContextPath() + "/loans/detail?id=" + 
                    savedLoan.getId() + "&message=application_submitted");

        } catch (Exception e) {
            log.error("Error processing loan application", e);
            req.setAttribute("error", "خطا در ثبت درخواست وام: " + e.getMessage());
            doGet(req, resp);
        }
    }

    /**
     * اعتبارسنجی ورودی‌ها
     */
    private String validateInput(String accountId, String principal, 
                                 String interestRate, String duration) {
        
        if (accountId == null || accountId.isBlank()) {
            return "انتخاب حساب الزامی است";
        }

        if (principal == null || principal.isBlank()) {
            return "مبلغ اصل وام الزامی است";
        }

        try {
            BigDecimal principalValue = new BigDecimal(principal);
            if (principalValue.compareTo(BigDecimal.ZERO) <= 0) {
                return "مبلغ وام باید بیشتر از صفر باشد";
            }
            if (principalValue.compareTo(new BigDecimal("1000000000")) > 0) {
                return "مبلغ وام خیلی زیاد است";
            }
        } catch (NumberFormatException e) {
            return "مبلغ وام نامعتبر است";
        }

        if (interestRate == null || interestRate.isBlank()) {
            return "نرخ بهره الزامی است";
        }

        try {
            BigDecimal rate = new BigDecimal(interestRate);
            if (rate.compareTo(BigDecimal.ZERO) < 0 || rate.compareTo(new BigDecimal("100")) > 0) {
                return "نرخ بهره باید بین 0 تا 100 باشد";
            }
        } catch (NumberFormatException e) {
            return "نرخ بهره نامعتبر است";
        }

        if (duration == null || duration.isBlank()) {
            return "مدت زمان وام الزامی است";
        }

        try {
            Integer durationValue = Integer.parseInt(duration);
            if (durationValue < 1 || durationValue > 360) {
                return "مدت زمان وام باید بین 1 تا 360 ماه باشد";
            }
        } catch (NumberFormatException e) {
            return "مدت زمان نامعتبر است";
        }

        return null;
    }

    /**
     * محاسبه قسط ماهانه با فرمول ریاضی
     * PMT = P * [r(1+r)^n] / [(1+r)^n - 1]
     * P = اصل وام
     * r = نرخ بهره ماهانه (نرخ سالانه / 12)
     * n = تعداد ماه‌ها
     */
    private BigDecimal calculateMonthlyPayment(BigDecimal principal, 
                                               BigDecimal annualRate, 
                                               Integer months) {
        
        if (annualRate.compareTo(BigDecimal.ZERO) == 0) {
            // بدون بهره
            return principal.divide(new BigDecimal(months), 2, RoundingMode.HALF_UP);
        }

        // نرخ ماهانه = نرخ سالانه / 12 / 100
        BigDecimal monthlyRate = annualRate
                .divide(new BigDecimal("12"), 6, RoundingMode.HALF_UP)
                .divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP);

        // (1 + r)^n
        double onePlusR = 1 + monthlyRate.doubleValue();
        double power = Math.pow(onePlusR, months);

        // r * (1+r)^n
        BigDecimal numerator = monthlyRate.multiply(new BigDecimal(power));

        // (1+r)^n - 1
        BigDecimal denominator = new BigDecimal(power).subtract(BigDecimal.ONE);

        // P * [r(1+r)^n] / [(1+r)^n - 1]
        BigDecimal payment = principal
                .multiply(numerator)
                .divide(denominator, 2, RoundingMode.HALF_UP);

        return payment;
    }

    /**
     * تولید شماره وام یکتا
     */
    private String generateLoanNumber() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder("LOAN-");
        
        for (int i = 0; i < 12; i++) {
            sb.append(random.nextInt(10));
        }
        
        return sb.toString();
    }
}
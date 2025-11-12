package com.sleepy.onlinebankingsystem.controller.servlet;

import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.entity.Loan;
import com.sleepy.onlinebankingsystem.model.entity.Transaction;
import com.sleepy.onlinebankingsystem.model.enums.AccountStatus;
import com.sleepy.onlinebankingsystem.model.enums.LoanStatus;
import com.sleepy.onlinebankingsystem.model.enums.TransactionStatus;
import com.sleepy.onlinebankingsystem.model.enums.TransactionType;
import com.sleepy.onlinebankingsystem.service.AccountService;
import com.sleepy.onlinebankingsystem.service.LoanService;
import com.sleepy.onlinebankingsystem.service.TransactionService;
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
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@WebServlet("/loans/payment")
public class LoanPaymentServlet extends HttpServlet {

    @Inject
    private LoanService loanService;

    @Inject
    private AccountService accountService;

    @Inject
    private TransactionService transactionService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        try {
            // 1️⃣ دریافت ID وام
            String idParam = req.getParameter("id");
            
            if (idParam == null || idParam.isBlank()) {
                resp.sendRedirect(req.getContextPath() + "/loans/list?error=missing_id");
                return;
            }

            Long loanId = Long.parseLong(idParam);

            // 2️⃣ پیدا کردن وام
            Optional<Loan> loanOpt = loanService.findById(loanId);
            
            if (loanOpt.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/loans/list?error=not_found");
                return;
            }

            Loan loan = loanOpt.get();

            // 3️⃣ بررسی مالکیت
            HttpSession session = req.getSession(false);
            String currentUsername = (String) session.getAttribute("username");

            if (!loan.getUser().getUsername().equals(currentUsername)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "دسترسی غیرمجاز");
                return;
            }

            // 4️⃣ بررسی وضعیت وام
            if (loan.getStatus() != LoanStatus.APPROVED && loan.getStatus() != LoanStatus.ACTIVE) {
                req.setAttribute("error", "فقط وام‌های تأیید شده قابل پرداخت هستند");
                req.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(req, resp);
                return;
            }

            // 5️⃣ ارسال اطلاعات به JSP
            req.setAttribute("loan", loan);
            req.setAttribute("account", loan.getAccount());

            // 6️⃣ نمایش فرم پرداخت
            req.getRequestDispatcher("/WEB-INF/views/loans/payment.jsp").forward(req, resp);

        } catch (Exception e) {
            log.error("Error loading loan payment form", e);
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

            // 1️⃣ دریافت پارامترها
            String loanIdParam = req.getParameter("loanId");
            String paymentAmountParam = req.getParameter("paymentAmount");

            if (loanIdParam == null || loanIdParam.isBlank() || 
                paymentAmountParam == null || paymentAmountParam.isBlank()) {
                req.setAttribute("error", "اطلاعات ناقص است");
                doGet(req, resp);
                return;
            }

            Long loanId = Long.parseLong(loanIdParam);
            BigDecimal paymentAmount = new BigDecimal(paymentAmountParam);

            // 2️⃣ اعتبارسنجی مبلغ
            if (paymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
                req.setAttribute("error", "مبلغ پرداختی باید بیشتر از صفر باشد");
                doGet(req, resp);
                return;
            }

            // 3️⃣ پیدا کردن وام
            Optional<Loan> loanOpt = loanService.findById(loanId);
            
            if (loanOpt.isEmpty()) {
                req.setAttribute("error", "وام یافت نشد");
                doGet(req, resp);
                return;
            }

            Loan loan = loanOpt.get();

            // 4️⃣ بررسی مالکیت
            if (!loan.getUser().getUsername().equals(currentUsername)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "دسترسی غیرمجاز");
                return;
            }

            // 5️⃣ بررسی وضعیت وام
            if (loan.getStatus() != LoanStatus.APPROVED && loan.getStatus() != LoanStatus.ACTIVE) {
                req.setAttribute("error", "فقط وام‌های تأیید شده قابل پرداخت هستند");
                doGet(req, resp);
                return;
            }

            // 6️⃣ دریافت حساب
            Account account = loan.getAccount();

            // 7️⃣ بررسی وضعیت حساب
            if (account.getStatus() != AccountStatus.ACTIVE) {
                req.setAttribute("error", "حساب فعال نیست");
                doGet(req, resp);
                return;
            }

            // 8️⃣ بررسی موجودی کافی
            if (account.getBalance().compareTo(paymentAmount) < 0) {
                req.setAttribute("error", "موجودی حساب کافی نیست");
                doGet(req, resp);
                return;
            }

            // 9️⃣ کاهش موجودی حساب
            account.setBalance(account.getBalance().subtract(paymentAmount));
            accountService.update(account);

            // 🔟 تغییر وضعیت وام به ACTIVE (اگر اولین پرداخت است)
            if (loan.getStatus() == LoanStatus.APPROVED) {
                loan.setStatus(LoanStatus.ACTIVE);
                loanService.update(loan);
            }

            // 1️⃣1️⃣ ثبت تراکنش
            String transactionId = "TRX-LOAN-" + System.currentTimeMillis();
            
            Transaction transaction = Transaction.builder()
                    .transactionId(transactionId)
                    .fromAccount(account)
                    .amount(paymentAmount)
                    .type(TransactionType.LOAN_PAYMENT)
                    .transactionDate(LocalDateTime.now())
                    .status(TransactionStatus.COMPLETED)
                    .description("پرداخت قسط وام " + loan.getLoanNumber())
                    .referenceNumber(UUID.randomUUID().toString().substring(0, 10))
                    .build();

            transactionService.save(transaction);

            log.info("Loan payment successful: {} amount: {} for loan: {} by user: {}", 
                    transactionId, paymentAmount, loan.getLoanNumber(), currentUsername);

            // 1️⃣2️⃣ هدایت به صفحه موفقیت
            resp.sendRedirect(req.getContextPath() + "/loans/detail?id=" + 
                    loanId + "&message=payment_success");

        } catch (Exception e) {
            log.error("Error processing loan payment", e);
            req.setAttribute("error", "خطا در پردازش پرداخت: " + e.getMessage());
            doGet(req, resp);
        }
    }
}
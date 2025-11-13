package com.sleepy.onlinebankingsystem.controller.api;

import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.entity.Loan;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.model.enums.AccountStatus;
import com.sleepy.onlinebankingsystem.model.enums.LoanStatus;
import com.sleepy.onlinebankingsystem.service.AccountService;
import com.sleepy.onlinebankingsystem.service.LoanService;
import com.sleepy.onlinebankingsystem.service.UserService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Path("/api/loans")
@Slf4j
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LoanApi {

    @Inject
    private LoanService loanService;

    @Inject
    private AccountService accountService;

    @Inject
    private UserService userService;

    /**
     * درخواست وام جدید
     * POST /api/loans/apply
     */
    @POST
    @Path("/apply")
    public Response applyForLoan(LoanApplicationRequest request) {
        try {
            log.info("Processing loan application for account: {}", request.getAccountId());

            // اعتبارسنجی
            String validationError = validateLoanRequest(request);
            if (validationError != null) {
                return Response.status(400)
                        .entity(new ErrorResponse(validationError))
                        .build();
            }

            // پیدا کردن حساب
            Optional<Account> accountOpt = accountService.findById(request.getAccountId());
            if (accountOpt.isEmpty()) {
                return Response.status(404)
                        .entity(new ErrorResponse("حساب یافت نشد"))
                        .build();
            }

            Account account = accountOpt.get();

            // بررسی وضعیت حساب
            if (account.getStatus() != AccountStatus.ACTIVE) {
                return Response.status(400)
                        .entity(new ErrorResponse("حساب باید فعال باشد"))
                        .build();
            }

            // محاسبه قسط ماهانه
            BigDecimal monthlyPayment = calculateMonthlyPayment(
                    request.getPrincipal(),
                    request.getAnnualInterestRate(),
                    request.getDurationMonths()
            );

            // ساخت وام
            Loan loan = Loan.builder()
                    .account(account)
                    .user(account.getUser())
                    .loanNumber(generateLoanNumber())
                    .principal(request.getPrincipal())
                    .annualInterestRate(request.getAnnualInterestRate())
                    .durationMonths(request.getDurationMonths())
                    .monthlyPayment(monthlyPayment)
                    .startDate(LocalDate.now())
                    .status(LoanStatus.PENDING)
                    .build();

            Loan savedLoan = loanService.save(loan);
            log.info("Loan application submitted: {}", savedLoan.getLoanNumber());

            return Response.status(201).entity(savedLoan).build();

        } catch (Exception e) {
            log.error("Error processing loan application", e);
            return Response.status(500)
                    .entity(new ErrorResponse("خطا در ثبت درخواست وام: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * تأیید وام (توسط مدیر)
     * POST /api/loans/{id}/approve
     */
    @POST
    @Path("/{id}/approve")
    public Response approveLoan(@PathParam("id") Long id) {
        try {
            Optional<Loan> loanOpt = loanService.findById(id);

            if (loanOpt.isEmpty()) {
                return Response.status(404)
                        .entity(new ErrorResponse("وام یافت نشد"))
                        .build();
            }

            Loan loan = loanOpt.get();

            // بررسی وضعیت وام
            if (loan.getStatus() != LoanStatus.PENDING) {
                return Response.status(400)
                        .entity(new ErrorResponse("فقط وام‌های در انتظار قابل تأیید هستند"))
                        .build();
            }

            // تأیید وام
            loan.setStatus(LoanStatus.APPROVED);
            loanService.update(loan);

            // واریز مبلغ وام به حساب
            Account account = loan.getAccount();
            account.setBalance(account.getBalance().add(loan.getPrincipal()));
            accountService.update(account);

            log.info("Loan approved: {}", loan.getLoanNumber());

            return Response.ok().entity(loan).build();

        } catch (Exception e) {
            log.error("Error approving loan: {}", id, e);
            return Response.status(500)
                    .entity(new ErrorResponse("خطا در تأیید وام: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * رد وام (توسط مدیر)
     * POST /api/loans/{id}/reject
     */
    @POST
    @Path("/{id}/reject")
    public Response rejectLoan(@PathParam("id") Long id) {
        try {
            Optional<Loan> loanOpt = loanService.findById(id);

            if (loanOpt.isEmpty()) {
                return Response.status(404)
                        .entity(new ErrorResponse("وام یافت نشد"))
                        .build();
            }

            Loan loan = loanOpt.get();

            // بررسی وضعیت وام
            if (loan.getStatus() != LoanStatus.PENDING) {
                return Response.status(400)
                        .entity(new ErrorResponse("فقط وام‌های در انتظار قابل رد هستند"))
                        .build();
            }

            // رد وام
            loan.setStatus(LoanStatus.REJECTED);
            loanService.update(loan);

            log.info("Loan rejected: {}", loan.getLoanNumber());

            return Response.ok().entity(loan).build();

        } catch (Exception e) {
            log.error("Error rejecting loan: {}", id, e);
            return Response.status(500)
                    .entity(new ErrorResponse("خطا در رد وام: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * دریافت همه وام‌ها
     * GET /api/loans
     */
    @GET
    public Response getAllLoans(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size) {
        try {
            List<Loan> loans = loanService.findAll(page, size);
            return Response.ok().entity(loans).build();

        } catch (Exception e) {
            log.error("Error fetching loans", e);
            return Response.status(500)
                    .entity(new ErrorResponse("خطا در دریافت وام‌ها: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * دریافت وام با ID
     * GET /api/loans/{id}
     */
    @GET
    @Path("/{id}")
    public Response getLoanById(@PathParam("id") Long id) {
        try {
            Optional<Loan> loanOpt = loanService.findById(id);

            if (loanOpt.isEmpty()) {
                return Response.status(404)
                        .entity(new ErrorResponse("وام یافت نشد"))
                        .build();
            }

            return Response.ok().entity(loanOpt.get()).build();

        } catch (Exception e) {
            log.error("Error fetching loan by id: {}", id, e);
            return Response.status(500)
                    .entity(new ErrorResponse("خطا در دریافت وام: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * دریافت وام با شماره وام
     * GET /api/loans/number/{loanNumber}
     */
    @GET
    @Path("/number/{loanNumber}")
    public Response getLoanByNumber(@PathParam("loanNumber") String loanNumber) {
        try {
            Optional<Loan> loanOpt = loanService.findByLoanNumber(loanNumber);

            if (loanOpt.isEmpty()) {
                return Response.status(404)
                        .entity(new ErrorResponse("وام یافت نشد"))
                        .build();
            }

            return Response.ok().entity(loanOpt.get()).build();

        } catch (Exception e) {
            log.error("Error fetching loan by number: {}", loanNumber, e);
            return Response.status(500)
                    .entity(new ErrorResponse("خطا در دریافت وام: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * دریافت وام‌های یک کاربر
     * GET /api/loans/user/{userId}
     */
    @GET
    @Path("/user/{userId}")
    public Response getLoansByUser(@PathParam("userId") Long userId) {
        try {
            Optional<User> userOpt = userService.findById(userId);

            if (userOpt.isEmpty()) {
                return Response.status(404)
                        .entity(new ErrorResponse("کاربر یافت نشد"))
                        .build();
            }

            List<Loan> loans = loanService.findByUser(userOpt.get());
            return Response.ok().entity(loans).build();

        } catch (Exception e) {
            log.error("Error fetching loans for user: {}", userId, e);
            return Response.status(500)
                    .entity(new ErrorResponse("خطا در دریافت وام‌ها: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * دریافت وام‌ها بر اساس وضعیت
     * GET /api/loans/status/{status}
     */
    @GET
    @Path("/status/{status}")
    public Response getLoansByStatus(@PathParam("status") String status) {
        try {
            LoanStatus loanStatus = LoanStatus.valueOf(status);
            List<Loan> loans = loanService.findByStatus(loanStatus);
            return Response.ok().entity(loans).build();

        } catch (IllegalArgumentException e) {
            return Response.status(400)
                    .entity(new ErrorResponse("وضعیت نامعتبر است"))
                    .build();
        } catch (Exception e) {
            log.error("Error fetching loans by status: {}", status, e);
            return Response.status(500)
                    .entity(new ErrorResponse("خطا در دریافت وام‌ها: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * دریافت وام‌های فعال
     * GET /api/loans/active
     */
    @GET
    @Path("/active")
    public Response getActiveLoans() {
        try {
            List<Loan> loans = loanService.findActiveLoans();
            return Response.ok().entity(loans).build();

        } catch (Exception e) {
            log.error("Error fetching active loans", e);
            return Response.status(500)
                    .entity(new ErrorResponse("خطا در دریافت وام‌های فعال: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * دریافت وام‌های در انتظار تأیید
     * GET /api/loans/pending
     */
    @GET
    @Path("/pending")
    public Response getPendingLoans() {
        try {
            List<Loan> loans = loanService.findByStatus(LoanStatus.PENDING);
            return Response.ok().entity(loans).build();

        } catch (Exception e) {
            log.error("Error fetching pending loans", e);
            return Response.status(500)
                    .entity(new ErrorResponse("خطا در دریافت وام‌های در انتظار: " + e.getMessage()))
                    .build();
        }
    }

    // ==================== Helper Methods ====================

    private String validateLoanRequest(LoanApplicationRequest request) {
        if (request.getAccountId() == null) {
            return "شناسه حساب الزامی است";
        }

        if (request.getPrincipal() == null ||
                request.getPrincipal().compareTo(BigDecimal.ZERO) <= 0) {
            return "مبلغ اصل وام باید بیشتر از صفر باشد";
        }

        if (request.getPrincipal().compareTo(new BigDecimal("1000000000")) > 0) {
            return "مبلغ وام خیلی زیاد است";
        }

        if (request.getAnnualInterestRate() == null ||
                request.getAnnualInterestRate().compareTo(BigDecimal.ZERO) < 0 ||
                request.getAnnualInterestRate().compareTo(new BigDecimal("100")) > 0) {
            return "نرخ بهره باید بین 0 تا 100 باشد";
        }

        if (request.getDurationMonths() == null ||
                request.getDurationMonths() < 1 ||
                request.getDurationMonths() > 360) {
            return "مدت زمان وام باید بین 1 تا 360 ماه باشد";
        }

        return null;
    }

    private BigDecimal calculateMonthlyPayment(BigDecimal principal,
                                               BigDecimal annualRate,
                                               Integer months) {
        if (annualRate.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(new BigDecimal(months), 2, RoundingMode.HALF_UP);
        }

        BigDecimal monthlyRate = annualRate
                .divide(new BigDecimal("12"), 6, RoundingMode.HALF_UP)
                .divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP);

        double onePlusR = 1 + monthlyRate.doubleValue();
        double power = Math.pow(onePlusR, months);

        BigDecimal numerator = monthlyRate.multiply(new BigDecimal(power));
        BigDecimal denominator = new BigDecimal(power).subtract(BigDecimal.ONE);

        return principal.multiply(numerator)
                .divide(denominator, 2, RoundingMode.HALF_UP);
    }

    private String generateLoanNumber() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder("LOAN-");

        for (int i = 0; i < 12; i++) {
            sb.append(random.nextInt(10));
        }

        return sb.toString();
    }

    // ==================== Request/Response DTOs ====================

    public static class LoanApplicationRequest {
        private Long accountId;
        private BigDecimal principal;
        private BigDecimal annualInterestRate;
        private Integer durationMonths;

        // Getters & Setters
        public Long getAccountId() { return accountId; }
        public void setAccountId(Long accountId) { this.accountId = accountId; }
        public BigDecimal getPrincipal() { return principal; }
        public void setPrincipal(BigDecimal principal) { this.principal = principal; }
        public BigDecimal getAnnualInterestRate() { return annualInterestRate; }
        public void setAnnualInterestRate(BigDecimal annualInterestRate) {
            this.annualInterestRate = annualInterestRate;
        }
        public Integer getDurationMonths() { return durationMonths; }
        public void setDurationMonths(Integer durationMonths) {
            this.durationMonths = durationMonths;
        }
    }

    public static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
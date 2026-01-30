package com.sleepy.onlinebankingsystem.controller.api;

import com.sleepy.onlinebankingsystem.model.dto.request.CreateLoanRequest;
import com.sleepy.onlinebankingsystem.model.dto.response.ApiResponse;
import com.sleepy.onlinebankingsystem.model.entity.Loan;
import com.sleepy.onlinebankingsystem.model.enums.LoanStatus;
import com.sleepy.onlinebankingsystem.service.LoanService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/loans")
@Slf4j
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LoanApi {

    @Inject
    private LoanService loanService;

    /**
     * درخواست وام جدید
     * POST /api/loans/apply
     */
    @POST
    @Path("/apply")
    public Response applyForLoan(CreateLoanRequest request) {
        try {
            log.info("API: Processing loan application for account: {}", request.getAccountNumber());

            // فراخوانی Service
            Loan loan = loanService.applyForLoan(
                    request.getAccountNumber(),
                    request.getPrincipal(),
                    request.getAnnualInterestRate(),
                    request.getDurationMonths()
            );

            // تبدیل به Response
            LoanResponse response = mapToResponse(loan);

            return Response.status(201).entity(ApiResponse.success(response)).build();

        } catch (IllegalArgumentException e) {
            log.warn("Validation error in loan application: {}", e.getMessage());
            return Response.status(400)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (IllegalStateException e) {
            log.warn("Business logic error in loan application: {}", e.getMessage());
            return Response.status(400)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Error processing loan application", e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در ثبت درخواست وام: " + e.getMessage()))
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
            log.info("API: Approving loan with ID: {}", id);

            // فراخوانی Service
            Loan loan = loanService.approveLoan(id);

            // تبدیل به Response
            LoanResponse response = mapToResponse(loan);

            return Response.ok().entity(ApiResponse.success(response)).build();

        } catch (IllegalArgumentException e) {
            log.warn("Validation error in loan approval: {}", e.getMessage());
            return Response.status(404)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (IllegalStateException e) {
            log.warn("Business logic error in loan approval: {}", e.getMessage());
            return Response.status(400)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Error approving loan: {}", id, e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در تأیید وام: " + e.getMessage()))
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
            log.info("API: Rejecting loan with ID: {}", id);

            // فراخوانی Service
            Loan loan = loanService.rejectLoan(id);

            // تبدیل به Response
            LoanResponse response = mapToResponse(loan);

            return Response.ok().entity(ApiResponse.success(response)).build();

        } catch (IllegalArgumentException e) {
            log.warn("Validation error in loan rejection: {}", e.getMessage());
            return Response.status(404)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (IllegalStateException e) {
            log.warn("Business logic error in loan rejection: {}", e.getMessage());
            return Response.status(400)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Error rejecting loan: {}", id, e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در رد وام: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * پرداخت قسط وام
     * POST /api/loans/{id}/pay
     */
    @POST
    @Path("/{id}/pay")
    public Response payLoanInstallment(@PathParam("id") Long id, PaymentRequest request) {
        try {
            log.info("API: Processing loan payment for ID: {}", id);

            // فراخوانی Service
            Loan loan = loanService.payLoanInstallment(id, request.getAmount());

            // تبدیل به Response
            LoanResponse response = mapToResponse(loan);

            return Response.ok().entity(ApiResponse.success(response)).build();

        } catch (IllegalArgumentException e) {
            log.warn("Validation error in loan payment: {}", e.getMessage());
            return Response.status(400)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (IllegalStateException e) {
            log.warn("Business logic error in loan payment: {}", e.getMessage());
            return Response.status(400)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Error paying loan installment: {}", id, e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در پرداخت قسط: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * محاسبه قسط ماهانه (بدون ذخیره)
     * POST /api/loans/calculate
     */
    @POST
    @Path("/calculate")
    public Response calculateMonthlyPayment(CalculateRequest request) {
        try {
            log.info("API: Calculating monthly payment");

            // فراخوانی Service
            BigDecimal monthlyPayment = loanService.calculateMonthlyPayment(
                    request.getPrincipal(),
                    request.getAnnualInterestRate(),
                    request.getDurationMonths()
            );

            CalculateResponse response = new CalculateResponse(
                    request.getPrincipal(),
                    request.getAnnualInterestRate(),
                    request.getDurationMonths(),
                    monthlyPayment
            );

            return Response.ok().entity(ApiResponse.success(response)).build();

        } catch (IllegalArgumentException e) {
            log.warn("Validation error in calculation: {}", e.getMessage());
            return Response.status(400)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Error calculating monthly payment", e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در محاسبه: " + e.getMessage()))
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
            List<LoanResponse> responses = loans.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());

            return Response.ok()
                    .entity(responses)
                    .build();

        } catch (Exception e) {
            log.error("Error fetching loans", e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در دریافت وام‌ها: " + e.getMessage()))
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
                        .entity(ApiResponse.error("وام یافت نشد"))
                        .build();
            }

            LoanResponse response = mapToResponse(loanOpt.get());

            return Response.ok().entity(response).build();

        } catch (Exception e) {
            log.error("Error fetching loan by id: {}", id, e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در دریافت وام: " + e.getMessage()))
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
            List<LoanResponse> responses = loans.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());

            return Response.ok().entity(responses).build();

        } catch (IllegalArgumentException e) {
            return Response.status(400)
                    .entity(ApiResponse.error("وضعیت نامعتبر است"))
                    .build();
        } catch (Exception e) {
            log.error("Error fetching loans by status: {}", status, e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در دریافت وام‌ها: " + e.getMessage()))
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
            List<LoanResponse> responses = loans.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());

            return Response.ok().entity(responses).build();

        } catch (Exception e) {
            log.error("Error fetching active loans", e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در دریافت وام‌های فعال: " + e.getMessage()))
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
            List<LoanResponse> responses = loans.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());

            return Response.ok().entity(responses).build();

        } catch (Exception e) {
            log.error("Error fetching pending loans", e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در دریافت وام‌های در انتظار: " + e.getMessage()))
                    .build();
        }
    }

    // ========== متدهای کمکی ==========

    /**
     * ✅ تبدیل Entity به DTO - اضافه شدن remainingBalance و سایر فیلدها
     */
    private LoanResponse mapToResponse(Loan loan) {
        return LoanResponse.builder()
                .loanNumber(loan.getLoanNumber())
                .principal(loan.getPrincipal())
                .remainingBalance(loan.getRemainingBalance())
                .paidAmount(loan.getPaidAmount())
                .annualInterestRate(loan.getAnnualInterestRate())
                .durationMonths(loan.getDurationMonths())
                .monthlyPayment(loan.getMonthlyPayment())
                .totalRepayment(loan.getTotalRepayment())
                .totalInterest(loan.getTotalInterest())
                .paymentProgress(loan.getPaymentProgress())
                .status(loan.getStatus())
                .build();
    }

    // ========== Request/Response DTOs ==========

    public static class PaymentRequest {
        private BigDecimal amount;

        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
    }

    public static class CalculateRequest {
        private BigDecimal principal;
        private BigDecimal annualInterestRate;
        private Integer durationMonths;

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

    public static class CalculateResponse {
        private BigDecimal principal;
        private BigDecimal annualInterestRate;
        private Integer durationMonths;
        private BigDecimal monthlyPayment;

        public CalculateResponse(BigDecimal principal, BigDecimal annualInterestRate,
                                 Integer durationMonths, BigDecimal monthlyPayment) {
            this.principal = principal;
            this.annualInterestRate = annualInterestRate;
            this.durationMonths = durationMonths;
            this.monthlyPayment = monthlyPayment;
        }

        public BigDecimal getPrincipal() { return principal; }
        public BigDecimal getAnnualInterestRate() { return annualInterestRate; }
        public Integer getDurationMonths() { return durationMonths; }
        public BigDecimal getMonthlyPayment() { return monthlyPayment; }
    }

    @lombok.Builder
    public static class LoanResponse {
        private String loanNumber;
        private BigDecimal principal;
        private BigDecimal remainingBalance;
        private BigDecimal paidAmount;
        private BigDecimal annualInterestRate;
        private Integer durationMonths;
        private BigDecimal monthlyPayment;
        private BigDecimal totalRepayment;
        private BigDecimal totalInterest;
        private int paymentProgress;
        private LoanStatus status;

        public LoanResponse(String loanNumber, BigDecimal principal, BigDecimal remainingBalance,
                            BigDecimal paidAmount, BigDecimal annualInterestRate, Integer durationMonths,
                            BigDecimal monthlyPayment, BigDecimal totalRepayment, BigDecimal totalInterest,
                            int paymentProgress, LoanStatus status) {
            this.loanNumber = loanNumber;
            this.principal = principal;
            this.remainingBalance = remainingBalance;
            this.paidAmount = paidAmount;
            this.annualInterestRate = annualInterestRate;
            this.durationMonths = durationMonths;
            this.monthlyPayment = monthlyPayment;
            this.totalRepayment = totalRepayment;
            this.totalInterest = totalInterest;
            this.paymentProgress = paymentProgress;
            this.status = status;
        }

        // Getters
        public String getLoanNumber() { return loanNumber; }
        public BigDecimal getPrincipal() { return principal; }
        public BigDecimal getRemainingBalance() { return remainingBalance; }
        public BigDecimal getPaidAmount() { return paidAmount; }
        public BigDecimal getAnnualInterestRate() { return annualInterestRate; }
        public Integer getDurationMonths() { return durationMonths; }
        public BigDecimal getMonthlyPayment() { return monthlyPayment; }
        public BigDecimal getTotalRepayment() { return totalRepayment; }
        public BigDecimal getTotalInterest() { return totalInterest; }
        public int getPaymentProgress() { return paymentProgress; }
        public LoanStatus getStatus() { return status; }
    }
}

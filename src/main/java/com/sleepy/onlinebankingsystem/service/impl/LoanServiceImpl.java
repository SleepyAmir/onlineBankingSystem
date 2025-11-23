package com.sleepy.onlinebankingsystem.service.impl;

import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.entity.Loan;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.model.enums.AccountStatus;
import com.sleepy.onlinebankingsystem.model.enums.LoanStatus;
import com.sleepy.onlinebankingsystem.repository.LoanRepository;
import com.sleepy.onlinebankingsystem.service.AccountService;
import com.sleepy.onlinebankingsystem.service.LoanService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class LoanServiceImpl implements LoanService {

    @Inject
    LoanRepository loanRepository;

    @Inject
    AccountService accountService;

    private final SecureRandom random = new SecureRandom();

    // حدود وام
    private static final BigDecimal MIN_LOAN_AMOUNT = new BigDecimal("1000000");        // 1M
    private static final BigDecimal MAX_LOAN_AMOUNT = new BigDecimal("1000000000");     // 1B
    private static final BigDecimal MIN_INTEREST_RATE = BigDecimal.ZERO;
    private static final BigDecimal MAX_INTEREST_RATE = new BigDecimal("100");
    private static final int MIN_DURATION_MONTHS = 1;
    private static final int MAX_DURATION_MONTHS = 360;

    // ========== متدهای CRUD موجود ==========

    @Transactional
    @Override
    public Loan save(Loan loan) throws Exception {
        log.info("Saving loan: {}", loan.getLoanNumber());

        if (loanRepository.findByLoanNumber(loan.getLoanNumber()).isPresent()) {
            throw new IllegalArgumentException("Loan number already exists: " + loan.getLoanNumber());
        }

        return loanRepository.save(loan);
    }

    @Transactional
    @Override
    public Loan update(Loan loan) throws Exception {
        if (loan.getId() == null) throw new IllegalArgumentException("ID is required");
        return loanRepository.save(loan);
    }

    @Transactional
    @Override
    public void softDelete(Long id) throws Exception {
        loanRepository.softDelete(id);
    }

    @Transactional
    @Override
    public void softDeleteByLoanNumber(String loanNumber) throws Exception {
        loanRepository.findByLoanNumber(loanNumber)
                .ifPresent(loan -> loanRepository.softDelete(loan.getId()));
    }

    @Override
    @Transactional
    public Loan payInstallment(Loan loan, BigDecimal amount) throws Exception {
        if (loan == null || amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("وام و مبلغ پرداخت الزامی و باید مثبت باشد");
        }

        if (loan.getStatus() != LoanStatus.APPROVED && loan.getStatus() != LoanStatus.ACTIVE) {
            throw new IllegalStateException("فقط وام‌های تأیید شده یا فعال قابل پرداخت هستند");
        }

        BigDecimal standardInstallment = loan.getMonthlyPayment();

        if (amount.compareTo(standardInstallment) < 0) {
            throw new IllegalArgumentException(
                    String.format("مبلغ پرداخت باید حداقل %.2f باشد", standardInstallment)
            );
        }

        BigDecimal remainingPrincipal = loan.getPrincipal().subtract(amount);
        if (remainingPrincipal.compareTo(BigDecimal.ZERO) <= 0) {
            loan.setStatus(LoanStatus.PAID);
            loan.setPrincipal(BigDecimal.ZERO);
            log.info("Loan fully paid: {}", loan.getLoanNumber());
        } else {
            loan.setPrincipal(remainingPrincipal);
            loan.setStatus(LoanStatus.ACTIVE);
            log.info("Installment paid for loan: {}, remaining: {}", loan.getLoanNumber(), remainingPrincipal);
        }

        return loanRepository.save(loan);
    }

    @Override
    public Optional<Loan> findById(Long id) throws Exception {
        return loanRepository.findById(id);
    }

    @Override
    public Optional<Loan> findByLoanNumber(String loanNumber) throws Exception {
        return loanRepository.findByLoanNumber(loanNumber);
    }

    @Override
    public List<Loan> findByUser(User user) throws Exception {
        return loanRepository.findByUser(user);
    }

    @Override
    public List<Loan> findByStatus(LoanStatus status) throws Exception {
        return loanRepository.findByStatus(status);
    }

    @Override
    public List<Loan> findActiveLoans() throws Exception {
        return loanRepository.findActiveLoans();
    }

    @Override
    public List<Loan> findAll(int page, int size) throws Exception {
        return loanRepository.findAll(page, size);
    }

    // ========== متدهای بیزنس جدید ==========

    @Transactional
    @Override
    public Loan applyForLoan(String accountNumber, BigDecimal principal,
                             BigDecimal annualInterestRate, Integer durationMonths) throws Exception {

        log.info("Processing loan application: {} for account {}", principal, accountNumber);

        // 1. اعتبارسنجی کامل
        validateLoanApplication(accountNumber, principal, annualInterestRate, durationMonths);

        // 2. پیدا کردن حساب
        Account account = accountService.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("حساب یافت نشد"));

        // 3. بررسی وضعیت حساب
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("حساب باید فعال باشد");
        }

        // 4. محاسبه قسط ماهانه
        BigDecimal monthlyPayment = calculateMonthlyPayment(principal, annualInterestRate, durationMonths);

        // 5. تولید شماره وام
        String loanNumber = generateLoanNumber();

        // 6. ساخت وام
        Loan loan = Loan.builder()
                .account(account)
                .user(account.getUser())
                .loanNumber(loanNumber)
                .principal(principal)
                .annualInterestRate(annualInterestRate)
                .durationMonths(durationMonths)
                .monthlyPayment(monthlyPayment)
                .startDate(LocalDate.now())
                .status(LoanStatus.PENDING)
                .build();

        Loan savedLoan = loanRepository.save(loan);
        log.info("Loan application submitted successfully: {}", savedLoan.getLoanNumber());

        return savedLoan;
    }

    @Transactional
    @Override
    public Loan approveLoan(Long loanId) throws Exception {

        log.info("Approving loan with ID: {}", loanId);

        // 1. پیدا کردن وام
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("وام یافت نشد"));

        // 2. بررسی وضعیت وام
        if (loan.getStatus() != LoanStatus.PENDING) {
            throw new IllegalStateException("فقط وام‌های در انتظار قابل تأیید هستند");
        }

        // 3. تغییر وضعیت وام
        loan.setStatus(LoanStatus.APPROVED);
        loanRepository.save(loan);

        // 4. واریز مبلغ وام به حساب
        Account account = loan.getAccount();
        account.setBalance(account.getBalance().add(loan.getPrincipal()));
        accountService.update(account);

        log.info("Loan approved successfully: {}", loan.getLoanNumber());

        return loan;
    }

    @Transactional
    @Override
    public Loan rejectLoan(Long loanId) throws Exception {

        log.info("Rejecting loan with ID: {}", loanId);

        // 1. پیدا کردن وام
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("وام یافت نشد"));

        // 2. بررسی وضعیت وام
        if (loan.getStatus() != LoanStatus.PENDING) {
            throw new IllegalStateException("فقط وام‌های در انتظار قابل رد هستند");
        }

        // 3. تغییر وضعیت وام
        loan.setStatus(LoanStatus.REJECTED);
        Loan updatedLoan = loanRepository.save(loan);

        log.info("Loan rejected: {}", loan.getLoanNumber());

        return updatedLoan;
    }

    @Transactional
    @Override
    public Loan payLoanInstallment(Long loanId, BigDecimal amount) throws Exception {

        log.info("Processing loan installment payment for loan ID: {}", loanId);

        // 1. پیدا کردن وام
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("وام یافت نشد"));

        // 2. بررسی وضعیت وام
        if (loan.getStatus() != LoanStatus.APPROVED && loan.getStatus() != LoanStatus.ACTIVE) {
            throw new IllegalStateException("فقط وام‌های فعال قابل پرداخت هستند");
        }

        // 3. تعیین مبلغ پرداخت (اگر null باشد، قسط استاندارد)
        BigDecimal paymentAmount = amount != null ? amount : loan.getMonthlyPayment();

        // 4. بررسی موجودی حساب
        Account account = loan.getAccount();
        if (account.getBalance().compareTo(paymentAmount) < 0) {
            throw new IllegalStateException("موجودی حساب کافی نیست");
        }

        // 5. کاهش موجودی حساب
        account.setBalance(account.getBalance().subtract(paymentAmount));
        accountService.update(account);

        // 6. پرداخت قسط
        Loan updatedLoan = payInstallment(loan, paymentAmount);

        log.info("Loan installment paid successfully: {} amount: {}",
                loan.getLoanNumber(), paymentAmount);

        return updatedLoan;
    }

    @Override
    public BigDecimal calculateMonthlyPayment(BigDecimal principal,
                                              BigDecimal annualInterestRate,
                                              Integer durationMonths) throws Exception {

        // بررسی ورودی‌ها
        if (principal == null || annualInterestRate == null || durationMonths == null) {
            throw new IllegalArgumentException("تمام پارامترها الزامی هستند");
        }

        if (principal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("مبلغ اصل باید مثبت باشد");
        }

        if (durationMonths <= 0) {
            throw new IllegalArgumentException("مدت زمان باید مثبت باشد");
        }

        // اگر بهره صفر باشد
        if (annualInterestRate.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(new BigDecimal(durationMonths), 2, RoundingMode.HALF_UP);
        }

        // محاسبه نرخ ماهانه (نرخ سالانه / 12 / 100)
        BigDecimal monthlyRate = annualInterestRate
                .divide(new BigDecimal("12"), 6, RoundingMode.HALF_UP)
                .divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP);

        // فرمول: PMT = P * [r(1+r)^n] / [(1+r)^n - 1]
        double onePlusR = 1 + monthlyRate.doubleValue();
        double power = Math.pow(onePlusR, durationMonths);

        BigDecimal numerator = monthlyRate.multiply(new BigDecimal(power));
        BigDecimal denominator = new BigDecimal(power).subtract(BigDecimal.ONE);

        BigDecimal monthlyPayment = principal
                .multiply(numerator)
                .divide(denominator, 2, RoundingMode.HALF_UP);

        log.debug("Calculated monthly payment: {} for principal: {}, rate: {}, duration: {}",
                monthlyPayment, principal, annualInterestRate, durationMonths);

        return monthlyPayment;
    }

    @Override
    public void validateLoanApplication(String accountNumber, BigDecimal principal,
                                        BigDecimal annualInterestRate,
                                        Integer durationMonths) throws Exception {

        // 1. بررسی شماره حساب
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("شماره حساب الزامی است");
        }

        // 2. بررسی مبلغ اصل وام
        if (principal == null) {
            throw new IllegalArgumentException("مبلغ اصل وام الزامی است");
        }
        if (principal.compareTo(MIN_LOAN_AMOUNT) < 0) {
            throw new IllegalArgumentException(
                    String.format("حداقل مبلغ وام %.0f ریال است", MIN_LOAN_AMOUNT)
            );
        }
        if (principal.compareTo(MAX_LOAN_AMOUNT) > 0) {
            throw new IllegalArgumentException(
                    String.format("حداکثر مبلغ وام %.0f ریال است", MAX_LOAN_AMOUNT)
            );
        }

        // 3. بررسی نرخ بهره
        if (annualInterestRate == null) {
            throw new IllegalArgumentException("نرخ بهره الزامی است");
        }
        if (annualInterestRate.compareTo(MIN_INTEREST_RATE) < 0 ||
                annualInterestRate.compareTo(MAX_INTEREST_RATE) > 0) {
            throw new IllegalArgumentException(
                    String.format("نرخ بهره باید بین %.0f تا %.0f درصد باشد",
                            MIN_INTEREST_RATE, MAX_INTEREST_RATE)
            );
        }

        // 4. بررسی مدت زمان
        if (durationMonths == null) {
            throw new IllegalArgumentException("مدت زمان وام الزامی است");
        }
        if (durationMonths < MIN_DURATION_MONTHS || durationMonths > MAX_DURATION_MONTHS) {
            throw new IllegalArgumentException(
                    String.format("مدت زمان وام باید بین %d تا %d ماه باشد",
                            MIN_DURATION_MONTHS, MAX_DURATION_MONTHS)
            );
        }
    }

    // در LoanServiceImpl.java - اضافه کنید

    @Override
    public Optional<Loan> findByIdWithUserAndAccount(Long id) throws Exception {
        log.debug("Fetching loan with user and account by ID: {}", id);
        return loanRepository.findByIdWithUserAndAccount(id);
    }

    @Override
    public Optional<Loan> findByLoanNumberWithUserAndAccount(String loanNumber) throws Exception {
        log.debug("Fetching loan with user and account by loan number: {}", loanNumber);
        return loanRepository.findByLoanNumberWithUserAndAccount(loanNumber);
    }
    @Override
    public List<Loan> findByStatusWithUserAndAccount(LoanStatus status) throws Exception {
        log.debug("Fetching loans with user and account by status: {}", status);
        return loanRepository.findByStatusWithUserAndAccount(status);
    }

    // ========== متدهای کمکی ==========

    private String generateLoanNumber() {
        StringBuilder sb = new StringBuilder("LOAN-");
        for (int i = 0; i < 12; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
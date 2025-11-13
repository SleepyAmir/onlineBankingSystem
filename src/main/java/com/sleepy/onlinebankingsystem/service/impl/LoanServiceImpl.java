package com.sleepy.onlinebankingsystem.service.impl;

import com.sleepy.onlinebankingsystem.model.entity.Loan;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.model.enums.LoanStatus;
import com.sleepy.onlinebankingsystem.repository.LoanRepository;
import com.sleepy.onlinebankingsystem.service.LoanService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class LoanServiceImpl implements LoanService {

    @Inject
    LoanRepository loanRepository;

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

        // محاسبه قسط استاندارد
        BigDecimal standardInstallment = loan.getMonthlyPayment();

        // اگر مبلغ پرداخت شده کمتر از قسط استاندارد باشد → خطا
        if (amount.compareTo(standardInstallment) < 0) {
            throw new IllegalArgumentException(
                    String.format("مبلغ پرداخت باید حداقل %.2f باشد", standardInstallment)
            );
        }

        // کاهش مانده وام
        BigDecimal remainingPrincipal = loan.getPrincipal().subtract(amount);
        if (remainingPrincipal.compareTo(BigDecimal.ZERO) <= 0) {
            // وام تسویه شد
            loan.setStatus(LoanStatus.PAID);
            loan.setPrincipal(BigDecimal.ZERO);
            log.info("Loan fully paid: {}", loan.getLoanNumber());
        } else {
            // فقط قسط پرداخت شد
            loan.setPrincipal(remainingPrincipal);
            loan.setStatus(LoanStatus.ACTIVE); // اگر قبلاً APPROVED بود
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


}
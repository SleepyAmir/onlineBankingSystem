package com.sleepy.onlinebankingsystem.model.entity;

import com.sleepy.onlinebankingsystem.model.enums.LoanStatus;
import lombok.*;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "loans",
        uniqueConstraints = @UniqueConstraint(columnNames = "loanNumber"))
@NamedQueries({
        @NamedQuery(name = "Loan.findByUser",
                query = "SELECT l FROM Loan l WHERE l.user = :user"),

        @NamedQuery(name = "Loan.findByLoanNumber",
                query = "SELECT l FROM Loan l WHERE l.loanNumber = :loanNumber"),

        @NamedQuery(name = "Loan.findByStatus",
                query = "SELECT l FROM Loan l WHERE l.status = :status"),

        @NamedQuery(name = "Loan.findActiveLoans",
                query = "SELECT l FROM Loan l WHERE l.status = 'ACTIVE'"),

        @NamedQuery(name = "Loan.findAll",
                query = "SELECT l FROM Loan l"),

        // ✅ JOIN FETCH برای User و Account
        @NamedQuery(name = "Loan.findByIdWithUserAndAccount",
                query = "SELECT l FROM Loan l " +
                        "JOIN FETCH l.user " +
                        "JOIN FETCH l.account a " +
                        "JOIN FETCH a.user " +
                        "WHERE l.id = :id AND l.deleted = false"),

        @NamedQuery(name = "Loan.findByLoanNumberWithUserAndAccount",
                query = "SELECT l FROM Loan l " +
                        "JOIN FETCH l.user " +
                        "JOIN FETCH l.account a " +
                        "JOIN FETCH a.user " +
                        "WHERE l.loanNumber = :loanNumber AND l.deleted = false")
})
public class Loan extends Base {

    public static final String FIND_BY_USER = "Loan.findByUser";
    public static final String FIND_BY_LOAN_NUMBER = "Loan.findByLoanNumber";
    public static final String FIND_BY_STATUS = "Loan.findByStatus";
    public static final String FIND_ACTIVE_LOANS = "Loan.findActiveLoans";
    public static final String FIND_ALL = "Loan.findAll";
    public static final String FIND_BY_ID_WITH_USER_AND_ACCOUNT = "Loan.findByIdWithUserAndAccount";
    public static final String FIND_BY_LOAN_NUMBER_WITH_USER_AND_ACCOUNT = "Loan.findByLoanNumberWithUserAndAccount";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(nullable = false, unique = true, length = 50)
    private String loanNumber;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal principal;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal annualInterestRate;

    @Column(nullable = false)
    private Integer durationMonths;

    @Column(precision = 19, scale = 2)
    private BigDecimal monthlyPayment;

    @Column(nullable = false)
    private LocalDate startDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status = LoanStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ✅ متد کمکی برای تبدیل به Date (برای JSP)
    public Date getStartDateAsDate() {
        if (this.startDate == null) return null;
        return Date.from(this.startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    // ✅ متد فرمت‌شده
    public String getFormattedStartDate() {
        if (this.startDate == null) return "";
        return this.startDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }

    // ✅ تاریخ پایان وام
    public LocalDate getEndDate() {
        if (this.startDate == null || this.durationMonths == null) return null;
        return this.startDate.plusMonths(this.durationMonths);
    }

    public Date getEndDateAsDate() {
        LocalDate endDate = getEndDate();
        if (endDate == null) return null;
        return Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public String getFormattedEndDate() {
        LocalDate endDate = getEndDate();
        if (endDate == null) return "";
        return endDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }

    // ✅ محاسبه کل مبلغ بازپرداخت
    public BigDecimal getTotalRepayment() {
        if (this.monthlyPayment == null || this.durationMonths == null) return BigDecimal.ZERO;
        return this.monthlyPayment.multiply(new BigDecimal(this.durationMonths));
    }

    // ✅ محاسبه کل سود
    public BigDecimal getTotalInterest() {
        return getTotalRepayment().subtract(this.principal != null ? this.principal : BigDecimal.ZERO);
    }
}
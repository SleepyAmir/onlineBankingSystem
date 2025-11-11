package com.sleepy.onlinebankingsystem.model.entity;


import com.sleepy.onlinebankingsystem.model.enums.LoanStatus;
import lombok.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "loans",
        uniqueConstraints = @UniqueConstraint(columnNames = "loanNumber"))
@NamedQueries({
        @NamedQuery(name = "Loan.findByUser", query = "SELECT l FROM Loan l WHERE l.user = :user"),
        @NamedQuery(name = "Loan.findByLoanNumber", query = "SELECT l FROM Loan l WHERE l.loanNumber = :loanNumber "),
        @NamedQuery(name = "Loan.findByStatus", query = "SELECT l FROM Loan l WHERE l.status = :status"),
        @NamedQuery(name = "Loan.findActiveLoans", query = "SELECT l FROM Loan l WHERE l.status = 'ACTIVE' "),
        @NamedQuery(name = "Loan.findAll", query = "SELECT l FROM Loan l ")
})
public class Loan extends Base {
    public static final String FIND_BY_USER = "Loan.findByUser";
    public static final String FIND_BY_LOAN_NUMBER = "Loan.findByLoanNumber";
    public static final String FIND_BY_STATUS = "Loan.findByStatus";
    public static final String FIND_BY_LOAN_TYPE = "Loan.findByLoanType";
    public static final String FIND_ACTIVE_LOANS = "Loan.findActiveLoans";
    public static final String FIND_ALL = "Loan.findAll";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(nullable = false, unique = true, length = 50)
    private String loanNumber;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal principal;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal annualInterestRate; // e.g., 18.00

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
}
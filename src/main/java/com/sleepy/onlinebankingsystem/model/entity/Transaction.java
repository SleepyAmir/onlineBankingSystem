package com.sleepy.onlinebankingsystem.model.entity;

import com.sleepy.onlinebankingsystem.model.enums.TransactionStatus;
import com.sleepy.onlinebankingsystem.model.enums.TransactionType;
import lombok.*;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transactions",
        uniqueConstraints = @UniqueConstraint(columnNames = "transactionId"))
@NamedQueries({
        @NamedQuery(name = "Transaction.findByAccount",
                query = "SELECT t FROM Transaction t WHERE (t.fromAccount = :account OR t.toAccount = :account)"),
        @NamedQuery(name = "Transaction.findByUser",
                query = "SELECT t FROM Transaction t WHERE (t.fromAccount.user = :user OR t.toAccount.user = :user)"),
        @NamedQuery(name = "Transaction.findByTransactionId",
                query = "SELECT t FROM Transaction t WHERE t.transactionId = :transactionId"),
        @NamedQuery(name = "Transaction.findByDateRange",
                query = "SELECT t FROM Transaction t WHERE t.transactionDate BETWEEN :startDate AND :endDate"),
        @NamedQuery(name = "Transaction.findAll",
                query = "SELECT t FROM Transaction t"),
        @NamedQuery(name = "Transaction.findByIdWithAccounts",
                query = "SELECT t FROM Transaction t " +
                        "LEFT JOIN FETCH t.fromAccount fa " +
                        "LEFT JOIN FETCH fa.user " +
                        "LEFT JOIN FETCH t.toAccount ta " +
                        "LEFT JOIN FETCH ta.user " +
                        "WHERE t.id = :id AND t.deleted = false"),
        @NamedQuery(name = "Transaction.findByTransactionIdWithAccounts",
                query = "SELECT t FROM Transaction t " +
                        "LEFT JOIN FETCH t.fromAccount fa " +
                        "LEFT JOIN FETCH fa.user " +
                        "LEFT JOIN FETCH t.toAccount ta " +
                        "LEFT JOIN FETCH ta.user " +
                        "WHERE t.transactionId = :transactionId AND t.deleted = false"),
        @NamedQuery(name = "Transaction.findAllWithAccounts",
                query = "SELECT t FROM Transaction t " +
                        "LEFT JOIN FETCH t.fromAccount fa " +
                        "LEFT JOIN FETCH fa.user " +
                        "LEFT JOIN FETCH t.toAccount ta " +
                        "LEFT JOIN FETCH ta.user " +
                        "WHERE t.deleted = false " +
                        "ORDER BY t.transactionDate DESC"),

        // ✅ جدید - تراکنش‌های یک کاربر با Account و User
        @NamedQuery(name = "Transaction.findByUserWithAccounts",
                query = "SELECT DISTINCT t FROM Transaction t " +
                        "LEFT JOIN FETCH t.fromAccount fa " +
                        "LEFT JOIN FETCH fa.user " +
                        "LEFT JOIN FETCH t.toAccount ta " +
                        "LEFT JOIN FETCH ta.user " +
                        "WHERE (fa.user = :user OR ta.user = :user) " +
                        "AND t.deleted = false " +
                        "ORDER BY t.transactionDate DESC"),

        // ✅ جدید - تراکنش‌های یک حساب با Account و User
        @NamedQuery(name = "Transaction.findByAccountWithAccounts",
                query = "SELECT t FROM Transaction t " +
                        "LEFT JOIN FETCH t.fromAccount fa " +
                        "LEFT JOIN FETCH fa.user " +
                        "LEFT JOIN FETCH t.toAccount ta " +
                        "LEFT JOIN FETCH ta.user " +
                        "WHERE (t.fromAccount = :account OR t.toAccount = :account) " +
                        "AND t.deleted = false " +
                        "ORDER BY t.transactionDate DESC"),

        // ✅ جدید - تراکنش‌های بازه زمانی با Account و User
        @NamedQuery(name = "Transaction.findByDateRangeWithAccounts",
                query = "SELECT t FROM Transaction t " +
                        "LEFT JOIN FETCH t.fromAccount fa " +
                        "LEFT JOIN FETCH fa.user " +
                        "LEFT JOIN FETCH t.toAccount ta " +
                        "LEFT JOIN FETCH ta.user " +
                        "WHERE t.transactionDate BETWEEN :startDate AND :endDate " +
                        "AND t.deleted = false " +
                        "ORDER BY t.transactionDate DESC")
})
public class Transaction extends Base {

    public static final String FIND_BY_ACCOUNT = "Transaction.findByAccount";
    public static final String FIND_BY_USER = "Transaction.findByUser";
    public static final String FIND_BY_TRANSACTION_ID = "Transaction.findByTransactionId";
    public static final String FIND_BY_DATE_RANGE = "Transaction.findByDateRange";
    public static final String FIND_ALL = "Transaction.findAll";
    public static final String FIND_BY_ID_WITH_ACCOUNTS = "Transaction.findByIdWithAccounts";
    public static final String FIND_BY_TRANSACTION_ID_WITH_ACCOUNTS = "Transaction.findByTransactionIdWithAccounts";
    public static final String FIND_ALL_WITH_ACCOUNTS = "Transaction.findAllWithAccounts";
    public static final String FIND_BY_USER_WITH_ACCOUNTS = "Transaction.findByUserWithAccounts";
    public static final String FIND_BY_ACCOUNT_WITH_ACCOUNTS = "Transaction.findByAccountWithAccounts";
    public static final String FIND_BY_DATE_RANGE_WITH_ACCOUNTS = "Transaction.findByDateRangeWithAccounts";

    @Column(nullable = false, unique = true, length = 50)
    private String transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_account_id")
    private Account fromAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_account_id")
    private Account toAccount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false)
    private LocalDateTime transactionDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Column(length = 255, columnDefinition = "NVARCHAR2(255)")
    private String description;

    @Column(length = 50)
    private String referenceNumber;

    // ✅ متد کمکی برای تبدیل به Date (برای JSP با fmt:formatDate)
    public Date getTransactionDateAsDate() {
        if (this.transactionDate == null) return null;
        return Date.from(this.transactionDate.atZone(ZoneId.systemDefault()).toInstant());
    }

    // ✅ متد فرمت‌شده برای نمایش مستقیم در JSP
    public String getFormattedTransactionDate() {
        if (this.transactionDate == null) return "";
        return this.transactionDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
    }

    // ✅ فقط تاریخ
    public String getFormattedDate() {
        if (this.transactionDate == null) return "";
        return this.transactionDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }

    // ✅ فقط ساعت
    public String getFormattedTime() {
        if (this.transactionDate == null) return "";
        return this.transactionDate.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}
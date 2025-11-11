package com.sleepy.onlinebankingsystem.model.entity;

import com.sleepy.onlinebankingsystem.model.enums.AccountStatus;
import com.sleepy.onlinebankingsystem.model.enums.AccountType;
import lombok.*;
import jakarta.persistence.*;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "accounts", uniqueConstraints = @UniqueConstraint(columnNames = "accountNumber"))
@NamedQueries({
        @NamedQuery(name = "Account.findByUser", query = "SELECT a FROM Account a WHERE a.user = :user"),
        @NamedQuery(name = "Account.findByAccountNumber", query = "SELECT a FROM Account a WHERE a.accountNumber = :accountNumber"),
        @NamedQuery(name = "Account.findByStatus", query = "SELECT a FROM Account a WHERE a.status = :status"),
        @NamedQuery(name = "Account.findAll", query = "SELECT a FROM Account a")
})
public class Account extends Base {

    public static final String FIND_BY_USER = "Account.findByUser";
    public static final String FIND_BY_ACCOUNT_NUMBER = "Account.findByAccountNumber";
    public static final String FIND_BY_STATUS = "Account.findByStatus";
    public static final String FIND_ALL = "Account.findAll";



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true, length = 16)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status = AccountStatus.ACTIVE;

    @OneToMany(mappedBy = "fromAccount", fetch = FetchType.LAZY)
    private Set<Transaction> outgoingTransactions = new HashSet<>();

    @OneToMany(mappedBy = "toAccount", fetch = FetchType.LAZY)
    private Set<Transaction> incomingTransactions = new HashSet<>();

    // isDeleted() از Base میاد
}
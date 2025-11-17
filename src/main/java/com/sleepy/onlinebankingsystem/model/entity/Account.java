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
        @NamedQuery(name = "Account.findByUserWithUser", query = "SELECT DISTINCT a FROM Account a " + "JOIN FETCH a.user u " + "WHERE a.user = :user AND a.deleted = false"),        @NamedQuery(name = "Account.findAll", query = "SELECT a FROM Account a"),
        @NamedQuery(name = "Account.findByIdWithUser", query = "SELECT a FROM Account a JOIN FETCH a.user WHERE a.id = :id AND a.deleted = false")
})
public class Account extends Base {

    public static final String FIND_BY_USER = "Account.findByUser";
    public static final String FIND_BY_ACCOUNT_NUMBER = "Account.findByAccountNumber";
    public static final String FIND_BY_STATUS = "Account.findByStatus";
    public static final String FIND_ALL = "Account.findAll";
    public static final String FIND_BY_USER_WITH_USER = "Account.findByUserWithUser";
    public static final String FIND_BY_ID_WITH_USER = "Account.findByIdWithUser";


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true, length = 16)
    private String accountNumber;



    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Basic(fetch = FetchType.EAGER)
    private AccountType type;



    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Basic(fetch = FetchType.EAGER)
    private AccountStatus status = AccountStatus.ACTIVE;

    @OneToMany(mappedBy = "fromAccount", fetch = FetchType.LAZY)
    private Set<Transaction> outgoingTransactions = new HashSet<>();

    @OneToMany(mappedBy = "toAccount", fetch = FetchType.LAZY)
    private Set<Transaction> incomingTransactions = new HashSet<>();

    // isDeleted() از Base میاد
}
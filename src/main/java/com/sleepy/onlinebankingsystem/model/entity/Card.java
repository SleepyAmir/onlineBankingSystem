package com.sleepy.onlinebankingsystem.model.entity;


import com.sleepy.onlinebankingsystem.model.enums.CardType;
import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cards",
        uniqueConstraints = @UniqueConstraint(columnNames = "cardNumber"))
@NamedQueries({
        @NamedQuery(name = "Card.findByAccount", query = "SELECT c FROM Card c WHERE c.account = :account "),
        @NamedQuery(name = "Card.findByCardNumber", query = "SELECT c FROM Card c WHERE c.cardNumber = :cardNumber "),
        @NamedQuery(name = "Card.findByUser", query = "SELECT c FROM Card c WHERE c.account.user = :user"),
        @NamedQuery(name = "Card.findActiveCards", query = "SELECT c FROM Card c WHERE c.active = true "),
        @NamedQuery(name = "Card.findAll", query = "SELECT c FROM Card c "),
        @NamedQuery(name = "Card.findByUserWithAccount", query = "SELECT c FROM Card c JOIN FETCH c.account WHERE c.account.user.id = :userId AND c.deleted = false"),
        @NamedQuery(name = "Card.findByIdWithAccount", query = "SELECT c FROM Card c JOIN FETCH c.account WHERE c.id = :id AND c.deleted = false"),
        @NamedQuery(name = "Card.findByUserWithAccountAndUser", query = "SELECT c FROM Card c " + "JOIN FETCH c.account a " + "JOIN FETCH a.user u " + "WHERE u.id = :userId AND c.deleted = false"),
        @NamedQuery(name = "Card.findByCardNumberWithAccount", query = "SELECT c FROM Card c JOIN FETCH c.account a JOIN FETCH a.user WHERE c.cardNumber = :cardNumber AND c.deleted = false")
})
public class Card extends Base {
    public static final String FIND_BY_ACCOUNT = "Card.findByAccount";
    public static final String FIND_BY_CARD_NUMBER = "Card.findByCardNumber";
    public static final String FIND_BY_USER = "Card.findByUser";
    public static final String FIND_ACTIVE_CARDS = "Card.findActiveCards";
    public static final String FIND_ALL = "Card.findAll";
    public static final String FIND_BY_USER_WITH_ACCOUNT = "Card.findByUserWithAccount";
    public static final String FIND_BY_USER_WITH_ACCOUNT_AND_USER = "Card.findByUserWithAccountAndUser";
    public static final String FIND_BY_ID_WITH_ACCOUNT = "Card.findByIdWithAccount";
    public static final String FIND_BY_CARD_NUMBER_WITH_ACCOUNT = "Card.findByCardNumberWithAccount";



    @Column(nullable = false, unique = true, length = 16)
    private String cardNumber;

    @Column(nullable = false, length = 3)
    private String cvv;

    @Column(nullable = false)
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Basic(fetch = FetchType.EAGER)
    private CardType type;

    @Column(nullable = false)
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    public Date getExpiryDateAsDate() {
        return Date.from(this.expiryDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}

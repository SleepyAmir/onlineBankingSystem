package com.sleepy.onlinebankingsystem.service.impl;

import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.entity.Card;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.model.enums.AccountStatus;
import com.sleepy.onlinebankingsystem.model.enums.CardType;
import com.sleepy.onlinebankingsystem.repository.CardRepository;
import com.sleepy.onlinebankingsystem.service.AccountService;
import com.sleepy.onlinebankingsystem.service.CardService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class CardServiceImpl implements CardService {

    @Inject
    CardRepository cardRepository;

    @Inject
    AccountService accountService;

    private final SecureRandom random = new SecureRandom();

    // ثوابت کارت
    private static final String CARD_BIN = "6037"; // BIN معمول کارت‌های ایرانی
    private static final int CARD_VALIDITY_YEARS = 3;
    private static final int MAX_CARDS_PER_ACCOUNT = 5;

    // ========== متدهای CRUD موجود ==========

    @Transactional
    @Override
    public Card save(Card card) throws Exception {
        log.info("Saving card: {}", card.getCardNumber());

        if (cardRepository.findByCardNumber(card.getCardNumber()).isPresent()) {
            throw new IllegalArgumentException("Card number already exists: " + card.getCardNumber());
        }

        return cardRepository.save(card);
    }

    @Transactional
    @Override
    public Card update(Card card) throws Exception {
        if (card.getId() == null) throw new IllegalArgumentException("ID is required");
        return cardRepository.save(card);
    }

    @Transactional
    @Override
    public void softDelete(Long id) throws Exception {
        cardRepository.softDelete(id);
    }

    @Transactional
    @Override
    public void softDeleteByCardNumber(String cardNumber) throws Exception {
        cardRepository.findByCardNumber(cardNumber)
                .ifPresent(card -> cardRepository.softDelete(card.getId()));
    }

    @Override
    public Optional<Card> findById(Long id) throws Exception {
        return cardRepository.findById(id);
    }

    @Override
    public Optional<Card> findByCardNumber(String cardNumber) throws Exception {
        return cardRepository.findByCardNumber(cardNumber);
    }

    @Override
    public List<Card> findByAccount(Account account) throws Exception {
        return cardRepository.findByAccount(account);
    }

    @Override
    public List<Card> findByUser(User user) throws Exception {
        return cardRepository.findByUser(user);
    }

    @Override
    public List<Card> findActiveCards() throws Exception {
        return cardRepository.findActiveCards();
    }

    @Override
    public List<Card> findAll(int page, int size) throws Exception {
        return cardRepository.findAll(page, size);
    }

    @Override
    public List<Card> findByUserWithAccountAndUser(Long userId) throws Exception {
        log.debug("Fetching cards with account and user for user ID: {}", userId);
        List<Card> cards = cardRepository.getEntityManager()
                .createNamedQuery(Card.FIND_BY_USER_WITH_ACCOUNT_AND_USER, Card.class)
                .setParameter("userId", userId)
                .getResultList();

        // ✅ Force initialize
        cards.forEach(card -> {
            card.getType(); // Force load enum
            if (card.getAccount() != null) {
                card.getAccount().getType(); // Force load Account enum
            }
        });

        return cards;
    }
    @Override
    public List<Card> findByUserWithAccount(Long userId) throws Exception {
        log.debug("Fetching cards with account for user ID: {}", userId);
        try {
            return cardRepository.getEntityManager()
                    .createNamedQuery(Card.FIND_BY_USER_WITH_ACCOUNT, Card.class)
                    .setParameter("userId", userId)
                    .getResultList();
        } catch (Exception e) {
            log.error("Error fetching cards with account for user ID: {}", userId, e);
            throw e;
        }
    }

    // ========== متدهای بیزنس جدید ==========

    @Transactional
    @Override
    public Card issueCard(Long accountId, CardType cardType) throws Exception {

        log.info("Issuing new card for account ID: {}", accountId);

        // 1. اعتبارسنجی
        validateCardIssuance(accountId);

        // 2. پیدا کردن حساب
        Account account = accountService.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("حساب یافت نشد"));

        // 3. بررسی نوع کارت
        if (cardType == null) {
            throw new IllegalArgumentException("نوع کارت الزامی است");
        }

        // 4. تولید اطلاعات کارت
        String cardNumber = generateUniqueCardNumber();
        String cvv = generateCVV();
        LocalDate expiryDate = LocalDate.now().plusYears(CARD_VALIDITY_YEARS);

        // 5. ساخت کارت
        Card card = Card.builder()
                .account(account)
                .cardNumber(cardNumber)
                .cvv(cvv)
                .expiryDate(expiryDate)
                .type(cardType)
                .active(true)
                .build();

        Card savedCard = cardRepository.save(card);
        log.info("Card issued successfully: {} for account: {}",
                maskCardNumber(cardNumber), account.getAccountNumber());

        return savedCard;
    }

    @Transactional
    @Override
    public Card activateCard(Long cardId) throws Exception {

        log.info("Activating card with ID: {}", cardId);

        // 1. پیدا کردن کارت
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("کارت یافت نشد"));

        // 2. بررسی وضعیت فعلی
        if (card.isActive()) {
            throw new IllegalStateException("کارت از قبل فعال است");
        }

        // 3. بررسی انقضا
        if (card.getExpiryDate().isBefore(LocalDate.now())) {
            throw new IllegalStateException("کارت منقضی شده است و قابل فعال‌سازی نیست");
        }

        // 4. فعال‌سازی
        card.setActive(true);
        Card updatedCard = cardRepository.save(card);

        log.info("Card activated: {}", maskCardNumber(card.getCardNumber()));

        return updatedCard;
    }

    @Transactional
    @Override
    public Card blockCard(Long cardId) throws Exception {

        log.info("Blocking card with ID: {}", cardId);

        // 1. پیدا کردن کارت
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("کارت یافت نشد"));

        // 2. بررسی وضعیت فعلی
        if (!card.isActive()) {
            throw new IllegalStateException("کارت از قبل مسدود است");
        }

        // 3. مسدودسازی
        card.setActive(false);
        Card updatedCard = cardRepository.save(card);

        log.info("Card blocked: {}", maskCardNumber(card.getCardNumber()));

        return updatedCard;
    }

    @Override
    public void validateCardForTransaction(String cardNumber) throws Exception {

        // 1. بررسی وجود کارت
        Card card = cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new IllegalArgumentException("کارت یافت نشد"));

        // 2. بررسی فعال بودن
        if (!card.isActive()) {
            throw new IllegalStateException("کارت غیرفعال است");
        }

        // 3. بررسی انقضا
        if (card.getExpiryDate().isBefore(LocalDate.now())) {
            throw new IllegalStateException("کارت منقضی شده است");
        }

        // 4. بررسی وضعیت حساب
        Account account = card.getAccount();
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("حساب مرتبط با کارت فعال نیست");
        }

        log.debug("Card validated successfully: {}", maskCardNumber(cardNumber));
    }

    @Override
    public void validateCardIssuance(Long accountId) throws Exception {

        // 1. بررسی وجود حساب
        Account account = accountService.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("حساب یافت نشد"));

        // 2. بررسی وضعیت حساب
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("حساب باید فعال باشد");
        }

        // 3. بررسی تعداد کارت‌های موجود
        List<Card> existingCards = cardRepository.findByAccount(account);
        long activeCardsCount = existingCards.stream()
                .filter(Card::isActive)
                .count();

        if (activeCardsCount >= MAX_CARDS_PER_ACCOUNT) {
            throw new IllegalStateException(
                    String.format("حداکثر %d کارت فعال برای هر حساب مجاز است", MAX_CARDS_PER_ACCOUNT)
            );
        }

        log.debug("Card issuance validated for account: {}", account.getAccountNumber());
    }

    // ========== متدهای کمکی ==========

    /**
     * تولید شماره کارت یکتا 16 رقمی
     */
    private String generateUniqueCardNumber() throws Exception {
        String cardNumber;
        int attempts = 0;
        int maxAttempts = 100;

        do {
            cardNumber = generateCardNumber();
            attempts++;

            if (attempts >= maxAttempts) {
                throw new IllegalStateException("خطا در تولید شماره کارت یکتا");
            }

        } while (cardRepository.findByCardNumber(cardNumber).isPresent());

        return cardNumber;
    }

    /**
     * تولید شماره کارت 16 رقمی
     */
    private String generateCardNumber() {
        StringBuilder sb = new StringBuilder(CARD_BIN); // 6037

        for (int i = 0; i < 12; i++) {
            sb.append(random.nextInt(10));
        }

        return sb.toString();
    }

    /**
     * تولید CVV 3 رقمی
     */
    private String generateCVV() {
        return String.format("%03d", 100 + random.nextInt(900));
    }

    /**
     * پنهان کردن شماره کارت (نمایش 4 رقم آخر)
     */
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        return "************" + cardNumber.substring(cardNumber.length() - 4);
    }
}
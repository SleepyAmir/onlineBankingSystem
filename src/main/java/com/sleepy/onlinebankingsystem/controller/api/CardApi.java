package com.sleepy.onlinebankingsystem.controller.api;

import com.sleepy.onlinebankingsystem.model.dto.response.ApiResponse;
import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.entity.Card;
import com.sleepy.onlinebankingsystem.model.enums.AccountStatus;
import com.sleepy.onlinebankingsystem.model.enums.CardType;
import com.sleepy.onlinebankingsystem.service.AccountService;
import com.sleepy.onlinebankingsystem.service.CardService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/cards")
@Slf4j
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CardApi {

    @Inject
    private CardService cardService;

    @Inject
    private AccountService accountService;

    /**
     * صدور کارت جدید
     * POST /api/cards
     */
    @POST
    public Response createCard(CardCreateRequest request) {
        try {
            log.info("Creating card for account: {}", request.getAccountId());

            // اعتبارسنجی
            if (request.getAccountId() == null) {
                return Response.status(400)
                        .entity(ApiResponse.error("شناسه حساب الزامی است"))
                        .build();
            }

            if (request.getType() == null) {
                return Response.status(400)
                        .entity(ApiResponse.error("نوع کارت الزامی است"))
                        .build();
            }

            // پیدا کردن حساب
            Optional<Account> accountOpt = accountService.findById(request.getAccountId());
            if (accountOpt.isEmpty()) {
                return Response.status(404)
                        .entity(ApiResponse.error("حساب یافت نشد"))
                        .build();
            }

            Account account = accountOpt.get();

            // بررسی وضعیت حساب
            if (account.getStatus() != AccountStatus.ACTIVE) {
                return Response.status(400)
                        .entity(ApiResponse.error("حساب باید فعال باشد"))
                        .build();
            }

            // تولید اطلاعات کارت
            String cardNumber = generateCardNumber();
            String cvv = generateCVV();
            LocalDate expiryDate = LocalDate.now().plusYears(3);

            // ساخت کارت
            Card card = Card.builder()
                    .account(account)
                    .cardNumber(cardNumber)
                    .cvv(cvv)
                    .expiryDate(expiryDate)
                    .type(request.getType())
                    .active(true)
                    .build();

            Card savedCard = cardService.save(card);
            log.info("Card created successfully: {}", maskCardNumber(cardNumber));

            CardResponse response = new CardResponse(savedCard);

            return Response.status(201)
                    .entity(ApiResponse.success(response))
                    .build();

        } catch (Exception e) {
            log.error("Error creating card", e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در صدور کارت: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * دریافت همه کارت‌ها
     * GET /api/cards
     */
    @GET
    public Response getAllCards(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size) {
        try {
            List<Card> cards = cardService.findAll(page, size);
            List<CardResponse> responses = cards.stream()
                    .map(CardResponse::new)
                    .collect(Collectors.toList());

            return Response.ok()
                    .entity(ApiResponse.success(responses))
                    .build();

        } catch (Exception e) {
            log.error("Error fetching cards", e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در دریافت کارت‌ها: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * دریافت کارت‌های فعال
     * GET /api/cards/active
     */
    @GET
    @Path("/active")
    public Response getActiveCards() {
        try {
            List<Card> cards = cardService.findActiveCards();
            List<CardResponse> responses = cards.stream()
                    .map(CardResponse::new)
                    .collect(Collectors.toList());

            return Response.ok()
                    .entity(ApiResponse.success(responses))
                    .build();

        } catch (Exception e) {
            log.error("Error fetching active cards", e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در دریافت کارت‌های فعال: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * دریافت کارت با ID
     * GET /api/cards/{id}
     */
    @GET
    @Path("/{id}")
    public Response getCardById(@PathParam("id") Long id) {
        try {
            Optional<Card> cardOpt = cardService.findById(id);

            if (cardOpt.isEmpty()) {
                return Response.status(404)
                        .entity(ApiResponse.error("کارت یافت نشد"))
                        .build();
            }

            CardResponse response = new CardResponse(cardOpt.get());

            return Response.ok()
                    .entity(ApiResponse.success(response))
                    .build();

        } catch (Exception e) {
            log.error("Error fetching card by id: {}", id, e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در دریافت کارت: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * دریافت کارت با شماره کارت
     * GET /api/cards/number/{cardNumber}
     */
    @GET
    @Path("/number/{cardNumber}")
    public Response getCardByNumber(@PathParam("cardNumber") String cardNumber) {
        try {
            Optional<Card> cardOpt = cardService.findByCardNumber(cardNumber);

            if (cardOpt.isEmpty()) {
                return Response.status(404)
                        .entity(ApiResponse.error("کارت یافت نشد"))
                        .build();
            }

            CardResponse response = new CardResponse(cardOpt.get());

            return Response.ok()
                    .entity(ApiResponse.success(response))
                    .build();

        } catch (Exception e) {
            log.error("Error fetching card by number: {}", cardNumber, e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در دریافت کارت: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * دریافت کارت‌های یک حساب
     * GET /api/cards/account/{accountId}
     */
    @GET
    @Path("/account/{accountId}")
    public Response getCardsByAccount(@PathParam("accountId") Long accountId) {
        try {
            Optional<Account> accountOpt = accountService.findById(accountId);

            if (accountOpt.isEmpty()) {
                return Response.status(404)
                        .entity(ApiResponse.error("حساب یافت نشد"))
                        .build();
            }

            List<Card> cards = cardService.findByAccount(accountOpt.get());
            List<CardResponse> responses = cards.stream()
                    .map(CardResponse::new)
                    .collect(Collectors.toList());

            return Response.ok()
                    .entity(ApiResponse.success(responses))
                    .build();

        } catch (Exception e) {
            log.error("Error fetching cards for account: {}", accountId, e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در دریافت کارت‌ها: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * فعال‌سازی کارت
     * POST /api/cards/{id}/activate
     */
    @POST
    @Path("/{id}/activate")
    public Response activateCard(@PathParam("id") Long id) {
        try {
            Optional<Card> cardOpt = cardService.findById(id);

            if (cardOpt.isEmpty()) {
                return Response.status(404)
                        .entity(ApiResponse.error("کارت یافت نشد"))
                        .build();
            }

            Card card = cardOpt.get();

            if (card.isActive()) {
                return Response.status(400)
                        .entity(ApiResponse.error("کارت از قبل فعال است"))
                        .build();
            }

            card.setActive(true);
            Card updatedCard = cardService.update(card);

            log.info("Card activated: {}", maskCardNumber(card.getCardNumber()));

            CardResponse response = new CardResponse(updatedCard);

            return Response.ok()
                    .entity(ApiResponse.success(response))
                    .build();

        } catch (Exception e) {
            log.error("Error activating card: {}", id, e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در فعال‌سازی کارت: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * مسدودسازی کارت
     * POST /api/cards/{id}/block
     */
    @POST
    @Path("/{id}/block")
    public Response blockCard(@PathParam("id") Long id) {
        try {
            Optional<Card> cardOpt = cardService.findById(id);

            if (cardOpt.isEmpty()) {
                return Response.status(404)
                        .entity(ApiResponse.error("کارت یافت نشد"))
                        .build();
            }

            Card card = cardOpt.get();

            if (!card.isActive()) {
                return Response.status(400)
                        .entity(ApiResponse.error("کارت از قبل مسدود است"))
                        .build();
            }

            card.setActive(false);
            Card updatedCard = cardService.update(card);

            log.info("Card blocked: {}", maskCardNumber(card.getCardNumber()));

            CardResponse response = new CardResponse(updatedCard);

            return Response.ok()
                    .entity(ApiResponse.success(response))
                    .build();

        } catch (Exception e) {
            log.error("Error blocking card: {}", id, e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در مسدودسازی کارت: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * حذف نرم کارت
     * DELETE /api/cards/{id}
     */
    @DELETE
    @Path("/{id}")
    public Response deleteCard(@PathParam("id") Long id) {
        try {
            Optional<Card> cardOpt = cardService.findById(id);

            if (cardOpt.isEmpty()) {
                return Response.status(404)
                        .entity(ApiResponse.error("کارت یافت نشد"))
                        .build();
            }

            cardService.softDelete(id);
            log.info("Card soft-deleted: {}", maskCardNumber(cardOpt.get().getCardNumber()));

            return Response.ok()
                    .entity(ApiResponse.success("کارت با موفقیت حذف شد"))
                    .build();

        } catch (Exception e) {
            log.error("Error deleting card: {}", id, e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در حذف کارت: " + e.getMessage()))
                    .build();
        }
    }

    // ==================== Helper Methods ====================

    private String generateCardNumber() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder("6037"); // BIN معمول کارت‌های ایرانی

        for (int i = 0; i < 12; i++) {
            sb.append(random.nextInt(10));
        }

        return sb.toString();
    }

    private String generateCVV() {
        SecureRandom random = new SecureRandom();
        return String.format("%03d", 100 + random.nextInt(900));
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        return "************" + cardNumber.substring(cardNumber.length() - 4);
    }

    // ==================== Request/Response DTOs ====================

    public static class CardCreateRequest {
        private Long accountId;
        private CardType type;

        public Long getAccountId() { return accountId; }
        public void setAccountId(Long accountId) { this.accountId = accountId; }
        public CardType getType() { return type; }
        public void setType(CardType type) { this.type = type; }
    }

    public static class CardResponse {
        private Long id;
        private String cardNumber; // Masked
        private LocalDate expiryDate;
        private CardType type;
        private boolean active;
        private Long accountId;

        public CardResponse(Card card) {
            this.id = card.getId();
            this.cardNumber = maskCardNumber(card.getCardNumber());
            this.expiryDate = card.getExpiryDate();
            this.type = card.getType();
            this.active = card.isActive();
            this.accountId = card.getAccount() != null ? card.getAccount().getId() : null;
        }

        private String maskCardNumber(String cardNumber) {
            if (cardNumber == null || cardNumber.length() < 4) {
                return "****";
            }
            return "************" + cardNumber.substring(cardNumber.length() - 4);
        }

        // Getters
        public Long getId() { return id; }
        public String getCardNumber() { return cardNumber; }
        public LocalDate getExpiryDate() { return expiryDate; }
        public CardType getType() { return type; }
        public boolean isActive() { return active; }
        public Long getAccountId() { return accountId; }
    }
}
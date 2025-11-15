package com.sleepy.onlinebankingsystem.controller.api;

import com.sleepy.onlinebankingsystem.model.dto.response.ApiResponse;
import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.entity.Card;
import com.sleepy.onlinebankingsystem.model.enums.CardType;
import com.sleepy.onlinebankingsystem.service.AccountService;
import com.sleepy.onlinebankingsystem.service.CardService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

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
            log.info("API: Creating card for account: {}", request.getAccountId());

            // بررسی ورودی
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

            // فراخوانی Service
            Card card = cardService.issueCard(request.getAccountId(), request.getType());

            // تبدیل به Response
            CardResponse response = mapToResponse(card);

            return Response.status(201)
                    .entity(ApiResponse.success(response))
                    .build();

        } catch (IllegalArgumentException e) {
            log.warn("Validation error in card creation: {}", e.getMessage());
            return Response.status(400)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (IllegalStateException e) {
            log.warn("Business logic error in card creation: {}", e.getMessage());
            return Response.status(400)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Error creating card", e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در صدور کارت: " + e.getMessage()))
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
            log.info("API: Activating card with ID: {}", id);

            // فراخوانی Service
            Card card = cardService.activateCard(id);

            // تبدیل به Response
            CardResponse response = mapToResponse(card);

            return Response.ok()
                    .entity(ApiResponse.success(response))
                    .build();

        } catch (IllegalArgumentException e) {
            log.warn("Validation error in card activation: {}", e.getMessage());
            return Response.status(404)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (IllegalStateException e) {
            log.warn("Business logic error in card activation: {}", e.getMessage());
            return Response.status(400)
                    .entity(ApiResponse.error(e.getMessage()))
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
            log.info("API: Blocking card with ID: {}", id);

            // فراخوانی Service
            Card card = cardService.blockCard(id);

            // تبدیل به Response
            CardResponse response = mapToResponse(card);

            return Response.ok()
                    .entity(ApiResponse.success(response))
                    .build();

        } catch (IllegalArgumentException e) {
            log.warn("Validation error in card blocking: {}", e.getMessage());
            return Response.status(404)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (IllegalStateException e) {
            log.warn("Business logic error in card blocking: {}", e.getMessage());
            return Response.status(400)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (Exception e) {
            log.error("Error blocking card: {}", id, e);
            return Response.status(500)
                    .entity(ApiResponse.error("خطا در مسدودسازی کارت: " + e.getMessage()))
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
                    .map(this::mapToResponse)
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
                    .map(this::mapToResponse)
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

            CardResponse response = mapToResponse(cardOpt.get());

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

            CardResponse response = mapToResponse(cardOpt.get());

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
                    .map(this::mapToResponse)
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

    // ========== متدهای کمکی ==========

    /**
     * تبدیل Entity به DTO
     */
    private CardResponse mapToResponse(Card card) {
        return new CardResponse(
                card.getId(),
                maskCardNumber(card.getCardNumber()),
                card.getExpiryDate(),
                card.getType(),
                card.isActive(),
                card.getAccount() != null ? card.getAccount().getId() : null
        );
    }

    /**
     * پنهان کردن شماره کارت
     */
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        return "************" + cardNumber.substring(cardNumber.length() - 4);
    }

    // ========== Request/Response DTOs ==========

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
        private String cardNumber;
        private LocalDate expiryDate;
        private CardType type;
        private boolean active;
        private Long accountId;

        public CardResponse(Long id, String cardNumber, LocalDate expiryDate,
                            CardType type, boolean active, Long accountId) {
            this.id = id;
            this.cardNumber = cardNumber;
            this.expiryDate = expiryDate;
            this.type = type;
            this.active = active;
            this.accountId = accountId;
        }

        public Long getId() { return id; }
        public String getCardNumber() { return cardNumber; }
        public LocalDate getExpiryDate() { return expiryDate; }
        public CardType getType() { return type; }
        public boolean isActive() { return active; }
        public Long getAccountId() { return accountId; }
    }
}
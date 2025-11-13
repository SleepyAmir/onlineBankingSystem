package com.sleepy.onlinebankingsystem.controller.servlet;

import com.sleepy.onlinebankingsystem.model.entity.Card;
import com.sleepy.onlinebankingsystem.model.enums.UserRole;
import com.sleepy.onlinebankingsystem.service.CardService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@Slf4j
@WebServlet("/cards/detail")
public class CardDetailServlet extends HttpServlet {

    @Inject
    private CardService cardService;

    @Override
    @Transactional
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        try {
            String idParam = req.getParameter("id");
            String cardNumberParam = req.getParameter("cardNumber");

            if ((idParam == null || idParam.isBlank()) && 
                (cardNumberParam == null || cardNumberParam.isBlank())) {
                log.warn("Card detail requested without identifier");
                resp.sendRedirect(req.getContextPath() + "/cards/list?error=missing_id");
                return;
            }

            Optional<Card> cardOpt;
            
            if (cardNumberParam != null && !cardNumberParam.isBlank()) {
                cardOpt = cardService.findByCardNumber(cardNumberParam);
            } else {
                Long cardId = Long.parseLong(idParam);
                cardOpt = cardService.findById(cardId);
            }

            if (cardOpt.isEmpty()) {
                log.warn("Card not found");
                resp.sendRedirect(req.getContextPath() + "/cards/list?error=not_found");
                return;
            }

            Card card = cardOpt.get();

            HttpSession session = req.getSession(false);
            String currentUsername = (String) session.getAttribute("username");
            
            @SuppressWarnings("unchecked")
            Set<UserRole> userRoles = (Set<UserRole>) session.getAttribute("roles");

            if (!userRoles.contains(UserRole.ADMIN) && !userRoles.contains(UserRole.MANAGER)) {
                if (!card.getAccount().getUser().getUsername().equals(currentUsername)) {
                    log.warn("Unauthorized access to card {} by user {}", 
                            maskCardNumber(card.getCardNumber()), currentUsername);
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "دسترسی غیرمجاز");
                    return;
                }
            }

            req.setAttribute("card", card);

            log.info("Fetched details for card: {}", maskCardNumber(card.getCardNumber()));

            req.getRequestDispatcher("/views/cards/detail.jsp").forward(req, resp);

        } catch (Exception e) {
            log.error("Error fetching card details", e);
            req.setAttribute("error", "خطا در دریافت جزئیات کارت: " + e.getMessage());
            req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
        }
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
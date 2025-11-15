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
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@Slf4j
@WebServlet("/cards/block")
public class CardBlockServlet extends HttpServlet {

    @Inject
    private CardService cardService;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            HttpSession session = req.getSession(false);
            String currentUsername = (String) session.getAttribute("username");

            @SuppressWarnings("unchecked")
            Set<UserRole> userRoles = (Set<UserRole>) session.getAttribute("roles");

            String idParam = req.getParameter("id");
            if (idParam == null || idParam.isBlank()) {
                resp.sendRedirect(req.getContextPath() + "/cards/list?error=missing_id");
                return;
            }

            Long cardId = Long.parseLong(idParam);
            Optional<Card> cardOpt = cardService.findById(cardId);

            if (cardOpt.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/cards/list?error=not_found");
                return;
            }

            Card card = cardOpt.get();

            // بررسی دسترسی
            if (!userRoles.contains(UserRole.ADMIN) && !userRoles.contains(UserRole.MANAGER)) {
                if (!card.getAccount().getUser().getUsername().equals(currentUsername)) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "دسترسی غیرمجاز");
                    return;
                }
            }

            // فراخوانی Service
            cardService.blockCard(cardId);

            log.info("Card blocked by user: {}", currentUsername);

            resp.sendRedirect(req.getContextPath() + "/cards/detail?id=" +
                    cardId + "&message=card_blocked");

        } catch (IllegalArgumentException e) {
            log.warn("Validation error in card blocking: {}", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/cards/list?error=not_found");
        } catch (IllegalStateException e) {
            log.warn("Business error in card blocking: {}", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/cards/list?error=" + e.getMessage());
        } catch (Exception e) {
            log.error("Error blocking card", e);
            resp.sendRedirect(req.getContextPath() + "/cards/list?error=block_failed");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "از متد POST استفاده کنید");
    }
}

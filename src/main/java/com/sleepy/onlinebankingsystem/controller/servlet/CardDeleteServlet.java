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
@WebServlet("/cards/delete")
public class CardDeleteServlet extends HttpServlet {

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

            // بررسی دسترسی (فقط Admin)
            if (!userRoles.contains(UserRole.ADMIN)) {
                log.warn("Unauthorized card deletion attempt by user: {}", currentUsername);
                resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "فقط ادمین می‌تواند کارت را حذف کند");
                return;
            }

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

            // حذف نرم (Soft Delete)
            cardService.softDelete(cardId);

            log.info("Card soft-deleted by admin: {}", currentUsername);

            resp.sendRedirect(req.getContextPath() + "/cards/list?message=card_deleted");

        } catch (Exception e) {
            log.error("Error deleting card", e);
            resp.sendRedirect(req.getContextPath() + "/cards/list?error=delete_failed");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "از متد POST استفاده کنید");
    }
}
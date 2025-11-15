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
import java.util.Set;

@Slf4j
@WebServlet("/cards/activate")
public class CardActivateServlet extends HttpServlet {

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

            // بررسی دسترسی
            if (!userRoles.contains(UserRole.ADMIN) && !userRoles.contains(UserRole.MANAGER)) {
                log.warn("Unauthorized card activation attempt by user: {}", currentUsername);
                resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "فقط مدیر می‌تواند کارت را فعال کند");
                return;
            }

            // فراخوانی Service
            Card card = cardService.activateCard(cardId);

            log.info("Card activated by manager: {}", currentUsername);

            resp.sendRedirect(req.getContextPath() + "/cards/detail?id=" +
                    cardId + "&message=card_activated");

        } catch (IllegalArgumentException e) {
            log.warn("Validation error in card activation: {}", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/cards/list?error=not_found");
        } catch (IllegalStateException e) {
            log.warn("Business error in card activation: {}", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/cards/list?error=" + e.getMessage());
        } catch (Exception e) {
            log.error("Error activating card", e);
            resp.sendRedirect(req.getContextPath() + "/cards/list?error=activate_failed");
        }
    }

    @Override
    @Transactional
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "از متد POST استفاده کنید");
    }
}

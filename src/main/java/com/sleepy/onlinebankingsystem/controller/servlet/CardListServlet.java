package com.sleepy.onlinebankingsystem.controller.servlet;

import com.sleepy.onlinebankingsystem.model.entity.Card;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.model.enums.UserRole;
import com.sleepy.onlinebankingsystem.service.CardService;
import com.sleepy.onlinebankingsystem.service.UserService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@WebServlet("/cards/list")
public class CardListServlet extends HttpServlet {

    @Inject private CardService cardService;
    @Inject private UserService userService;

    private static final int PAGE_SIZE = 10;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            HttpSession session = req.getSession(false);
            if (session == null) {
                resp.sendRedirect(req.getContextPath() + "/auth/login");
                return;
            }

            String currentUsername = (String) session.getAttribute("username");
            @SuppressWarnings("unchecked")
            Set<UserRole> userRoles = (Set<UserRole>) session.getAttribute("roles");

            // --- پارامترها ---
            int page = parsePage(req.getParameter("page"));
            Boolean filterActive = parseActive(req.getParameter("active"));

            List<Card> cards;

            // --- Admin / Manager ---
            if (userRoles.contains(UserRole.ADMIN) || userRoles.contains(UserRole.MANAGER)) {

                String userIdParam = req.getParameter("userId");

                if (userIdParam != null && !userIdParam.isBlank()) {
                    Long userId = Long.parseLong(userIdParam);
                    Optional<User> userOpt = userService.findById(userId);

                    if (userOpt.isPresent()) {
                        User targetUser = userOpt.get();
                        // متد جدید با JOIN FETCH user
                        cards = cardService.findByUserWithAccountAndUser(targetUser.getId());
                        req.setAttribute("selectedUser", targetUser);
                    } else {
                        cards = getFilteredCards(filterActive, page);
                    }
                } else {
                    cards = getFilteredCards(filterActive, page);
                }

                req.setAttribute("users", userService.findActiveUsers());

            } else {
                // --- کاربر عادی ---
                Optional<User> userOpt = userService.findByUsername(currentUsername);
                if (userOpt.isEmpty()) {
                    resp.sendRedirect(req.getContextPath() + "/auth/login?error=user_not_found");
                    return;
                }

                User user = userOpt.get();
                cards = cardService.findByUserWithAccountAndUser(user.getId()); // متد جدید

                if (filterActive != null) {
                    boolean active = filterActive;
                    cards = cards.stream()
                            .filter(card -> card.isActive() == active)
                            .collect(Collectors.toList());
                }
            }

            // --- ارسال به JSP ---
            req.setAttribute("cards", cards);
            req.setAttribute("currentPage", page);
            req.setAttribute("pageSize", PAGE_SIZE);
            req.setAttribute("selectedActive", filterActive);

            log.info("Fetched {} cards for user: {}", cards.size(), currentUsername);

            req.getRequestDispatcher("/views/cards/list.jsp").forward(req, resp);

        } catch (Exception e) {
            log.error("Error fetching card list", e);
            req.setAttribute("error", "خطا در دریافت لیست کارت‌ها: " + e.getMessage());
            req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
        }
    }

    // --- متدهای کمکی ---
    private int parsePage(String pageParam) {
        if (pageParam == null) return 0;
        try {
            int page = Integer.parseInt(pageParam);
            return page < 0 ? 0 : page;
        } catch (NumberFormatException e) {
            log.warn("Invalid page parameter: {}", pageParam);
            return 0;
        }
    }

    private Boolean parseActive(String activeParam) {
        if (activeParam == null || activeParam.isBlank()) return null;
        return Boolean.parseBoolean(activeParam);
    }

    private List<Card> getFilteredCards(Boolean filterActive, int page) throws Exception {
        if (filterActive != null && filterActive) {
            return cardService.findActiveCards();
        } else {
            return cardService.findAll(page, PAGE_SIZE);
        }
    }

}
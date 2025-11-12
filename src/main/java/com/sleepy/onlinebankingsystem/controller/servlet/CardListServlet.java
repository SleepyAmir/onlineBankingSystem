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

    @Inject
    private CardService cardService;

    @Inject
    private UserService userService;

    private static final int PAGE_SIZE = 10;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        try {
            HttpSession session = req.getSession(false);
            String currentUsername = (String) session.getAttribute("username");
            
            @SuppressWarnings("unchecked")
            Set<UserRole> userRoles = (Set<UserRole>) session.getAttribute("roles");

            // 1️⃣ دریافت شماره صفحه
            String pageParam = req.getParameter("page");
            int page = 0;
            
            if (pageParam != null) {
                try {
                    page = Integer.parseInt(pageParam);
                    if (page < 0) page = 0;
                } catch (NumberFormatException e) {
                    log.warn("Invalid page parameter: {}", pageParam);
                    page = 0;
                }
            }

            // 2️⃣ دریافت فیلتر وضعیت (فعال/غیرفعال)
            String activeParam = req.getParameter("active");
            Boolean filterActive = null;
            
            if (activeParam != null && !activeParam.isBlank()) {
                filterActive = Boolean.parseBoolean(activeParam);
            }

            List<Card> cards;

            // 3️⃣ اگر Admin یا Manager است، همه کارت‌ها را نمایش بده
            if (userRoles.contains(UserRole.ADMIN) || userRoles.contains(UserRole.MANAGER)) {
                
                String userIdParam = req.getParameter("userId");
                
                if (userIdParam != null && !userIdParam.isBlank()) {
                    // کارت‌های یک کاربر خاص
                    Long userId = Long.parseLong(userIdParam);
                    Optional<User> userOpt = userService.findById(userId);
                    
                    if (userOpt.isPresent()) {
                        cards = cardService.findByUser(userOpt.get());
                        req.setAttribute("selectedUser", userOpt.get());
                    } else {
                        cards = filterActive != null && filterActive ? 
                                cardService.findActiveCards() : 
                                cardService.findAll(page, PAGE_SIZE);
                    }
                } else if (filterActive != null && filterActive) {
                    // فقط کارت‌های فعال
                    cards = cardService.findActiveCards();
                } else {
                    // همه کارت‌ها
                    cards = cardService.findAll(page, PAGE_SIZE);
                }

                // ارسال لیست کاربران برای فیلتر
                req.setAttribute("users", userService.findActiveUsers());
            } else {
                // 4️⃣ کاربر عادی فقط کارت‌های خودش را می‌بیند
                Optional<User> userOpt = userService.findByUsername(currentUsername);
                
                if (userOpt.isEmpty()) {
                    resp.sendRedirect(req.getContextPath() + "/auth/login?error=user_not_found");
                    return;
                }
                
                cards = cardService.findByUser(userOpt.get());

                // فیلتر بر اساس وضعیت (اختیاری)
                if (filterActive != null) {
                    final Boolean active = filterActive;
                    cards = cards.stream()
                            .filter(card -> card.isActive() == active)
                            .collect(Collectors.toList());
                }
            }

            // 5️⃣ ارسال اطلاعات به JSP
            req.setAttribute("cards", cards);
            req.setAttribute("currentPage", page);
            req.setAttribute("pageSize", PAGE_SIZE);
            req.setAttribute("selectedActive", filterActive);

            log.info("Fetched {} cards for user: {}", cards.size(), currentUsername);

            // 6️⃣ نمایش JSP
            req.getRequestDispatcher("/views/cards/list.jsp").forward(req, resp);

        } catch (Exception e) {
            log.error("Error fetching card list", e);
            req.setAttribute("error", "خطا در دریافت لیست کارت‌ها: " + e.getMessage());
            req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
        }
    }
}
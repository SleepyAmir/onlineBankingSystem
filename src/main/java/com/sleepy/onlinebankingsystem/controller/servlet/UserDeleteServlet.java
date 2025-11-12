package com.sleepy.onlinebankingsystem.controller.servlet;

import com.sleepy.onlinebankingsystem.model.entity.User;
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
import java.util.Optional;

@Slf4j
@WebServlet("/users/delete")
public class UserDeleteServlet extends HttpServlet {

    @Inject
    private UserService userService;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        try {
            // 1️⃣ دریافت ID کاربر
            String idParam = req.getParameter("id");
            
            if (idParam == null || idParam.isBlank()) {
                log.warn("Delete attempt without user ID");
                resp.sendRedirect(req.getContextPath() + "/users/list?error=missing_id");
                return;
            }

            Long userId;
            try {
                userId = Long.parseLong(idParam);
            } catch (NumberFormatException e) {
                log.warn("Invalid user ID for deletion: {}", idParam);
                resp.sendRedirect(req.getContextPath() + "/users/list?error=invalid_id");
                return;
            }

            // 2️⃣ بررسی وجود کاربر
            Optional<User> userOpt = userService.findById(userId);
            
            if (userOpt.isEmpty()) {
                log.warn("User not found for deletion: ID {}", userId);
                resp.sendRedirect(req.getContextPath() + "/users/list?error=not_found");
                return;
            }

            User user = userOpt.get();

            // 3️⃣ جلوگیری از حذف خودش توسط کاربر
            HttpSession session = req.getSession(false);
            String currentUsername = (String) session.getAttribute("username");
            
            if (user.getUsername().equals(currentUsername)) {
                log.warn("User attempted to delete themselves: {}", currentUsername);
                resp.sendRedirect(req.getContextPath() + "/users/list?error=cannot_delete_self");
                return;
            }

            // 4️⃣ حذف نرم (Soft Delete)
            userService.softDelete(userId);

            log.info("User soft-deleted successfully: {} by admin: {}", 
                    user.getUsername(), currentUsername);

            // 5️⃣ هدایت به لیست با پیام موفقیت
            resp.sendRedirect(req.getContextPath() + "/users/list?message=deleted");

        } catch (Exception e) {
            log.error("Error deleting user", e);
            resp.sendRedirect(req.getContextPath() + "/users/list?error=delete_failed");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // حذف فقط با POST مجاز است
        resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "از متد POST استفاده کنید");
    }
}
package com.sleepy.onlinebankingsystem.controller.servlet;

import com.sleepy.onlinebankingsystem.model.entity.Token;
import com.sleepy.onlinebankingsystem.security.SessionManager;
import com.sleepy.onlinebankingsystem.service.TokenService;
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
@WebServlet("/auth/logout")
public class LogoutServlet extends HttpServlet {

    @Inject
    private TokenService tokenService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            // 1️⃣ دریافت Session
            HttpSession session = req.getSession(false);

            if (session != null) {
                // 2️⃣ دریافت اطلاعات کاربر از Session
                String username = (String) session.getAttribute("username");

                if (username != null) {
                    log.info("Logout attempt for user: {}", username);

                    // 3️⃣ حذف Token از دیتابیس (Soft Delete)
                    try {
                        Optional<Token> tokenOpt = tokenService.findByUsername(username);
                        if (tokenOpt.isPresent()) {
                            tokenService.softDelete(tokenOpt.get().getId());
                            log.info("Token deleted for user: {}", username);
                        }
                    } catch (Exception e) {
                        log.error("Error deleting token for user: {}", username, e);
                    }

                    // 4️⃣ حذف Session از SessionManager
                    SessionManager.removeSession(username);

                    log.info("Logout successful for user: {}", username);
                }

                // 5️⃣ نابود کردن Session
                session.invalidate();
            }

            // 6️⃣ هدایت به صفحه لاگین
            resp.sendRedirect(req.getContextPath() + "/auth/login?message=logout");

        } catch (Exception e) {
            log.error("Error during logout", e);
            resp.sendRedirect(req.getContextPath() + "/auth/login?error=logout_failed");
        }
    }
}
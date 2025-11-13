package com.sleepy.onlinebankingsystem.controller.servlet;

import com.sleepy.onlinebankingsystem.model.enums.UserRole;
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
@WebServlet("/user-dashboard")
public class HomeDashboardServlet extends HttpServlet {

    @Override
    @Transactional
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        HttpSession session = req.getSession(false);
        
        if (session == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        @SuppressWarnings("unchecked")
        Set<UserRole> userRoles = (Set<UserRole>) session.getAttribute("roles");
        String username = (String) session.getAttribute("username");

        if (userRoles == null || userRoles.isEmpty()) {
            log.warn("User {} has no roles", username);
            resp.sendRedirect(req.getContextPath() + "/auth/login?error=no_role");
            return;
        }

        // هدایت به داشبورد مناسب بر اساس نقش
        if (userRoles.contains(UserRole.ADMIN)) {
            resp.sendRedirect(req.getContextPath() + "/admin/user-dashboard");
        } else if (userRoles.contains(UserRole.MANAGER)) {
            resp.sendRedirect(req.getContextPath() + "/manager/user-dashboard");
        } else if (userRoles.contains(UserRole.CUSTOMER)) {
            resp.sendRedirect(req.getContextPath() + "/customer/user-dashboard");
        } else {
            log.error("Unknown role for user: {}", username);
            resp.sendRedirect(req.getContextPath() + "/auth/login?error=unknown_role");
        }
    }
}
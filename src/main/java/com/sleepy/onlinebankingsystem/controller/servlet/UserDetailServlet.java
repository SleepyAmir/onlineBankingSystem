package com.sleepy.onlinebankingsystem.controller.servlet;

import com.sleepy.onlinebankingsystem.model.entity.Role;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.service.RoleService;
import com.sleepy.onlinebankingsystem.service.UserService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@WebServlet("/users/detail")
public class UserDetailServlet extends HttpServlet {

    @Inject
    private UserService userService;

    @Inject
    private RoleService roleService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        try {
            // 1️⃣ دریافت ID کاربر از پارامتر
            String idParam = req.getParameter("id");
            
            if (idParam == null || idParam.isBlank()) {
                log.warn("User detail requested without ID");
                resp.sendRedirect(req.getContextPath() + "/users/list?error=missing_id");
                return;
            }

            Long userId;
            try {
                userId = Long.parseLong(idParam);
            } catch (NumberFormatException e) {
                log.warn("Invalid user ID: {}", idParam);
                resp.sendRedirect(req.getContextPath() + "/users/list?error=invalid_id");
                return;
            }

            // 2️⃣ پیدا کردن کاربر
            Optional<User> userOpt = userService.findById(userId);
            
            if (userOpt.isEmpty()) {
                log.warn("User not found with ID: {}", userId);
                resp.sendRedirect(req.getContextPath() + "/users/list?error=not_found");
                return;
            }

            User user = userOpt.get();

            // 3️⃣ دریافت نقش‌های کاربر
            List<Role> roles = roleService.findByUser(user);

            // 4️⃣ ارسال اطلاعات به JSP
            req.setAttribute("user", user);
            req.setAttribute("roles", roles);

            log.info("Fetched details for user: {}", user.getUsername());

            // 5️⃣ نمایش JSP
            req.getRequestDispatcher("/views/users/detail.jsp").forward(req, resp);

        } catch (Exception e) {
            log.error("Error fetching user details", e);
            req.setAttribute("error", "خطا در دریافت جزئیات کاربر: " + e.getMessage());
            req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
        }
    }
}
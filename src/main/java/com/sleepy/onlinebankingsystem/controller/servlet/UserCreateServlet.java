package com.sleepy.onlinebankingsystem.controller.servlet;

import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.model.enums.UserRole;
import com.sleepy.onlinebankingsystem.service.UserService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * سرولت ایجاد کاربر توسط ادمین
 * تمام بیزنس لاجیک در UserService
 */
@Slf4j
@WebServlet("/users/create")
public class UserCreateServlet extends HttpServlet {

    @Inject
    private UserService userService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // نمایش فرم با لیست نقش‌ها
        req.setAttribute("availableRoles", UserRole.values());
        req.getRequestDispatcher("/views/users/create.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            // 1️⃣ دریافت پارامترها
            String username = req.getParameter("username");
            String password = req.getParameter("password");
            String firstName = req.getParameter("firstName");
            String lastName = req.getParameter("lastName");
            String phone = req.getParameter("phone");
            String nationalCode = req.getParameter("nationalCode");
            String roleParam = req.getParameter("role");
            String activeParam = req.getParameter("active");

            // 2️⃣ تبدیل پارامترها
            UserRole role = UserRole.valueOf(roleParam);
            boolean active = "on".equals(activeParam) || "true".equals(activeParam);

            log.info("Admin creating user: {} with role: {}", username, role);

            // 3️⃣ فراخوانی Service
            User savedUser = userService.createUserByAdmin(
                    username,
                    password,
                    firstName,
                    lastName,
                    phone,
                    nationalCode,
                    role,
                    active
            );

            log.info("User created successfully by admin: {}", savedUser.getUsername());

            // 4️⃣ هدایت به صفحه جزئیات
            resp.sendRedirect(req.getContextPath() + "/users/detail?id=" +
                    savedUser.getId() + "&message=created");

        } catch (IllegalArgumentException e) {
            log.warn("Validation error in user creation: {}", e.getMessage());
            setError(req, resp, e.getMessage());
        } catch (Exception e) {
            log.error("Error creating user", e);
            setError(req, resp, "خطا در ایجاد کاربر: " + e.getMessage());
        }
    }

    /**
     * نمایش خطا و بازگشت به فرم
     */
    private void setError(HttpServletRequest req, HttpServletResponse resp, String message)
            throws ServletException, IOException {
        req.setAttribute("error", message);
        req.setAttribute("availableRoles", UserRole.values());
        req.getRequestDispatcher("/views/users/create.jsp").forward(req, resp);
    }
}
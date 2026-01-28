package com.sleepy.onlinebankingsystem.controller.servlet;

import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.service.UserService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@WebServlet("/auth/register")
public class RegisterServlet extends HttpServlet {

    @Inject
    private UserService userService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/views/auth/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text/html; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        try {
            // 1️⃣ دریافت پارامترها
            String username = req.getParameter("username");
            String password = req.getParameter("password");
            String confirmPassword = req.getParameter("confirmPassword");
            String firstName = req.getParameter("firstName");
            String lastName = req.getParameter("lastName");
            String phone = req.getParameter("phone");
            String nationalCode = req.getParameter("nationalCode");

            log.info("Registration attempt for username: {}", username);

            // 2️⃣ بررسی تطابق رمز عبور
            if (!password.equals(confirmPassword)) {
                setError(req, resp, "رمز عبور و تکرار آن یکسان نیستند");
                return;
            }

            // 3️⃣ فراخوانی Service (تمام اعتبارسنجی اونجاست)
            User savedUser = userService.registerUser(
                    username,
                    password,
                    firstName,
                    lastName,
                    phone,
                    nationalCode
            );

            log.info("Registration successful for user: {}", savedUser.getUsername());

            // 4️⃣ هدایت به لاگین
            resp.sendRedirect(req.getContextPath() + "/auth/login?message=registered");

        } catch (IllegalArgumentException e) {
            log.warn("Validation error during registration: {}", e.getMessage());
            setError(req, resp, e.getMessage());
        } catch (Exception e) {
            log.error("Error during registration", e);
            setError(req, resp, "خطای سرور: " + e.getMessage());
        }
    }

    /**
     * نمایش خطا و بازگشت به فرم
     */
    private void setError(HttpServletRequest req, HttpServletResponse resp, String message)
            throws ServletException, IOException {
        req.setAttribute("error", message);
        req.getRequestDispatcher("/views/auth/register.jsp").forward(req, resp);
    }
}
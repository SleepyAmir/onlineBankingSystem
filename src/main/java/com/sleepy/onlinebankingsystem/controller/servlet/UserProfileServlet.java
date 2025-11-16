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
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * سرولت پروفایل کاربر
 * تمام بیزنس لاجیک در UserService
 */
@Slf4j
@WebServlet("/user-profile")
public class UserProfileServlet extends HttpServlet {

    @Inject
    private UserService userService;

    @Inject
    private RoleService roleService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            // 1️⃣ دریافت نام کاربری از Session
            HttpSession session = req.getSession(false);
            String username = (String) session.getAttribute("username");

            if (username == null) {
                resp.sendRedirect(req.getContextPath() + "/auth/login?error=session_expired");
                return;
            }

            // 2️⃣ پیدا کردن کاربر
            Optional<User> userOpt = userService.findByUsername(username);
            if (userOpt.isEmpty()) {
                log.error("Logged-in user not found: {}", username);
                resp.sendRedirect(req.getContextPath() + "/auth/login?error=user_not_found");
                return;
            }

            User user = userOpt.get();
            List<Role> roles = roleService.findByUser(user);

            // 3️⃣ ارسال به JSP
            req.setAttribute("user", user);
            req.setAttribute("roles", roles);
            req.getRequestDispatcher("/views/users/profile.jsp").forward(req, resp);

        } catch (Exception e) {
            log.error("Error loading profile", e);
            setError(req, resp, "خطا در بارگذاری پروفایل: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            // 1️⃣ دریافت نام کاربری از Session
            HttpSession session = req.getSession(false);
            String username = (String) session.getAttribute("username");

            if (username == null) {
                resp.sendRedirect(req.getContextPath() + "/auth/login?error=session_expired");
                return;
            }

            // 2️⃣ پیدا کردن کاربر
            Optional<User> userOpt = userService.findByUsername(username);
            if (userOpt.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/auth/login?error=user_not_found");
                return;
            }

            User user = userOpt.get();

            // 3️⃣ دریافت پارامترها
            String firstName = req.getParameter("firstName");
            String lastName = req.getParameter("lastName");
            String phone = req.getParameter("phone");
            String currentPassword = req.getParameter("currentPassword");
            String newPassword = req.getParameter("newPassword");
            String confirmPassword = req.getParameter("confirmPassword");

            log.info("Updating profile for user: {}", username);

            // 4️⃣ به‌روزرسانی اطلاعات پایه
            if (firstName != null && !firstName.isBlank()) {
                user.setFirstName(firstName);
            }
            if (lastName != null && !lastName.isBlank()) {
                user.setLastName(lastName);
            }
            if (phone != null && !phone.isBlank()) {
                user.setPhone(phone);
            }

            // 5️⃣ تغییر رمز عبور (اگر وارد شده)
            if (newPassword != null && !newPassword.isBlank()) {
                // بررسی تطابق رمز جدید
                if (!newPassword.equals(confirmPassword)) {
                    setErrorWithUser(req, resp, user, "رمز عبور جدید و تکرار آن یکسان نیستند");
                    return;
                }

                // فراخوانی Service برای تغییر رمز (اعتبارسنجی اونجاست)
                userService.changePassword(user.getId(), currentPassword, newPassword);

                log.info("Password changed for user: {}", username);
            }

            // 6️⃣ ذخیره تغییرات
            userService.update(user);

            // 7️⃣ به‌روزرسانی Session
            session.setAttribute("fullName", user.getFirstName() + " " + user.getLastName());

            log.info("Profile updated successfully for user: {}", username);

            // 8️⃣ نمایش پیام موفقیت
            setSuccessWithUser(req, resp, user, "پروفایل شما با موفقیت به‌روزرسانی شد");

        } catch (IllegalArgumentException e) {
            log.warn("Validation error in profile update: {}", e.getMessage());
            setErrorWithoutRedirect(req, resp, e.getMessage());
        } catch (Exception e) {
            log.error("Error updating profile", e);
            setError(req, resp, "خطا در به‌روزرسانی: " + e.getMessage());
        }
    }

    /**
     * نمایش خطا با اطلاعات کاربر
     */
    private void setErrorWithUser(HttpServletRequest req, HttpServletResponse resp,
                                  User user, String message)
            throws ServletException, IOException {
        try {
            req.setAttribute("error", message);
            req.setAttribute("user", user);
            req.setAttribute("roles", roleService.findByUser(user));
            req.getRequestDispatcher("/views/users/profile.jsp").forward(req, resp);
        } catch (Exception e) {
            log.error("Error setting error with user", e);
            setError(req, resp, message);
        }
    }

    /**
     * نمایش موفقیت با اطلاعات کاربر
     */
    private void setSuccessWithUser(HttpServletRequest req, HttpServletResponse resp,
                                    User user, String message)
            throws ServletException, IOException {
        try {
            req.setAttribute("success", message);
            req.setAttribute("user", user);
            req.setAttribute("roles", roleService.findByUser(user));
            req.getRequestDispatcher("/views/users/profile.jsp").forward(req, resp);
        } catch (Exception e) {
            log.error("Error setting success with user", e);
            resp.sendRedirect(req.getContextPath() + "/user-profile");
        }
    }

    /**
     * نمایش خطا بدون redirect
     */
    private void setErrorWithoutRedirect(HttpServletRequest req, HttpServletResponse resp,
                                         String message)
            throws ServletException, IOException {
        req.setAttribute("error", message);
        doGet(req, resp);
    }

    /**
     * نمایش خطا و redirect به error page
     */
    private void setError(HttpServletRequest req, HttpServletResponse resp, String message)
            throws ServletException, IOException {
        req.setAttribute("error", message);
        req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
    }
}
package com.sleepy.onlinebankingsystem.controller.servlet;

import com.sleepy.onlinebankingsystem.model.entity.Role;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.model.enums.UserRole;
import com.sleepy.onlinebankingsystem.service.RoleService;
import com.sleepy.onlinebankingsystem.service.UserService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * سرولت ویرایش کاربر
 * تمام بیزنس لاجیک در UserService
 */
@Slf4j
@WebServlet("/users/edit")
public class UserEditServlet extends HttpServlet {

    @Inject
    private UserService userService;

    @Inject
    private RoleService roleService;

    @Override
    @Transactional
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            // 1️⃣ دریافت ID
            String idParam = req.getParameter("id");
            if (idParam == null || idParam.isBlank()) {
                resp.sendRedirect(req.getContextPath() + "/users/list?error=missing_id");
                return;
            }

            Long userId = Long.parseLong(idParam);

            // 2️⃣ پیدا کردن کاربر
            Optional<User> userOpt = userService.findById(userId);
            if (userOpt.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/users/list?error=not_found");
                return;
            }

            User user = userOpt.get();
            List<Role> userRoles = roleService.findByUser(user);

            // 3️⃣ ارسال به JSP
            req.setAttribute("user", user);
            req.setAttribute("userRoles", userRoles);
            req.setAttribute("availableRoles", UserRole.values());
            req.getRequestDispatcher("/views/users/edit.jsp").forward(req, resp);

        } catch (Exception e) {
            log.error("Error loading edit form", e);
            setError(req, resp, "خطا در بارگذاری فرم: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            // 1️⃣ دریافت ID
            String idParam = req.getParameter("id");
            if (idParam == null || idParam.isBlank()) {
                resp.sendRedirect(req.getContextPath() + "/users/list?error=missing_id");
                return;
            }

            Long userId = Long.parseLong(idParam);

            // 2️⃣ پیدا کردن کاربر
            Optional<User> userOpt = userService.findById(userId);
            if (userOpt.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/users/list?error=not_found");
                return;
            }

            User user = userOpt.get();

            // 3️⃣ دریافت پارامترها
            String firstName = req.getParameter("firstName");
            String lastName = req.getParameter("lastName");
            String phone = req.getParameter("phone");
            String nationalCode = req.getParameter("nationalCode");
            String password = req.getParameter("password");
            String activeParam = req.getParameter("active");

            log.info("Updating user: {}", user.getUsername());

            // 4️⃣ به‌روزرسانی فیلدها (فقط اگر مقدار دارن)
            if (firstName != null && !firstName.isBlank()) {
                user.setFirstName(firstName);
            }
            if (lastName != null && !lastName.isBlank()) {
                user.setLastName(lastName);
            }
            if (phone != null && !phone.isBlank()) {
                user.setPhone(phone);
            }
            if (nationalCode != null && !nationalCode.isBlank()) {
                user.setNationalCode(nationalCode);
            }

            boolean active = "on".equals(activeParam) || "true".equals(activeParam);
            user.setActive(active);

            // 5️⃣ تغییر رمز عبور (اگر وارد شده)
            if (password != null && !password.isBlank()) {
                // فرض: رمز فعلی در فرم وارد نشده، فقط ادمین می‌تونه بدون رمز فعلی تغییر بده
                // برای امنیت بیشتر باید currentPassword هم بگیری
                userService.changePassword(userId, user.getPassword(), password);
            }

            // 6️⃣ ذخیره (اعتبارسنجی داخل Service)
            userService.update(user);

            log.info("User updated successfully: {}", user.getUsername());

            // 7️⃣ هدایت به صفحه جزئیات
            resp.sendRedirect(req.getContextPath() + "/users/detail?id=" +
                    userId + "&message=updated");

        } catch (IllegalArgumentException e) {
            log.warn("Validation error in user update: {}", e.getMessage());
            setError(req, resp, e.getMessage());
        } catch (Exception e) {
            log.error("Error updating user", e);
            setError(req, resp, "خطا در به‌روزرسانی: " + e.getMessage());
        }
    }

    /**
     * نمایش خطا و بازگشت به JSP
     */
    private void setError(HttpServletRequest req, HttpServletResponse resp, String message)
            throws ServletException, IOException {
        req.setAttribute("error", message);
        req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
    }
}
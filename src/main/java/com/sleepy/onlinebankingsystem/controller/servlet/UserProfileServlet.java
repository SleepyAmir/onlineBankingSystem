package com.sleepy.onlinebankingsystem.controller.servlet;

import com.sleepy.onlinebankingsystem.model.entity.Role;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.service.RoleService;
import com.sleepy.onlinebankingsystem.service.UserService;
import com.sleepy.onlinebankingsystem.utils.PasswordUtil;
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
import java.util.regex.Pattern;

@Slf4j
@WebServlet("/user-profile")
public class UserProfileServlet extends HttpServlet {

    @Inject
    private UserService userService;

    @Inject
    private RoleService roleService;

    @Inject
    private PasswordUtil passwordUtil;

    private static final Pattern PHONE_PATTERN = Pattern.compile("^09[0-9]{9}$");

    @Override
    @Transactional
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

            // 3️⃣ دریافت نقش‌های کاربر
            List<Role> roles = roleService.findByUser(user);

            // 4️⃣ ارسال اطلاعات به JSP
            req.setAttribute("user", user);
            req.setAttribute("roles", roles);

            // 5️⃣ نمایش پروفایل
            req.getRequestDispatcher("/views/user-profile.jsp").forward(req, resp);

        } catch (Exception e) {
            log.error("Error loading profile", e);
            req.setAttribute("error", "خطا در بارگذاری پروفایل: " + e.getMessage());
            req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
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

            // 3️⃣ دریافت پارامترهای فرم
            String firstName = req.getParameter("firstName");
            String lastName = req.getParameter("lastName");
            String phone = req.getParameter("phone");
            String currentPassword = req.getParameter("currentPassword");
            String newPassword = req.getParameter("newPassword");
            String confirmPassword = req.getParameter("confirmPassword");

            // 4️⃣ اعتبارسنجی
            String validationError = validateProfileInput(firstName, lastName, phone);
            
            if (validationError != null) {
                req.setAttribute("error", validationError);
                req.setAttribute("user", user);
                req.getRequestDispatcher("/views/user-profile.jsp").forward(req, resp);
                return;
            }

            // 5️⃣ به‌روزرسانی اطلاعات پایه
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPhone(phone);

            // 6️⃣ تغییر رمز عبور (اختیاری)
            if (newPassword != null && !newPassword.isBlank()) {
                // بررسی رمز عبور فعلی
                if (currentPassword == null || currentPassword.isBlank()) {
                    req.setAttribute("error", "برای تغییر رمز عبور، ابتدا رمز فعلی را وارد کنید");
                    req.setAttribute("user", user);
                    req.getRequestDispatcher("/views/user-profile.jsp").forward(req, resp);
                    return;
                }

                if (!passwordUtil.matches(currentPassword, user.getPassword())) {
                    req.setAttribute("error", "رمز عبور فعلی اشتباه است");
                    req.setAttribute("user", user);
                    req.getRequestDispatcher("/views/user-profile.jsp").forward(req, resp);
                    return;
                }

                // بررسی تطابق رمز جدید با تکرار آن
                if (!newPassword.equals(confirmPassword)) {
                    req.setAttribute("error", "رمز عبور جدید و تکرار آن یکسان نیستند");
                    req.setAttribute("user", user);
                    req.getRequestDispatcher("/views/user-profile.jsp").forward(req, resp);
                    return;
                }

                // بررسی طول رمز جدید
                if (newPassword.length() < 6) {
                    req.setAttribute("error", "رمز عبور جدید باید حداقل 6 کاراکتر باشد");
                    req.setAttribute("user", user);
                    req.getRequestDispatcher("/views/user-profile.jsp").forward(req, resp);
                    return;
                }

                // هش و ذخیره رمز جدید
                String hashedPassword = passwordUtil.hash(newPassword);
                user.setPassword(hashedPassword);

                log.info("Password changed for user: {}", username);
            }

            // 7️⃣ ذخیره تغییرات
            userService.update(user);

            // 8️⃣ به‌روزرسانی Session
            session.setAttribute("fullName", user.getFirstName() + " " + user.getLastName());

            log.info("Profile updated successfully for user: {}", username);

            // 9️⃣ نمایش پیام موفقیت
            req.setAttribute("success", "پروفایل شما با موفقیت به‌روزرسانی شد");
            req.setAttribute("user", user);
            
            List<Role> roles = roleService.findByUser(user);
            req.setAttribute("roles", roles);
            
            req.getRequestDispatcher("/views/user-profile.jsp").forward(req, resp);

        } catch (Exception e) {
            log.error("Error updating profile", e);
            req.setAttribute("error", "خطا در به‌روزرسانی پروفایل: " + e.getMessage());
            req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
        }
    }

    private String validateProfileInput(String firstName, String lastName, String phone) {
        
        if (firstName == null || firstName.isBlank()) {
            return "نام الزامی است";
        }

        if (lastName == null || lastName.isBlank()) {
            return "نام خانوادگی الزامی است";
        }

        if (phone == null || !PHONE_PATTERN.matcher(phone).matches()) {
            return "شماره تلفن نامعتبر است (فرمت: 09xxxxxxxxx)";
        }

        return null;
    }
}
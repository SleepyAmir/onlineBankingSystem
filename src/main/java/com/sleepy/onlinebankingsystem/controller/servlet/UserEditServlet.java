package com.sleepy.onlinebankingsystem.controller.servlet;

import com.sleepy.onlinebankingsystem.model.entity.Role;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.model.enums.UserRole;
import com.sleepy.onlinebankingsystem.service.RoleService;
import com.sleepy.onlinebankingsystem.service.UserService;
import com.sleepy.onlinebankingsystem.utils.PasswordUtil;
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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@WebServlet("/users/edit")
public class UserEditServlet extends HttpServlet {

    @Inject
    private UserService userService;

    @Inject
    private RoleService roleService;

    @Inject
    private PasswordUtil passwordUtil;

    private static final Pattern PHONE_PATTERN = Pattern.compile("^09[0-9]{9}$");
    private static final Pattern NATIONAL_CODE_PATTERN = Pattern.compile("^[0-9]{10}$");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        try {
            // 1️⃣ دریافت ID کاربر
            String idParam = req.getParameter("id");
            
            if (idParam == null || idParam.isBlank()) {
                resp.sendRedirect(req.getContextPath() + "/users/list?error=missing_id");
                return;
            }

            Long userId;
            try {
                userId = Long.parseLong(idParam);
            } catch (NumberFormatException e) {
                resp.sendRedirect(req.getContextPath() + "/users/list?error=invalid_id");
                return;
            }

            // 2️⃣ پیدا کردن کاربر
            Optional<User> userOpt = userService.findById(userId);
            
            if (userOpt.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/users/list?error=not_found");
                return;
            }

            User user = userOpt.get();

            // 3️⃣ دریافت نقش‌های کاربر
            List<Role> userRoles = roleService.findByUser(user);

            // 4️⃣ ارسال اطلاعات به JSP
            req.setAttribute("user", user);
            req.setAttribute("userRoles", userRoles);
            req.setAttribute("availableRoles", UserRole.values());

            // 5️⃣ نمایش فرم ویرایش
            req.getRequestDispatcher("/WEB-INF/views/users/edit.jsp").forward(req, resp);

        } catch (Exception e) {
            log.error("Error loading edit form", e);
            req.setAttribute("error", "خطا در بارگذاری فرم: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        try {
            // 1️⃣ دریافت ID کاربر
            String idParam = req.getParameter("id");
            
            if (idParam == null || idParam.isBlank()) {
                resp.sendRedirect(req.getContextPath() + "/users/list?error=missing_id");
                return;
            }

            Long userId = Long.parseLong(idParam);

            // 2️⃣ پیدا کردن کاربر موجود
            Optional<User> userOpt = userService.findById(userId);
            
            if (userOpt.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/users/list?error=not_found");
                return;
            }

            User user = userOpt.get();

            // 3️⃣ دریافت پارامترهای فرم
            String firstName = req.getParameter("firstName");
            String lastName = req.getParameter("lastName");
            String phone = req.getParameter("phone");
            String nationalCode = req.getParameter("nationalCode");
            String password = req.getParameter("password");
            String activeParam = req.getParameter("active");

            // 4️⃣ اعتبارسنجی
            String validationError = validateInput(firstName, lastName, phone, nationalCode);
            
            if (validationError != null) {
                req.setAttribute("error", validationError);
                req.setAttribute("user", user);
                req.setAttribute("availableRoles", UserRole.values());
                req.getRequestDispatcher("/WEB-INF/views/users/edit.jsp").forward(req, resp);
                return;
            }

            // 5️⃣ بررسی تکراری نبودن کد ملی (اگر تغییر کرده)
            if (!user.getNationalCode().equals(nationalCode)) {
                if (userService.findByNationalCode(nationalCode).isPresent()) {
                    req.setAttribute("error", "این کد ملی قبلاً ثبت شده است");
                    req.setAttribute("user", user);
                    req.setAttribute("availableRoles", UserRole.values());
                    req.getRequestDispatcher("/WEB-INF/views/users/edit.jsp").forward(req, resp);
                    return;
                }
            }

            // 6️⃣ به‌روزرسانی اطلاعات
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPhone(phone);
            user.setNationalCode(nationalCode);
            user.setActive("on".equals(activeParam) || "true".equals(activeParam));

            // 7️⃣ به‌روزرسانی رمز عبور (اگر وارد شده باشد)
            if (password != null && !password.isBlank()) {
                if (password.length() < 6) {
                    req.setAttribute("error", "رمز عبور باید حداقل 6 کاراکتر باشد");
                    req.setAttribute("user", user);
                    req.setAttribute("availableRoles", UserRole.values());
                    req.getRequestDispatcher("/WEB-INF/views/users/edit.jsp").forward(req, resp);
                    return;
                }
                String hashedPassword = passwordUtil.hash(password);
                user.setPassword(hashedPassword);
            }

            // 8️⃣ ذخیره تغییرات
            userService.update(user);

            log.info("User updated successfully: {}", user.getUsername());

            // 9️⃣ هدایت به صفحه جزئیات
            resp.sendRedirect(req.getContextPath() + "/users/detail?id=" + userId + "&message=updated");

        } catch (Exception e) {
            log.error("Error updating user", e);
            req.setAttribute("error", "خطا در به‌روزرسانی کاربر: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(req, resp);
        }
    }

    private String validateInput(String firstName, String lastName, 
                                 String phone, String nationalCode) {
        
        if (firstName == null || firstName.isBlank()) {
            return "نام الزامی است";
        }

        if (lastName == null || lastName.isBlank()) {
            return "نام خانوادگی الزامی است";
        }

        if (phone == null || !PHONE_PATTERN.matcher(phone).matches()) {
            return "شماره تلفن نامعتبر است (فرمت: 09xxxxxxxxx)";
        }

        if (nationalCode == null || !NATIONAL_CODE_PATTERN.matcher(nationalCode).matches()) {
            return "کد ملی نامعتبر است (باید 10 رقم باشد)";
        }

        return null;
    }
}
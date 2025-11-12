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
import java.util.regex.Pattern;

@Slf4j
@WebServlet("/users/create")
public class UserCreateServlet extends HttpServlet {

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
        
        // نمایش فرم ایجاد کاربر
        req.setAttribute("availableRoles", UserRole.values());
        req.getRequestDispatcher("/WEB-INF/views/users/create.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        try {
            // 1️⃣ دریافت پارامترهای فرم
            String username = req.getParameter("username");
            String password = req.getParameter("password");
            String firstName = req.getParameter("firstName");
            String lastName = req.getParameter("lastName");
            String phone = req.getParameter("phone");
            String nationalCode = req.getParameter("nationalCode");
            String roleParam = req.getParameter("role");
            String activeParam = req.getParameter("active");

            // 2️⃣ اعتبارسنجی
            String validationError = validateInput(username, password, firstName, 
                    lastName, phone, nationalCode, roleParam);
            
            if (validationError != null) {
                req.setAttribute("error", validationError);
                req.setAttribute("availableRoles", UserRole.values());
                req.getRequestDispatcher("/WEB-INF/views/users/create.jsp").forward(req, resp);
                return;
            }

            // 3️⃣ بررسی تکراری نبودن
            if (userService.findByUsername(username).isPresent()) {
                req.setAttribute("error", "این نام کاربری قبلاً ثبت شده است");
                req.setAttribute("availableRoles", UserRole.values());
                req.getRequestDispatcher("/WEB-INF/views/users/create.jsp").forward(req, resp);
                return;
            }

            if (userService.findByNationalCode(nationalCode).isPresent()) {
                req.setAttribute("error", "این کد ملی قبلاً ثبت شده است");
                req.setAttribute("availableRoles", UserRole.values());
                req.getRequestDispatcher("/WEB-INF/views/users/create.jsp").forward(req, resp);
                return;
            }

            // 4️⃣ هش کردن رمز عبور
            String hashedPassword = passwordUtil.hash(password);

            // 5️⃣ ساخت User جدید
            boolean isActive = "on".equals(activeParam) || "true".equals(activeParam);
            
            User newUser = User.builder()
                    .username(username)
                    .password(hashedPassword)
                    .firstName(firstName)
                    .lastName(lastName)
                    .phone(phone)
                    .nationalCode(nationalCode)
                    .active(isActive)
                    .build();

            // 6️⃣ ذخیره User
            User savedUser = userService.save(newUser);

            // 7️⃣ اضافه کردن نقش
            UserRole selectedRole = UserRole.valueOf(roleParam);
            Role role = Role.builder()
                    .user(savedUser)
                    .role(selectedRole)
                    .build();
            roleService.save(role);

            log.info("User created successfully: {} with role {}", username, selectedRole);

            // 8️⃣ هدایت به صفحه جزئیات
            resp.sendRedirect(req.getContextPath() + "/users/detail?id=" + savedUser.getId() + "&message=created");

        } catch (Exception e) {
            log.error("Error creating user", e);
            req.setAttribute("error", "خطا در ایجاد کاربر: " + e.getMessage());
            req.setAttribute("availableRoles", UserRole.values());
            req.getRequestDispatcher("/WEB-INF/views/users/create.jsp").forward(req, resp);
        }
    }

    private String validateInput(String username, String password, String firstName,
                                 String lastName, String phone, String nationalCode, 
                                 String role) {
        
        if (username == null || username.isBlank() || username.length() < 4 || username.length() > 50) {
            return "نام کاربری باید بین 4 تا 50 کاراکتر باشد";
        }

        if (password == null || password.isBlank() || password.length() < 6) {
            return "رمز عبور باید حداقل 6 کاراکتر باشد";
        }

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

        if (role == null || role.isBlank()) {
            return "انتخاب نقش الزامی است";
        }

        try {
            UserRole.valueOf(role);
        } catch (IllegalArgumentException e) {
            return "نقش انتخاب شده نامعتبر است";
        }

        return null;
    }
}
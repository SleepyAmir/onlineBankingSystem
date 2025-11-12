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
@WebServlet("/auth/register")
public class RegisterServlet extends HttpServlet {

    @Inject
    private UserService userService;

    @Inject
    private RoleService roleService;

    @Inject
    private PasswordUtil passwordUtil;

    // Pattern برای اعتبارسنجی
    private static final Pattern PHONE_PATTERN = Pattern.compile("^09[0-9]{9}$");
    private static final Pattern NATIONAL_CODE_PATTERN = Pattern.compile("^[0-9]{10}$");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // نمایش صفحه ثبت‌نام (فرم HTML/JSP)
        req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text/html; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        try {
            // 1️⃣ دریافت پارامترهای فرم
            String username = req.getParameter("username");
            String password = req.getParameter("password");
            String confirmPassword = req.getParameter("confirmPassword");
            String firstName = req.getParameter("firstName");
            String lastName = req.getParameter("lastName");
            String phone = req.getParameter("phone");
            String nationalCode = req.getParameter("nationalCode");

            // 2️⃣ اعتبارسنجی اولیه
            String validationError = validateInput(username, password, confirmPassword,
                    firstName, lastName, phone, nationalCode);

            if (validationError != null) {
                req.setAttribute("error", validationError);
                req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
                return;
            }

            log.info("Registration attempt for username: {}", username);

            // 3️⃣ بررسی تکراری نبودن username
            if (userService.findByUsername(username).isPresent()) {
                log.warn("Registration failed: Username already exists - {}", username);
                req.setAttribute("error", "این نام کاربری قبلاً ثبت شده است");
                req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
                return;
            }

            // 4️⃣ بررسی تکراری نبودن nationalCode
            if (userService.findByNationalCode(nationalCode).isPresent()) {
                log.warn("Registration failed: National code already exists - {}", nationalCode);
                req.setAttribute("error", "این کد ملی قبلاً ثبت شده است");
                req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
                return;
            }

            // 5️⃣ هش کردن رمز عبور
            String hashedPassword = passwordUtil.hash(password);

            // 6️⃣ ساخت User جدید
            User newUser = User.builder()
                    .username(username)
                    .password(hashedPassword)
                    .firstName(firstName)
                    .lastName(lastName)
                    .phone(phone)
                    .nationalCode(nationalCode)
                    .active(true)
                    .build();

            // 7️⃣ ذخیره User
            User savedUser = userService.save(newUser);

            // 8️⃣ اضافه کردن نقش CUSTOMER به کاربر جدید
            Role customerRole = Role.builder()
                    .user(savedUser)
                    .role(UserRole.CUSTOMER)
                    .build();
            roleService.save(customerRole);

            log.info("Registration successful for user: {}", savedUser.getUsername());

            // 9️⃣ هدایت به صفحه لاگین با پیام موفقیت
            resp.sendRedirect(req.getContextPath() + "/auth/login?message=registered");

        } catch (IllegalArgumentException e) {
            log.error("Validation error during registration", e);
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
        } catch (Exception e) {
            log.error("Error during registration", e);
            req.setAttribute("error", "خطای سرور: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
        }
    }

    /**
     * اعتبارسنجی ورودی‌های فرم
     */
    private String validateInput(String username, String password, String confirmPassword,
                                 String firstName, String lastName, String phone,
                                 String nationalCode) {

        if (username == null || username.isBlank()) {
            return "نام کاربری الزامی است";
        }
        if (username.length() < 4 || username.length() > 50) {
            return "نام کاربری باید بین 4 تا 50 کاراکتر باشد";
        }

        if (password == null || password.isBlank()) {
            return "رمز عبور الزامی است";
        }
        if (password.length() < 6) {
            return "رمز عبور باید حداقل 6 کاراکتر باشد";
        }

        if (!password.equals(confirmPassword)) {
            return "رمز عبور و تکرار آن یکسان نیستند";
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

        return null; // همه چیز معتبره
    }
}
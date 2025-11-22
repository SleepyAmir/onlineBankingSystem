package com.sleepy.onlinebankingsystem.controller.servlet;

import com.sleepy.onlinebankingsystem.model.entity.Role;
import com.sleepy.onlinebankingsystem.model.entity.Token;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.model.enums.UserRole;
import com.sleepy.onlinebankingsystem.security.JwtUtil;
import com.sleepy.onlinebankingsystem.security.SessionManager;
import com.sleepy.onlinebankingsystem.service.RoleService;
import com.sleepy.onlinebankingsystem.service.TokenService;
import com.sleepy.onlinebankingsystem.service.UserService;
import com.sleepy.onlinebankingsystem.utils.PasswordUtil;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@WebServlet("/auth/login")
public class LoginServlet extends HttpServlet {

    @Inject
    private UserService userService;

    @Inject
    private RoleService roleService;

    @Inject
    private TokenService tokenService;

    @Inject
    private PasswordUtil passwordUtil;

    @Inject
    private JwtUtil jwtUtil;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/views/auth/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            String username = req.getParameter("username");
            String password = req.getParameter("password");

            if (username == null || username.isBlank() ||
                    password == null || password.isBlank()) {
                req.setAttribute("error", "نام کاربری و رمز عبور الزامی است");
                req.getRequestDispatcher("/views/auth/login.jsp").forward(req, resp);
                return;
            }

            log.info("Login attempt for username: {}", username);

            Optional<User> userOpt = userService.findByUsername(username);

            if (userOpt.isEmpty()) {
                log.warn("Login failed: User not found - {}", username);
                req.setAttribute("error", "نام کاربری یا رمز عبور اشتباه است");
                req.getRequestDispatcher("/views/auth/login.jsp").forward(req, resp);
                return;
            }

            User user = userOpt.get();

            if (!user.isActive()) {
                log.warn("Login failed: User is inactive - {}", username);
                req.setAttribute("error", "حساب کاربری شما غیرفعال شده است");
                req.getRequestDispatcher("/views/auth/login.jsp").forward(req, resp);
                return;
            }

            if (!passwordUtil.matches(password, user.getPassword())) {
                log.warn("Login failed: Invalid password - {}", username);
                req.setAttribute("error", "نام کاربری یا رمز عبور اشتباه است");
                req.getRequestDispatcher("/views/auth/login.jsp").forward(req, resp);
                return;
            }

            // دریافت نقش‌های کاربر
            List<Role> roles = roleService.findByUser(user);
            Set<UserRole> userRoles = roles.stream()
                    .map(Role::getRole)
                    .collect(Collectors.toSet());

            if (userRoles.isEmpty()) {
                log.warn("Login failed: User has no roles - {}", username);
                req.setAttribute("error", "کاربر فاقد نقش سیستمی است");
                req.getRequestDispatcher("/views/auth/login.jsp").forward(req, resp);
                return;
            }

            // ✅ تبدیل نقش‌ها به String برای استفاده در JSP
            Set<String> roleNames = userRoles.stream()
                    .map(UserRole::name)
                    .collect(Collectors.toSet());

            // تولید JWT Token
            String jwtToken = jwtUtil.generateToken(user.getUsername(), userRoles);

            // ذخیره Token در دیتابیس
            Token token = Token.builder()
                    .tokenValue(jwtToken)
                    .username(user.getUsername())
                    .expiry(LocalDateTime.now().plusMinutes(15))
                    .tokenType("ACCESS")
                    .build();
            tokenService.save(token);

            // ایجاد Session
            HttpSession session = req.getSession(true);
            session.setAttribute("username", user.getUsername());
            session.setAttribute("userId", user.getId());
            session.setAttribute("fullName", user.getFirstName() + " " + user.getLastName());
            session.setAttribute("roles", userRoles);
            session.setAttribute("token", jwtToken);

            // ✅ اضافه شد: نام نقش‌ها به صورت String برای JSP
            session.setAttribute("roleNames", roleNames);

            // ✅ اضافه شد: flag های boolean برای بررسی آسان در JSP
            session.setAttribute("isAdmin", userRoles.contains(UserRole.ADMIN));
            session.setAttribute("isManager", userRoles.contains(UserRole.MANAGER));
            session.setAttribute("isCustomer", userRoles.contains(UserRole.CUSTOMER));

            session.setMaxInactiveInterval(15 * 60);

            // ثبت Session در SessionManager
            SessionManager.addSession(user.getUsername(), session);

            log.info("Login successful for user: {} with roles: {}", user.getUsername(), userRoles);

            // هدایت به داشبورد بر اساس نقش
            String redirectUrl = determineRedirectUrl(userRoles);
            resp.sendRedirect(req.getContextPath() + redirectUrl);

        } catch (Exception e) {
            log.error("Error during login", e);
            req.setAttribute("error", "خطای سرور: " + e.getMessage());
            req.getRequestDispatcher("/views/auth/login.jsp").forward(req, resp);
        }
    }

    private String determineRedirectUrl(Set<UserRole> roles) {
        if (roles.contains(UserRole.ADMIN)) {
            return "/admin/admin-dashboard";
        } else if (roles.contains(UserRole.MANAGER)) {
            return "/manager/user-dashboard";
        } else {
            return "/customer/user-dashboard";
        }
    }
}
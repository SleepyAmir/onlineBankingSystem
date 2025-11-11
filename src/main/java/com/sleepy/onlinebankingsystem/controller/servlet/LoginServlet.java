package com.sleepy.onlinebankingsystem.controller.servlet;

import com.sleepy.onlinebankingsystem.exception.BankException;
import com.sleepy.onlinebankingsystem.filter.CsrfFilter;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.security.JwtUtil;
import com.sleepy.onlinebankingsystem.security.SessionManager;
import com.sleepy.onlinebankingsystem.service.UserService;
import jakarta.inject.Inject;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Set;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Inject
    private UserService userService;

    @Inject
    private JwtUtil jwtUtil;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        CsrfFilter.generateCsrfToken(request);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/login.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String username = request.getParameter("username");
            String password = request.getParameter("password");

            // پیدا کردن کاربر (با چک password – فرض hashed)
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new BankException("Invalid credentials"));

            if (!user.getPassword().equals(password)) { // TODO: compare hashed
                throw new BankException("Invalid credentials");
            }

            if (!user.isActive()) {
                throw new BankException("Account inactive");
            }

            // session
            HttpSession session = request.getSession(true);
            session.setMaxInactiveInterval(600); // 10 min timeout
            session.setAttribute("user", user);
            SessionManager.addSession(username, session);

            // JWT for APIs
            Set<String> roles = Set.of("USER"); // از Role انتیتی بگیر
            String token = jwtUtil.generateToken(username, roles);
            session.setAttribute("jwtToken", token);

            response.sendRedirect("/jsp/dashboard");

        } catch (BankException e) {
            request.setAttribute("error", e.getUserMessage());
            doGet(request, response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
package com.sleepy.onlinebankingsystem.controller.servlet;

import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.service.AccountService;
import com.sleepy.onlinebankingsystem.service.UserService;
import com.sleepy.onlinebankingsystem.utils.SessionListener;
import jakarta.inject.Inject;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin-dashboard")
public class AdminDashboardServlet extends HttpServlet {

    @Inject
    private UserService userService;

    @Inject
    private AccountService accountService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("/jsp/login");
            return;
        }

        // چک RBAC
        User admin = (User) session.getAttribute("user");
        if (!admin.getRoles().contains("ADMIN")) { // یا Manager
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // بارگذاری همه داده‌ها
        List<User> users = null; // pagination
        try {
            users = userService.findAll(0, 100);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        List<Account> accounts = null;
        try {
            accounts = accountService.findAll(0, 100);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        request.setAttribute("users", users);
        request.setAttribute("accounts", accounts);
        request.setAttribute("onlineUsers", SessionListener.getOnline());

        RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/admin-dashboard.jsp");
        dispatcher.forward(request, response);
    }

    // برای ادیت/حذف، POST handler اضافه کن
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // مثلاً softDelete account
        String action = request.getParameter("action");
        if ("deleteAccount".equals(action)) {
            Long id = Long.parseLong(request.getParameter("id"));
            try {
                accountService.softDelete(id);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            response.sendRedirect("/jsp/admin-dashboard?msg=Account deleted");
        }
        // مشابه برای edit
    }
}
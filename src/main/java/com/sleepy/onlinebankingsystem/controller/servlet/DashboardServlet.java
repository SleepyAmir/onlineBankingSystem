package com.sleepy.onlinebankingsystem.controller.servlet;

import com.sleepy.onlinebankingsystem.model.entity.Role;
import com.sleepy.onlinebankingsystem.model.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Set;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("/jsp/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        Set<Role> roles = user.getRoles(); // از Role بگیر

        if (roles.contains("ADMIN") || roles.contains("MANAGER")) {
            response.sendRedirect("/jsp/admin-dashboard");
        } else {
            response.sendRedirect("/jsp/user-dashboard");
        }
    }
}
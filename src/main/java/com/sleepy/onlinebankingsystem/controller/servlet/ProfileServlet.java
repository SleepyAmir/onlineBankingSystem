package com.sleepy.onlinebankingsystem.controller.servlet;


import com.sleepy.onlinebankingsystem.filter.CsrfFilter;
import com.sleepy.onlinebankingsystem.model.entity.User;
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

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    @Inject
    private UserService userService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");
        request.setAttribute("user", user);
        CsrfFilter.generateCsrfToken(request);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/profile.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        // آپدیت فیلدها
        user.setFirstName(request.getParameter("firstName"));
        user.setLastName(request.getParameter("lastName"));
        user.setPhone(request.getParameter("phone"));
        // password اگر تغییر کرد، hash کن

        try {
            userService.update(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        response.sendRedirect("/jsp/user-dashboard?msg=Profile updated");
    }
}
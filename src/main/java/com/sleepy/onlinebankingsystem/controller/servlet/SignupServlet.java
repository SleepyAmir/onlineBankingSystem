package com.sleepy.onlinebankingsystem.controller.servlet;

import com.sleepy.onlinebankingsystem.exception.BankException;
import com.sleepy.onlinebankingsystem.filter.CsrfFilter;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.service.UserService;
import com.sleepy.onlinebankingsystem.utils.CustomException;
import jakarta.inject.Inject;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/signup")
public class SignupServlet extends HttpServlet {

    @Inject
    private UserService userService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        CsrfFilter.generateCsrfToken(request);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/signup.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // validation ورودی‌ها (مثل regex برای phone/nationalCode)
            String username = request.getParameter("username");
            String password = request.getParameter("password"); // هش کن!
            String firstName = request.getParameter("firstName");
            String lastName = request.getParameter("lastName");
            String phone = request.getParameter("phone");
            String nationalCode = request.getParameter("nationalCode");

            User user = User.builder()
                    .username(username)
                    .password(password) // TODO: bcrypt or hash
                    .firstName(firstName)
                    .lastName(lastName)
                    .phone(phone)
                    .nationalCode(nationalCode)
                    .active(false) // نیاز به approve توسط admin
                    .build();

            userService.save(user);
            response.sendRedirect("/login?msg=Signup successful! Please login.");

        } catch (BankException | CustomException e) {
            request.setAttribute("error", e.getMessage());
            doGet(request, response);
        } catch (Exception e) {
            try {
                throw new BankException("Signup failed", e.getMessage());
            } catch (BankException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
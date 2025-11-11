package com.sleepy.onlinebankingsystem.controller.servlet;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/welcome")
public class WelcomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // معرفی بانک
        request.setAttribute("bankIntro", "Welcome to Sleepy Bank! Secure and reliable banking.");
        RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/welcome.jsp");
        dispatcher.forward(request, response);
    }
}
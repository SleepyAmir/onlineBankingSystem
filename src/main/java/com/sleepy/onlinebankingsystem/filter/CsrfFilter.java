package com.sleepy.onlinebankingsystem.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.UUID;

@WebFilter(urlPatterns = {"*.jsp", "/servlet/*"})
public class CsrfFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if ("POST".equalsIgnoreCase(request.getMethod())) {
            HttpSession session = request.getSession(false);
            if (session == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }

            String sessionToken = (String) session.getAttribute("csrfToken");
            String requestToken = request.getParameter("csrfToken");

            if (sessionToken == null || !sessionToken.equals(requestToken)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("CSRF token mismatch or missing");
                return;
            }
        }
        chain.doFilter(request, response);
    }

    public static void generateCsrfToken(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String token = UUID.randomUUID().toString();
        session.setAttribute("csrfToken", token);
    }
}
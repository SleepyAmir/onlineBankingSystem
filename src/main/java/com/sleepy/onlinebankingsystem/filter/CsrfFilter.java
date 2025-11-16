package com.sleepy.onlinebankingsystem.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.UUID;

/**
 * CSRF Protection Filter
 * Only applies to web UI (JSP/Servlets), NOT to REST API
 */
@Slf4j
@WebFilter(urlPatterns = {"*.jsp", "/servlet/*"})
public class CsrfFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // Skip CSRF check for API endpoints
        String requestURI = request.getRequestURI();
        if (requestURI.contains("/api/")) {
            chain.doFilter(request, response);
            return;
        }

        // Skip for GET requests (CSRF only matters for state-changing operations)
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        // Check CSRF token for POST requests
        HttpSession session = request.getSession(false);
        if (session == null) {
            log.warn("CSRF check: No session found");
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        String sessionToken = (String) session.getAttribute("csrfToken");
        String requestToken = request.getParameter("csrfToken");

        if (sessionToken == null || !sessionToken.equals(requestToken)) {
            log.warn("CSRF token mismatch: expected={}, actual={}", sessionToken, requestToken);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write("<h1>خطای امنیتی</h1><p>توکن CSRF نامعتبر است.</p>");
            return;
        }

        chain.doFilter(request, response);
    }

    /**
     * Generate CSRF token for a session
     */
    public static void generateCsrfToken(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String token = UUID.randomUUID().toString();
        session.setAttribute("csrfToken", token);
    }

    /**
     * Get CSRF token from session
     */
    public static String getCsrfToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        String token = (String) session.getAttribute("csrfToken");
        if (token == null) {
            generateCsrfToken(request);
            token = (String) session.getAttribute("csrfToken");
        }
        return token;
    }
}
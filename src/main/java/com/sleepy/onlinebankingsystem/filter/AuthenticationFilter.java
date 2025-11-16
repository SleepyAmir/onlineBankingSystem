package com.sleepy.onlinebankingsystem.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Authentication Filter
 * Handles session-based authentication for web UI
 * API endpoints (/api/*) are excluded and handled separately
 */
@Slf4j
@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    // مسیرهایی که نیاز به احراز هویت ندارن
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/",
            "/welcome",
            "/about",
            "/services",
            "/contact",
            "/auth/login",
            "/auth/register",
            "/auth/logout",
            "/api/",           // ✅ API endpoints are public (handle auth separately)
            "/css/",
            "/js/",
            "/images/",
            "/favicon.ico"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());

        // ✅ Skip authentication for API endpoints
        if (path.startsWith("/api/")) {
            log.debug("API request, skipping session authentication: {}", path);
            chain.doFilter(request, response);
            return;
        }

        // بررسی اینکه مسیر public هست یا نه
        boolean isPublicPath = PUBLIC_PATHS.stream()
                .anyMatch(path::startsWith);

        if (isPublicPath) {
            chain.doFilter(request, response);
            return;
        }

        // بررسی Session برای مسیرهای محافظت‌شده
        HttpSession session = httpRequest.getSession(false);

        if (session == null || session.getAttribute("username") == null) {
            log.warn("Unauthorized access attempt to: {} from IP: {}",
                    path, httpRequest.getRemoteAddr());

            // Save the original URL to redirect after login
            httpRequest.getSession(true).setAttribute("redirectAfterLogin", path);

            httpResponse.sendRedirect(contextPath + "/auth/login?error=unauthorized&redirect=" + path);
            return;
        }

        // کاربر لاگین کرده، اجازه دسترسی
        log.debug("Authenticated request to: {} by user: {}", path, session.getAttribute("username"));
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("AuthenticationFilter initialized");
    }

    @Override
    public void destroy() {
        log.info("AuthenticationFilter destroyed");
    }
}
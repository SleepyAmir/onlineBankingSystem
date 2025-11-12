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

        // بررسی اینکه مسیر public هست یا نه
        boolean isPublicPath = PUBLIC_PATHS.stream()
                .anyMatch(path::startsWith);

        if (isPublicPath) {
            chain.doFilter(request, response);
            return;
        }

        // بررسی Session
        HttpSession session = httpRequest.getSession(false);

        if (session == null || session.getAttribute("username") == null) {
            log.warn("Unauthorized access attempt to: {}", path);
            httpResponse.sendRedirect(contextPath + "/auth/login?error=unauthorized");
            return;
        }

        // کاربر لاگین کرده، اجازه دسترسی
        chain.doFilter(request, response);
    }
}
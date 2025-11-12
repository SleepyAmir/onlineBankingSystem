package com.sleepy.onlinebankingsystem.filter;

import com.sleepy.onlinebankingsystem.model.enums.UserRole;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Set;

@Slf4j
@WebFilter({"/admin/*", "/manager/*", "/users/*"})
public class AuthorizationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());

        HttpSession session = httpRequest.getSession(false);

        if (session == null) {
            httpResponse.sendRedirect(contextPath + "/auth/login?error=unauthorized");
            return;
        }

        @SuppressWarnings("unchecked")
        Set<UserRole> userRoles = (Set<UserRole>) session.getAttribute("roles");

        if (userRoles == null || userRoles.isEmpty()) {
            log.warn("User has no roles: {}", session.getAttribute("username"));
            httpResponse.sendRedirect(contextPath + "/auth/login?error=no_role");
            return;
        }

        // بررسی دسترسی بر اساس مسیر
        if (path.startsWith("/admin/") && !userRoles.contains(UserRole.ADMIN)) {
            log.warn("Forbidden access to admin area by user: {}", session.getAttribute("username"));
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "دسترسی غیرمجاز");
            return;
        }

        if (path.startsWith("/manager/") &&
                !userRoles.contains(UserRole.ADMIN) &&
                !userRoles.contains(UserRole.MANAGER)) {
            log.warn("Forbidden access to manager area by user: {}", session.getAttribute("username"));
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "دسترسی غیرمجاز");
            return;
        }

        if (path.startsWith("/users/") &&
                !userRoles.contains(UserRole.ADMIN) &&
                !userRoles.contains(UserRole.MANAGER)) {
            log.warn("Forbidden access to user management by user: {}", session.getAttribute("username"));
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "دسترسی غیرمجاز");
            return;
        }

        // دسترسی مجاز
        chain.doFilter(request, response);
    }
}
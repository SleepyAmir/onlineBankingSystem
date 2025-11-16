package com.sleepy.onlinebankingsystem.filter;

import com.sleepy.onlinebankingsystem.security.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.inject.Inject;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * API Authentication Filter
 * Handles JWT-based authentication for REST API endpoints
 * Only applies to /api/* paths
 */
@Slf4j
@WebFilter("/api/*")
public class ApiAuthenticationFilter implements Filter {

    @Inject
    private JwtUtil jwtUtil;

    // API endpoints that don't require authentication
    private static final List<String> PUBLIC_API_PATHS = Arrays.asList(
            "/api/test",
            "/api/health"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());

        // Allow CORS preflight requests
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            setCorsHeaders(httpResponse);
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        // Check if path is public
        boolean isPublicPath = PUBLIC_API_PATHS.stream()
                .anyMatch(path::equals);

        if (isPublicPath) {
            log.debug("Public API endpoint: {}", path);
            setCorsHeaders(httpResponse);
            chain.doFilter(request, response);
            return;
        }

        // Extract JWT token from Authorization header
        String authHeader = httpRequest.getHeader("Authorization");

        // ⚠️ TEMPORARY: Allow API access without JWT for development
        // TODO: Remove this in production and enforce JWT authentication
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("API request without JWT token: {} - Allowing for development", path);
            setCorsHeaders(httpResponse);
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            // Validate JWT
            Claims claims = jwtUtil.validateToken(token);

            if (jwtUtil.isExpired(claims)) {
                sendJsonError(httpResponse, HttpServletResponse.SC_UNAUTHORIZED,
                        "Token has expired");
                return;
            }

            // Set user info in request attributes
            httpRequest.setAttribute("username", jwtUtil.getUsername(claims));
            httpRequest.setAttribute("roles", jwtUtil.getRoles(claims));

            log.debug("Authenticated API request: {} by user: {}",
                    path, jwtUtil.getUsername(claims));

            setCorsHeaders(httpResponse);
            chain.doFilter(request, response);

        } catch (JwtException e) {
            log.warn("Invalid JWT token for API request: {} - Error: {}", path, e.getMessage());
            sendJsonError(httpResponse, HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid or expired token: " + e.getMessage());
        }
    }

    /**
     * Set CORS headers for API responses
     */
    private void setCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Max-Age", "3600");
    }

    /**
     * Send JSON error response
     */
    private void sendJsonError(HttpServletResponse response, int status, String message)
            throws IOException {
        response.setStatus(status);
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(String.format(
                "{\"error\": \"%s\", \"status\": %d}",
                message, status
        ));
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("ApiAuthenticationFilter initialized");
    }

    @Override
    public void destroy() {
        log.info("ApiAuthenticationFilter destroyed");
    }
}
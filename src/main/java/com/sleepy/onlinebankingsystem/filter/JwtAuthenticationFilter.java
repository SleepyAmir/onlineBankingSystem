package com.sleepy.onlinebankingsystem.filter;

import com.sleepy.onlinebankingsystem.repository.TokenRepository;
import com.sleepy.onlinebankingsystem.security.JwtUtil;
import com.sleepy.onlinebankingsystem.utils.CustomException;
import jakarta.inject.Inject;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

import java.io.IOException;

@WebFilter(urlPatterns = "/api/*")
public class JwtAuthenticationFilter extends HttpFilter {

    @Inject
    private JwtUtil jwtUtil;

    @Inject
    private TokenRepository tokenRepository;

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring(7);

        try {
            // 1. چک کردن وجود توکن در دیتابیس
            var tokenEntity = tokenRepository.findByTokenValue(token)
                    .orElseThrow(() -> new CustomException("Invalid token"));

            // 2. اعتبارسنجی JWT
            Claims claims = jwtUtil.validateToken(token);

            // 3. چک انقضا
            if (jwtUtil.isExpired(claims)) {
                tokenRepository.hardDelete(tokenEntity.getId());
                throw new CustomException("Token expired");
            }

            // 4. تنظیم اطلاعات در request
            request.setAttribute("claims", claims);
            request.setAttribute("username", jwtUtil.getUsername(claims));
            request.setAttribute("roles", jwtUtil.getRoles(claims));

            chain.doFilter(request, response);

        } catch (JwtException e) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT: " + e.getMessage());
        } catch (CustomException e) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Authentication error");
        }
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
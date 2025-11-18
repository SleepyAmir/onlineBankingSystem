package com.sleepy.onlinebankingsystem.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter برای تنظیم UTF-8 encoding برای همه request و response ها
 * این فیلتر باید اولین فیلتری باشد که اجرا می‌شود
 */
@WebFilter(filterName = "CharacterEncodingFilter", urlPatterns = {"/*"})
public class CharacterEncodingFilter implements Filter {

    private static final String ENCODING = "UTF-8";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // تنظیم encoding برای request
        if (httpRequest.getCharacterEncoding() == null) {
            httpRequest.setCharacterEncoding(ENCODING);
        }

        // تنظیم encoding برای response
        httpResponse.setCharacterEncoding(ENCODING);

        // برای HTML responses
        if (httpResponse.getContentType() == null ||
                httpResponse.getContentType().startsWith("text/html")) {
            httpResponse.setContentType("text/html; charset=UTF-8");
        }

        // برای JSON responses (API)
        if (httpRequest.getRequestURI().startsWith(httpRequest.getContextPath() + "/api/")) {
            httpResponse.setContentType("application/json; charset=UTF-8");
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }
}
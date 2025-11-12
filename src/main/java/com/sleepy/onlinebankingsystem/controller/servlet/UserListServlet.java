package com.sleepy.onlinebankingsystem.controller.servlet;

import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.service.UserService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

@Slf4j
@WebServlet("/users/list")
public class UserListServlet extends HttpServlet {

    @Inject
    private UserService userService;

    private static final int PAGE_SIZE = 10;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        try {
            // 1️⃣ دریافت شماره صفحه از پارامتر (پیش‌فرض: 0)
            String pageParam = req.getParameter("page");
            int page = 0;
            
            if (pageParam != null) {
                try {
                    page = Integer.parseInt(pageParam);
                    if (page < 0) page = 0;
                } catch (NumberFormatException e) {
                    log.warn("Invalid page parameter: {}", pageParam);
                    page = 0;
                }
            }

            // 2️⃣ دریافت لیست کاربران با صفحه‌بندی
            List<User> users = userService.findAll(page, PAGE_SIZE);

            // 3️⃣ ارسال اطلاعات به JSP
            req.setAttribute("users", users);
            req.setAttribute("currentPage", page);
            req.setAttribute("pageSize", PAGE_SIZE);

            log.info("Fetched {} users for page {}", users.size(), page);

            // 4️⃣ نمایش JSP
            req.getRequestDispatcher("/views/users/list.jsp").forward(req, resp);

        } catch (Exception e) {
            log.error("Error fetching user list", e);
            req.setAttribute("error", "خطا در دریافت لیست کاربران: " + e.getMessage());
            req.getRequestDispatcher("/views/error.jsp").forward(req, resp);
        }
    }
}
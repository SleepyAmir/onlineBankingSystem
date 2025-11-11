package com.sleepy.onlinebankingsystem.controller.servlet;

import com.sleepy.onlinebankingsystem.filter.CsrfFilter;
import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.model.enums.AccountStatus;
import com.sleepy.onlinebankingsystem.model.enums.AccountType;
import com.sleepy.onlinebankingsystem.service.AccountService;
import jakarta.inject.Inject;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Random;

@WebServlet("/account/create")
public class AccountCreationServlet extends HttpServlet {

    @Inject
    private AccountService accountService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("/jsp/login");
            return;
        }

        CsrfFilter.generateCsrfToken(request);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/create-account.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        try {
            String typeStr = request.getParameter("type");
            AccountType type = AccountType.valueOf(typeStr.toUpperCase());

            // تولید شماره حساب 16 رقمی
            String accountNumber = generateAccountNumber();

            Account account = Account.builder()
                    .user(user)
                    .accountNumber(accountNumber)
                    .type(type)
                    .balance(java.math.BigDecimal.ZERO)
                    .status(AccountStatus.ACTIVE)
                    .build();

            accountService.save(account);
            response.sendRedirect("/jsp/user-dashboard?msg=Account created: " + accountNumber);

        } catch (Exception e) {
            request.setAttribute("error", "Failed to create account: " + e.getMessage());
            doGet(request, response);
        }
    }

    private String generateAccountNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder("6037"); // کد بانک فرضی
        for (int i = 0; i < 12; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
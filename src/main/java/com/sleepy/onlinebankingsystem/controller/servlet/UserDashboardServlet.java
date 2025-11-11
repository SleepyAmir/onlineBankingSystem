package com.sleepy.onlinebankingsystem.controller.servlet;

import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.entity.Card;
import com.sleepy.onlinebankingsystem.model.entity.Loan;
import com.sleepy.onlinebankingsystem.model.entity.Transaction;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.service.AccountService;
import com.sleepy.onlinebankingsystem.service.CardService;
import com.sleepy.onlinebankingsystem.service.LoanService;
import com.sleepy.onlinebankingsystem.service.TransactionService;
import jakarta.inject.Inject;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/user-dashboard")
public class UserDashboardServlet extends HttpServlet {

    @Inject
    private AccountService accountService;

    @Inject
    private CardService cardService;

    @Inject
    private LoanService loanService;

    @Inject
    private TransactionService transactionService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("/jsp/login");
            return;
        }

        User user = (User) session.getAttribute("user");

        // بارگذاری داده‌ها
        List<Account> accounts;
        List<Card> cards;
        List<Loan> loans;
        List<Transaction> transactions;
        try {


            accounts = accountService.findByUser(user);
            cards = cardService.findByUser(user);
            loans = loanService.findByUser(user);
            transactions = transactionService.findByUser(user);
        } catch (Exception e) {
            throw new ServletException("Error loading user data", e);
        }

        request.setAttribute("accounts", accounts);
        request.setAttribute("cards", cards);
        request.setAttribute("loans", loans);
        request.setAttribute("transactions", transactions);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/user-dashboard.jsp");
        dispatcher.forward(request, response);
    }
}
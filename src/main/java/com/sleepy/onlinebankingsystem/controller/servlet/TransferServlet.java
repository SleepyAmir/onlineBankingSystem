package com.sleepy.onlinebankingsystem.controller.servlet;


import com.sleepy.onlinebankingsystem.exception.BankException;
import com.sleepy.onlinebankingsystem.filter.CsrfFilter;
import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.entity.Transaction;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.model.enums.TransactionType;
import com.sleepy.onlinebankingsystem.service.AccountService;
import com.sleepy.onlinebankingsystem.service.TransactionService;
import jakarta.inject.Inject;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.hibernate.resource.transaction.spi.TransactionStatus;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@WebServlet("/transfer")
public class TransferServlet extends HttpServlet {

    @Inject
    private AccountService accountService;

    @Inject
    private TransactionService transactionService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        CsrfFilter.generateCsrfToken(request);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/transfer.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        try {
            String fromAccountNum = request.getParameter("fromAccount");
            String toAccountNum = request.getParameter("toAccount");
            BigDecimal amount = new BigDecimal(request.getParameter("amount"));

            Account from = accountService.findByAccountNumber(fromAccountNum)
                    .orElseThrow(() -> new BankException("Invalid from account"));
            Account to = accountService.findByAccountNumber(toAccountNum)
                    .orElseThrow(() -> new BankException("Invalid to account"));

            // چک مالکیت و موجودی
            if (!from.getUser().equals(user)) {
                throw new BankException("Not your account");
            }
            if (from.getBalance().compareTo(amount) < 0) {
                throw new BankException("Insufficient balance");
            }

            // تراکنش
            from.setBalance(from.getBalance().subtract(amount));
            to.setBalance(to.getBalance().add(amount));

            Transaction tx = Transaction.builder()
                    .transactionId(UUID.randomUUID().toString())
                    .fromAccount(from)
                    .toAccount(to)
                    .amount(amount)
                    .type(TransactionType.TRANSFER)
                    .transactionDate(LocalDateTime.now())
                    .status(TransactionStatus.ACTIVE) // فرض enum درست
                    .description("Transfer from " + fromAccountNum)
                    .build();

            accountService.update(from);
            accountService.update(to);
            transactionService.save(tx);

            response.sendRedirect("/jsp/user-dashboard?msg=Transfer successful");

        } catch (BankException e) {
            request.setAttribute("error", e.getUserMessage());
            doGet(request, response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
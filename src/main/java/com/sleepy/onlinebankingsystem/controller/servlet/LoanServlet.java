package com.sleepy.onlinebankingsystem.controller.servlet;

import com.sleepy.onlinebankingsystem.exception.BankException;
import com.sleepy.onlinebankingsystem.filter.CsrfFilter;
import com.sleepy.onlinebankingsystem.model.entity.Account;
import com.sleepy.onlinebankingsystem.model.entity.Loan;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.model.enums.LoanStatus;
import com.sleepy.onlinebankingsystem.service.AccountService;
import com.sleepy.onlinebankingsystem.service.LoanService;
import jakarta.inject.Inject;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@WebServlet("/loan")
public class LoanServlet extends HttpServlet {

    @Inject
    private LoanService loanService;

    @Inject
    private AccountService accountService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        CsrfFilter.generateCsrfToken(request);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/loan.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        try {
            String accountNum = request.getParameter("account");
            BigDecimal principal = new BigDecimal(request.getParameter("principal"));
            Integer months = Integer.parseInt(request.getParameter("months"));

            Account account = accountService.findByAccountNumber(accountNum)
                    .orElseThrow(() -> new BankException("Invalid account"));

            if (!account.getUser().equals(user)) {
                throw new BankException("Not your account");
            }

            // محاسبه monthlyPayment (فرمول ساده)
            BigDecimal rate = BigDecimal.valueOf(0.18); // 18%
            BigDecimal monthlyPayment = principal.multiply(rate.divide(BigDecimal.valueOf(12), 2, BigDecimal.ROUND_HALF_UP))
                    .add(principal.divide(BigDecimal.valueOf(months), 2, BigDecimal.ROUND_HALF_UP));

            Loan loan = Loan.builder()
                    .account(account)
                    .loanNumber(UUID.randomUUID().toString())
                    .principal(principal)
                    .annualInterestRate(rate)
                    .durationMonths(months)
                    .monthlyPayment(monthlyPayment)
                    .startDate(LocalDate.now())
                    .status(LoanStatus.PENDING)
                    .build();

            loanService.save(loan);

            response.sendRedirect("/jsp/user-dashboard?msg=Loan requested");

        } catch (BankException e) {
            request.setAttribute("error", e.getUserMessage());
            doGet(request, response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
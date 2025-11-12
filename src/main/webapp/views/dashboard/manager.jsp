<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/views/common/header.jsp">
    <jsp:param name="title" value="داشبورد مدیر" />
    <jsp:param name="isDashboard" value="true" />
</jsp:include>

<jsp:include page="/views/common/navbar.jsp" />
<jsp:include page="/views/common/sidebar.jsp" />

<div class="content-wrapper">
    <div class="container-fluid px-4 py-4">
        <!-- عنوان -->
        <div class="row mb-4">
            <div class="col-12">
                <h1 class="display-5 fw-bold">داشبورد مدیر</h1>
                <p class="lead text-muted">مدیریت وام‌ها و نظارت سیستم</p>
            </div>
        </div>

        <!-- کارت‌های آمار -->
        <div class="row g-4 mb-5">
            <!-- کاربران فعال -->
            <div class="col-md-3">
                <div class="card shadow-lg border-0 rounded-3" style="background: linear-gradient(135deg, #667eea, #764ba2); color: white;">
                    <div class="card-body">
                        <h5>کاربران فعال</h5>
                        <h2><fmt:formatNumber value="${totalUsers}" /></h2>
                    </div>
                </div>
            </div>

            <!-- حساب‌ها -->
            <div class="col-md-3">
                <div class="card shadow-lg border-0 rounded-3" style="background: linear-gradient(135deg, #10b981, #059669); color: white;">
                    <div class="card-body">
                        <h5>کل حساب‌ها</h5>
                        <h2><fmt:formatNumber value="${totalAccounts}" /></h2>
                        <p>کل موجودی: <fmt:formatNumber value="${totalBankBalance}" type="currency" currencySymbol="ریال" /></p>
                    </div>
                </div>
            </div>

            <!-- تراکنش‌ها -->
            <div class="col-md-3">
                <div class="card shadow-lg border-0 rounded-3" style="background: linear-gradient(135deg, #3b82f6, #2563eb); color: white;">
                    <div class="card-body">
                        <h5>کل تراکنش‌ها</h5>
                        <h2><fmt:formatNumber value="${totalTransactions}" /></h2>
                        <p>امروز: <fmt:formatNumber value="${todayTransactionsCount}" /> | حجم: <fmt:formatNumber value="${todayTransactionVolume}" type="currency" currencySymbol="ریال" /></p>
                    </div>
                </div>
            </div>

            <!-- وام‌ها -->
            <div class="col-md-3">
                <div class="card shadow-lg border-0 rounded-3" style="background: linear-gradient(135deg, #f59e0b, #d97706); color: white;">
                    <div class="card-body">
                        <h5>کل وام‌ها</h5>
                        <h2><fmt:formatNumber value="${totalLoans}" /></h2>
                        <p>در انتظار: <fmt:formatNumber value="${pendingLoansCount}" /> | فعال: <fmt:formatNumber value="${activeLoansCount}" /></p>
                        <p>مبلغ در انتظار: <fmt:formatNumber value="${totalPendingLoanAmount}" type="currency" currencySymbol="ریال" /></p>
                    </div>
                </div>
            </div>
        </div>

        <!-- چارت توزیع وام‌ها -->
        <div class="row mb-5">
            <div class="col-12">
                <div class="card shadow-lg border-0 rounded-3">
                    <div class="card-header bg-light">توزیع وام‌ها</div>
                    <div class="card-body">
                        <canvas id="loanChart"></canvas>
                    </div>
                </div>
            </div>
        </div>

        <!-- لیست‌های اخیر -->
        <div class="row g-4">
            <!-- وام‌های در انتظار -->
            <div class="col-md-6">
                <div class="card shadow-lg border-0 rounded-3">
                    <div class="card-header bg-light">وام‌های در انتظار</div>
                    <div class="card-body">
                        <table class="table table-hover">
                            <thead>
                            <tr>
                                <th>کاربر</th>
                                <th>مبلغ</th>
                                <th>تاریخ</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${pendingLoans}" var="loan">
                                <tr>
                                    <td><c:out value="${loan.user.username}" /></td>
                                    <td><fmt:formatNumber value="${loan.principal}" type="currency" currencySymbol="ریال" /></td>
                                    <td><fmt:formatDate value="${loan.createdAt}" pattern="yyyy/MM/dd HH:mm" /></td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <!-- تراکنش‌های اخیر -->
            <div class="col-md-6">
                <div class="card shadow-lg border-0 rounded-3">
                    <div class="card-header bg-light">تراکنش‌های اخیر</div>
                    <div class="card-body">
                        <table class="table table-hover">
                            <thead>
                            <tr>
                                <th>نوع</th>
                                <th>مبلغ</th>
                                <th>تاریخ</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${recentTransactions}" var="tx">
                                <tr>
                                    <td><c:out value="${tx.type}" /></td>
                                    <td><fmt:formatNumber value="${tx.amount}" type="currency" currencySymbol="ریال" /></td>
                                    <td><fmt:formatDate value="${tx.transactionDate}" pattern="yyyy/MM/dd HH:mm" /></td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/views/common/footer.jsp" />

<script>
    // چارت وام‌ها (Bar Chart)
    const loanCtx = document.getElementById('loanChart').getContext('2d');
    new Chart(loanCtx, {
        type: 'bar',
        data: {
            labels: ['در انتظار', 'فعال', 'تأیید شده'],
            datasets: [{
                label: 'تعداد وام‌ها',
                data: [${pendingLoansCount}, ${activeLoansCount}, ${approvedLoansCount}],
                backgroundColor: ['#f59e0b', '#10b981', '#3b82f6']
            }]
        },
        options: { responsive: true, scales: { y: { beginAtZero: true } } }
    });
</script>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/views/common/header.jsp">
    <jsp:param name="title" value="داشبورد ادمین" />
    <jsp:param name="isDashboard" value="true" />
</jsp:include>

<jsp:include page="/views/common/navbar.jsp" />
<jsp:include page="/views/common/sidebar.jsp" />

<div class="content-wrapper">
    <div class="container-fluid px-4 py-4">
        <!-- عنوان داشبورد -->
        <div class="row mb-4">
            <div class="col-12">
                <h1 class="display-5 fw-bold">داشبورد ادمین</h1>
                <p class="lead text-muted">نمای کلی سیستم بانکی</p>
            </div>
        </div>

        <!-- کارت‌های آمار اصلی -->
        <div class="row g-4 mb-5">
            <!-- کاربران -->
            <div class="col-md-3">
                <div class="card shadow-lg border-0 rounded-3" style="background: linear-gradient(135deg, #667eea, #764ba2); color: white;">
                    <div class="card-body">
                        <div class="d-flex justify-content-between align-items-center">
                            <div>
                                <h5 class="card-title">کل کاربران</h5>
                                <h2 class="fw-bold"><fmt:formatNumber value="${totalUsers}" /></h2>
                            </div>
                            <i class="fas fa-users fa-3x opacity-50"></i>
                        </div>
                        <p class="mt-3">فعال: <fmt:formatNumber value="${activeUsers}" /> | غیرفعال: <fmt:formatNumber value="${inactiveUsers}" /></p>
                    </div>
                </div>
            </div>

            <!-- حساب‌ها -->
            <div class="col-md-3">
                <div class="card shadow-lg border-0 rounded-3" style="background: linear-gradient(135deg, #10b981, #059669); color: white;">
                    <div class="card-body">
                        <div class="d-flex justify-content-between align-items-center">
                            <div>
                                <h5 class="card-title">کل حساب‌ها</h5>
                                <h2 class="fw-bold"><fmt:formatNumber value="${totalAccounts}" /></h2>
                            </div>
                            <i class="fas fa-wallet fa-3x opacity-50"></i>
                        </div>
                        <p class="mt-3">فعال: <fmt:formatNumber value="${activeAccounts}" /> | فریز: <fmt:formatNumber value="${frozenAccounts}" /> | بسته: <fmt:formatNumber value="${closedAccounts}" /></p>
                        <p>کل موجودی بانک: <fmt:formatNumber value="${totalBankBalance}" type="currency" currencySymbol="ریال" /></p>
                    </div>
                </div>
            </div>

            <!-- تراکنش‌ها -->
            <div class="col-md-3">
                <div class="card shadow-lg border-0 rounded-3" style="background: linear-gradient(135deg, #3b82f6, #2563eb); color: white;">
                    <div class="card-body">
                        <div class="d-flex justify-content-between align-items-center">
                            <div>
                                <h5 class="card-title">کل تراکنش‌ها</h5>
                                <h2 class="fw-bold"><fmt:formatNumber value="${totalTransactions}" /></h2>
                            </div>
                            <i class="fas fa-exchange-alt fa-3x opacity-50"></i>
                        </div>
                        <p class="mt-3">امروز: <fmt:formatNumber value="${todayTransactions}" /> | حجم امروز: <fmt:formatNumber value="${todayTransactionVolume}" type="currency" currencySymbol="ریال" /></p>
                    </div>
                </div>
            </div>

            <!-- وام‌ها -->
            <div class="col-md-3">
                <div class="card shadow-lg border-0 rounded-3" style="background: linear-gradient(135deg, #f59e0b, #d97706); color: white;">
                    <div class="card-body">
                        <div class="d-flex justify-content-between align-items-center">
                            <div>
                                <h5 class="card-title">کل وام‌ها</h5>
                                <h2 class="fw-bold"><fmt:formatNumber value="${totalLoans}" /></h2>
                            </div>
                            <i class="fas fa-hand-holding-usd fa-3x opacity-50"></i>
                        </div>
                        <p class="mt-3">در انتظار: <fmt:formatNumber value="${pendingLoans}" /> | فعال: <fmt:formatNumber value="${activeLoans}" /> | رد شده: <fmt:formatNumber value="${rejectedLoans}" /></p>
                        <p>کل مبلغ فعال: <fmt:formatNumber value="${totalActiveLoanAmount}" type="currency" currencySymbol="ریال" /></p>
                    </div>
                </div>
            </div>
        </div>

        <!-- چارت‌ها -->
        <div class="row g-4 mb-5">
            <!-- چارت توزیع کاربران -->
            <div class="col-md-6">
                <div class="card shadow-lg border-0 rounded-3">
                    <div class="card-header bg-light">توزیع کاربران</div>
                    <div class="card-body">
                        <canvas id="userChart"></canvas>
                    </div>
                </div>
            </div>

            <!-- چارت توزیع حساب‌ها -->
            <div class="col-md-6">
                <div class="card shadow-lg border-0 rounded-3">
                    <div class="card-header bg-light">توزیع حساب‌ها</div>
                    <div class="card-body">
                        <canvas id="accountChart"></canvas>
                    </div>
                </div>
            </div>
        </div>

        <!-- لیست‌های اخیر -->
        <div class="row g-4">
            <!-- کاربران اخیر -->
            <div class="col-md-6">
                <div class="card shadow-lg border-0 rounded-3">
                    <div class="card-header bg-light">کاربران اخیر</div>
                    <div class="card-body">
                        <table class="table table-hover">
                            <thead>
                            <tr>
                                <th>نام</th>
                                <th>نام کاربری</th>
                                <th>تاریخ ثبت</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${recentUsers}" var="user">
                                <tr>
                                    <td><c:out value="${user.firstName} ${user.lastName}" /></td>
                                    <td><c:out value="${user.username}" /></td>
                                    <td><fmt:formatDate value="${user.createdAt}" pattern="yyyy/MM/dd HH:mm" /></td>
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
    // چارت کاربران (Pie Chart)
    const userCtx = document.getElementById('userChart').getContext('2d');
    new Chart(userCtx, {
        type: 'pie',
        data: {
            labels: ['فعال', 'غیرفعال'],
            datasets: [{
                data: [${activeUsers}, ${inactiveUsers}],
                backgroundColor: ['#10b981', '#ef4444']
            }]
        },
        options: { responsive: true }
    });

    // چارت حساب‌ها (Pie Chart)
    const accountCtx = document.getElementById('accountChart').getContext('2d');
    new Chart(accountCtx, {
        type: 'pie',
        data: {
            labels: ['فعال', 'فریز', 'بسته'],
            datasets: [{
                data: [${activeAccounts}, ${frozenAccounts}, ${closedAccounts}],
                backgroundColor: ['#10b981', '#f59e0b', '#ef4444']
            }]
        },
        options: { responsive: true }
    });
</script>
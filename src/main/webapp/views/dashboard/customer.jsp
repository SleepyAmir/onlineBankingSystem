<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/views/common/header.jsp">
    <jsp:param name="title" value="داشبورد مشتری" />
    <jsp:param name="isDashboard" value="true" />
</jsp:include>

<jsp:include page="/views/common/navbar.jsp" />
<jsp:include page="/views/common/sidebar.jsp" />

<div class="content-wrapper">
    <div class="container-fluid px-4 py-4">
        <!-- عنوان -->
        <div class="row mb-4">
            <div class="col-12">
                <h1 class="display-5 fw-bold">خوش آمدید، <c:out value="${fullName}" />!</h1>
                <p class="lead text-muted">نمای کلی حساب شما</p>
            </div>
        </div>

        <!-- اعلان‌ها -->
        <c:if test="${not empty notifications}">
            <div class="alert alert-warning alert-dismissible fade show mb-4" role="alert">
                <i class="fas fa-exclamation-triangle me-2"></i>
                <c:out value="${notifications}" />
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <!-- کارت‌های آمار -->
        <div class="row g-4 mb-5">
            <!-- موجودی کل -->
            <div class="col-md-4">
                <div class="card shadow-lg border-0 rounded-3" style="background: linear-gradient(135deg, #10b981, #059669); color: white;">
                    <div class="card-body">
                        <h5>موجودی کل</h5>
                        <h2><fmt:formatNumber value="${totalBalance}" type="currency" currencySymbol="ریال" /></h2>
                        <p>حساب‌های فعال: <fmt:formatNumber value="${activeAccountCount}" /></p>
                    </div>
                </div>
            </div>

            <!-- تراکنش‌ها -->
            <div class="col-md-4">
                <div class="card shadow-lg border-0 rounded-3" style="background: linear-gradient(135deg, #3b82f6, #2563eb); color: white;">
                    <div class="card-body">
                        <h5>تراکنش‌های این ماه</h5>
                        <h2><fmt:formatNumber value="${monthTransactions}" /></h2>
                        <p>حجم: <fmt:formatNumber value="${monthTransactionVolume}" type="currency" currencySymbol="ریال" /></p>
                    </div>
                </div>
            </div>

            <!-- وام‌ها -->
            <div class="col-md-4">
                <div class="card shadow-lg border-0 rounded-3" style="background: linear-gradient(135deg, #f59e0b, #d97706); color: white;">
                    <div class="card-body">
                        <h5>بدهی وام‌ها</h5>
                        <h2><fmt:formatNumber value="${totalLoanDebt}" type="currency" currencySymbol="ریال" /></h2>
                        <p>وام‌های فعال: <fmt:formatNumber value="${activeLoans.size()}" /> | در انتظار: <fmt:formatNumber value="${pendingLoans.size()}" /></p>
                    </div>
                </div>
            </div>
        </div>

        <!-- خلاصه مالی -->
        <div class="row mb-5">
            <div class="col-12">
                <div class="card shadow-lg border-0 rounded-3">
                    <div class="card-header bg-light">خلاصه مالی</div>
                    <div class="card-body">
                        <h4>خالص دارایی: <fmt:formatNumber value="${netWorth}" type="currency" currencySymbol="ریال" /></h4>
                        <canvas id="financialChart"></canvas>
                    </div>
                </div>
            </div>
        </div>

        <!-- لیست‌های اخیر -->
        <div class="row g-4">
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
                                    <td>${tx.transactionDate.toLocalDate()} ${tx.transactionDate.toLocalTime().withNano(0)}</td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <!-- وام‌های فعال -->
            <div class="col-md-6">
                <div class="card shadow-lg border-0 rounded-3">
                    <div class="card-header bg-light">وام‌های فعال</div>
                    <div class="card-body">
                        <table class="table table-hover">
                            <thead>
                            <tr>
                                <th>شماره وام</th>
                                <th>مبلغ</th>
                                <th>وضعیت</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${activeLoans}" var="loan">
                                <tr>
                                    <td><c:out value="${loan.loanNumber}" /></td>
                                    <td><fmt:formatNumber value="${loan.principal}" type="currency" currencySymbol="ریال" /></td>
                                    <td><c:out value="${loan.status}" /></td>
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

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<script>
    // چارت خلاصه مالی (Doughnut Chart)
    const financialCtx = document.getElementById('financialChart').getContext('2d');
    new Chart(financialCtx, {
        type: 'doughnut',
        data: {
            labels: ['موجودی', 'بدهی وام'],
            datasets: [{
                data: [${totalBalance}, ${totalLoanDebt}],
                backgroundColor: ['#10b981', '#ef4444']
            }]
        },
        options: { responsive: true }
    });
</script>
</body>
</html>
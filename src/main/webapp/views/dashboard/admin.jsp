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

        <!-- ✅ منوی دسترسی سریع -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="card shadow border-0">
                    <div class="card-header bg-dark text-white">
                        <i class="fas fa-bolt"></i> دسترسی سریع
                    </div>
                    <div class="card-body">
                        <div class="row g-3">
                            <div class="col-md-2">
                                <a href="${pageContext.request.contextPath}/users/list" class="btn btn-outline-primary w-100">
                                    <i class="fas fa-users"></i><br>مدیریت کاربران
                                </a>
                            </div>
                            <div class="col-md-2">
                                <a href="${pageContext.request.contextPath}/accounts/list" class="btn btn-outline-success w-100">
                                    <i class="fas fa-wallet"></i><br>مدیریت حساب‌ها
                                </a>
                            </div>
                            <div class="col-md-2">
                                <a href="${pageContext.request.contextPath}/loans/list" class="btn btn-outline-warning w-100">
                                    <i class="fas fa-hand-holding-usd"></i><br>مدیریت وام‌ها
                                </a>
                            </div>
                            <div class="col-md-2">
                                <a href="${pageContext.request.contextPath}/cards/list" class="btn btn-outline-info w-100">
                                    <i class="fas fa-credit-card"></i><br>مدیریت کارت‌ها
                                </a>
                            </div>
                            <div class="col-md-2">
                                <a href="${pageContext.request.contextPath}/transactions/history" class="btn btn-outline-secondary w-100">
                                    <i class="fas fa-exchange-alt"></i><br>تراکنش‌ها
                                </a>
                            </div>
                            <div class="col-md-2">
                                <a href="${pageContext.request.contextPath}/users/create" class="btn btn-primary w-100">
                                    <i class="fas fa-user-plus"></i><br>کاربر جدید
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
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
                        <p class="mt-3 mb-0">فعال: ${activeUsers} | غیرفعال: ${inactiveUsers}</p>
                        <a href="${pageContext.request.contextPath}/users/list" class="btn btn-light btn-sm mt-2">مدیریت</a>
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
                        <p class="mt-3 mb-0">فعال: ${activeAccounts} | فریز: ${frozenAccounts}</p>
                        <a href="${pageContext.request.contextPath}/accounts/list" class="btn btn-light btn-sm mt-2">مدیریت</a>
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
                        <p class="mt-3 mb-0">امروز: ${todayTransactions}</p>
                        <a href="${pageContext.request.contextPath}/transactions/history" class="btn btn-light btn-sm mt-2">مشاهده</a>
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
                        <p class="mt-3 mb-0">
                            <span class="badge bg-warning text-dark">در انتظار: ${pendingLoans}</span>
                        </p>
                        <a href="${pageContext.request.contextPath}/loans/list?status=PENDING" class="btn btn-light btn-sm mt-2">بررسی وام‌ها</a>
                    </div>
                </div>
            </div>
        </div>

        <!-- ✅ وام‌های در انتظار تأیید (با دکمه‌های عملیاتی) -->
        <c:if test="${pendingLoans > 0}">
            <div class="row mb-4">
                <div class="col-12">
                    <div class="card shadow border-danger">
                        <div class="card-header bg-danger text-white d-flex justify-content-between align-items-center">
                            <span><i class="fas fa-exclamation-triangle"></i> وام‌های در انتظار تأیید (${pendingLoans})</span>
                            <a href="${pageContext.request.contextPath}/loans/list?status=PENDING" class="btn btn-light btn-sm">مشاهده همه</a>
                        </div>
                        <div class="card-body">
                            <div class="table-responsive">
                                <table class="table table-hover">
                                    <thead>
                                    <tr>
                                        <th>شماره وام</th>
                                        <th>متقاضی</th>
                                        <th>مبلغ</th>
                                        <th>تاریخ درخواست</th>
                                        <th>عملیات</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach items="${recentLoans}" var="loan">
                                        <tr>
                                            <td>${loan.loanNumber}</td>
                                            <td>${loan.user.firstName} ${loan.user.lastName}</td>
                                            <td><fmt:formatNumber value="${loan.principal}" type="number" groupingUsed="true"/> ریال</td>
                                            <td>${loan.formattedCreatedAt}</td>
                                            <td>
                                                <a href="${pageContext.request.contextPath}/loans/detail?id=${loan.id}"
                                                   class="btn btn-sm btn-outline-primary">جزئیات</a>

                                                    <%-- ✅ دکمه تأیید وام --%>
                                                <form action="${pageContext.request.contextPath}/loans/approve" method="post" class="d-inline">
                                                    <input type="hidden" name="id" value="${loan.id}">
                                                    <button type="submit" class="btn btn-sm btn-success"
                                                            onclick="return confirm('آیا از تأیید این وام اطمینان دارید؟')">
                                                        <i class="fas fa-check"></i> تأیید
                                                    </button>
                                                </form>

                                                    <%-- ✅ دکمه رد وام --%>
                                                <form action="${pageContext.request.contextPath}/loans/reject" method="post" class="d-inline">
                                                    <input type="hidden" name="id" value="${loan.id}">
                                                    <button type="submit" class="btn btn-sm btn-danger"
                                                            onclick="return confirm('آیا از رد این وام اطمینان دارید؟')">
                                                        <i class="fas fa-times"></i> رد
                                                    </button>
                                                </form>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </c:if>

        <!-- چارت‌ها -->
        <div class="row g-4 mb-5">
            <div class="col-md-6">
                <div class="card shadow-lg border-0 rounded-3">
                    <div class="card-header bg-light">توزیع کاربران</div>
                    <div class="card-body">
                        <canvas id="userChart"></canvas>
                    </div>
                </div>
            </div>
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
                    <div class="card-header bg-light d-flex justify-content-between align-items-center">
                        <span>کاربران اخیر</span>
                        <a href="${pageContext.request.contextPath}/users/list" class="btn btn-sm btn-outline-primary">همه</a>
                    </div>
                    <div class="card-body">
                        <table class="table table-hover">
                            <thead>
                            <tr>
                                <th>نام</th>
                                <th>نام کاربری</th>
                                <th>وضعیت</th>
                                <th>عملیات</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${recentUsers}" var="user">
                                <tr>
                                    <td>${user.firstName} ${user.lastName}</td>
                                    <td>${user.username}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${user.active}">
                                                <span class="badge bg-success">فعال</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-danger">غیرفعال</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/users/detail?id=${user.id}"
                                           class="btn btn-sm btn-outline-info">جزئیات</a>
                                        <a href="${pageContext.request.contextPath}/users/edit?id=${user.id}"
                                           class="btn btn-sm btn-outline-warning">ویرایش</a>
                                    </td>
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
                    <div class="card-header bg-light d-flex justify-content-between align-items-center">
                        <span>تراکنش‌های اخیر</span>
                        <a href="${pageContext.request.contextPath}/transactions/history" class="btn btn-sm btn-outline-primary">همه</a>
                    </div>
                    <div class="card-body">
                        <table class="table table-hover">
                            <thead>
                            <tr>
                                <th>نوع</th>
                                <th>مبلغ</th>
                                <th>وضعیت</th>
                                <th>عملیات</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${recentTransactions}" var="tx">
                                <tr>
                                    <td>
                                        <c:choose>
                                            <c:when test="${tx.type == 'DEPOSIT'}"><span class="badge bg-success">واریز</span></c:when>
                                            <c:when test="${tx.type == 'WITHDRAWAL'}"><span class="badge bg-danger">برداشت</span></c:when>
                                            <c:when test="${tx.type == 'TRANSFER'}"><span class="badge bg-info">انتقال</span></c:when>
                                            <c:otherwise><span class="badge bg-secondary">${tx.type}</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td><fmt:formatNumber value="${tx.amount}" type="number" groupingUsed="true"/></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${tx.status == 'COMPLETED'}"><span class="badge bg-success">موفق</span></c:when>
                                            <c:when test="${tx.status == 'FAILED'}"><span class="badge bg-danger">ناموفق</span></c:when>
                                            <c:otherwise><span class="badge bg-warning">${tx.status}</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/transactions/detail?id=${tx.id}"
                                           class="btn btn-sm btn-outline-info">جزئیات</a>
                                    </td>
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

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    const userCtx = document.getElementById('userChart').getContext('2d');
    new Chart(userCtx, {
        type: 'pie',
        data: {
            labels: ['فعال', 'غیرفعال'],
            datasets: [{ data: [${activeUsers}, ${inactiveUsers}], backgroundColor: ['#10b981', '#ef4444'] }]
        },
        options: { responsive: true }
    });

    const accountCtx = document.getElementById('accountChart').getContext('2d');
    new Chart(accountCtx, {
        type: 'pie',
        data: {
            labels: ['فعال', 'فریز', 'بسته'],
            datasets: [{ data: [${activeAccounts}, ${frozenAccounts}, ${closedAccounts}], backgroundColor: ['#10b981', '#f59e0b', '#ef4444'] }]
        },
        options: { responsive: true }
    });
</script>
</body>
</html>
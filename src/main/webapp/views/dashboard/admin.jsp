<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/views/common/header.jsp">
    <jsp:param name="title" value="داشبورد ادمین" />
    <jsp:param name="isDashboard" value="true" />
</jsp:include>

<jsp:include page="/views/common/navbar.jsp" />
<jsp:include page="/views/common/sidebar.jsp" />

<style>
    @keyframes fadeInUp {
        from {
            opacity: 0;
            transform: translateY(30px);
        }
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }

    @keyframes pulse {
        0%, 100% {
            transform: scale(1);
        }
        50% {
            transform: scale(1.05);
        }
    }

    @keyframes shimmer {
        0% {
            background-position: -1000px 0;
        }
        100% {
            background-position: 1000px 0;
        }
    }

    .content-wrapper {
        background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
        min-height: 100vh;
    }

    .dashboard-header {
        background: white;
        border-radius: 25px;
        padding: 2.5rem;
        margin-bottom: 2rem;
        box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
        animation: fadeInUp 0.6s ease-out;
        position: relative;
        overflow: hidden;
    }

    .dashboard-header::before {
        content: '';
        position: absolute;
        top: 0;
        left: -100%;
        width: 100%;
        height: 100%;
        background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
        animation: shimmer 3s infinite;
    }

    .dashboard-header h1 {
        font-size: 2.5rem;
        font-weight: 800;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        margin-bottom: 0.5rem;
    }

    .quick-actions-card {
        background: white;
        border-radius: 25px;
        padding: 2rem;
        box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
        margin-bottom: 2rem;
        animation: fadeInUp 0.6s ease-out 0.1s backwards;
    }

    .quick-actions-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
        gap: 1.5rem;
        margin-top: 1.5rem;
    }

    .quick-action-btn {
        background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
        border: 2px solid #e9ecef;
        border-radius: 20px;
        padding: 2rem 1rem;
        text-align: center;
        text-decoration: none;
        color: #1f2937;
        transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
        position: relative;
        overflow: hidden;
    }

    .quick-action-btn::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
        opacity: 0;
        transition: opacity 0.4s ease;
        border-radius: 20px;
    }

    .quick-action-btn:hover {
        transform: translateY(-10px);
        box-shadow: 0 20px 40px rgba(102, 126, 234, 0.3);
        border-color: var(--primary-color);
    }

    .quick-action-btn:hover::before {
        opacity: 1;
    }

    .quick-action-btn:hover i,
    .quick-action-btn:hover span {
        color: white;
    }

    .quick-action-btn i {
        font-size: 2.5rem;
        margin-bottom: 1rem;
        display: block;
        transition: all 0.4s ease;
        position: relative;
        z-index: 1;
    }

    .quick-action-btn span {
        display: block;
        font-weight: 600;
        font-size: 0.9rem;
        position: relative;
        z-index: 1;
        transition: color 0.4s ease;
    }

    .stat-card {
        background: white;
        border-radius: 25px;
        padding: 2rem;
        box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
        position: relative;
        overflow: hidden;
        transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
        animation: fadeInUp 0.6s ease-out backwards;
    }

    .stat-card:nth-child(1) { animation-delay: 0.2s; }
    .stat-card:nth-child(2) { animation-delay: 0.3s; }
    .stat-card:nth-child(3) { animation-delay: 0.4s; }
    .stat-card:nth-child(4) { animation-delay: 0.5s; }

    .stat-card::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        height: 5px;
        background: linear-gradient(90deg, var(--card-color-1), var(--card-color-2));
    }

    .stat-card:hover {
        transform: translateY(-10px);
        box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
    }

    .stat-card.purple::before {
        --card-color-1: #667eea;
        --card-color-2: #764ba2;
    }

    .stat-card.green::before {
        --card-color-1: #10b981;
        --card-color-2: #059669;
    }

    .stat-card.blue::before {
        --card-color-1: #3b82f6;
        --card-color-2: #2563eb;
    }

    .stat-card.orange::before {
        --card-color-1: #f59e0b;
        --card-color-2: #d97706;
    }

    .stat-icon {
        width: 70px;
        height: 70px;
        border-radius: 20px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 2rem;
        margin-bottom: 1.5rem;
        position: relative;
        transition: all 0.4s ease;
    }

    .stat-card:hover .stat-icon {
        transform: rotate(-10deg) scale(1.1);
    }

    .stat-card.purple .stat-icon {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        box-shadow: 0 10px 30px rgba(102, 126, 234, 0.3);
    }

    .stat-card.green .stat-icon {
        background: linear-gradient(135deg, #10b981 0%, #059669 100%);
        color: white;
        box-shadow: 0 10px 30px rgba(16, 185, 129, 0.3);
    }

    .stat-card.blue .stat-icon {
        background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
        color: white;
        box-shadow: 0 10px 30px rgba(59, 130, 246, 0.3);
    }

    .stat-card.orange .stat-icon {
        background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
        color: white;
        box-shadow: 0 10px 30px rgba(245, 158, 11, 0.3);
    }

    .stat-title {
        font-size: 0.9rem;
        color: #6b7280;
        font-weight: 600;
        margin-bottom: 0.5rem;
        text-transform: uppercase;
        letter-spacing: 1px;
    }

    .stat-value {
        font-size: 2.5rem;
        font-weight: 800;
        color: #1f2937;
        margin-bottom: 0.5rem;
    }

    .stat-details {
        font-size: 0.85rem;
        color: #9ca3af;
        margin-bottom: 1rem;
    }

    .stat-action {
        display: inline-block;
        padding: 0.5rem 1.5rem;
        background: #f3f4f6;
        color: #1f2937;
        text-decoration: none;
        border-radius: 50px;
        font-size: 0.85rem;
        font-weight: 600;
        transition: all 0.3s ease;
    }

    .stat-action:hover {
        background: #1f2937;
        color: white;
        transform: translateX(-5px);
    }

    .pending-loans-card {
        background: white;
        border-radius: 25px;
        padding: 2rem;
        box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
        margin-bottom: 2rem;
        animation: fadeInUp 0.6s ease-out 0.6s backwards;
        border: 3px solid #fee2e2;
    }

    .pending-loans-header {
        background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
        color: white;
        padding: 1.5rem;
        border-radius: 20px;
        margin-bottom: 1.5rem;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .pending-loans-header h3 {
        margin: 0;
        font-size: 1.3rem;
        font-weight: 700;
    }

    .pending-count-badge {
        background: white;
        color: #ef4444;
        padding: 0.5rem 1rem;
        border-radius: 50px;
        font-weight: 800;
        font-size: 1.1rem;
        animation: pulse 2s infinite;
    }

    .modern-table {
        width: 100%;
        border-collapse: separate;
        border-spacing: 0 0.5rem;
    }

    .modern-table thead tr {
        background: #f9fafb;
    }

    .modern-table thead th {
        padding: 1rem;
        text-align: right;
        font-weight: 700;
        color: #374151;
        font-size: 0.85rem;
        text-transform: uppercase;
        letter-spacing: 1px;
        border: none;
    }

    .modern-table tbody tr {
        background: white;
        transition: all 0.3s ease;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
    }

    .modern-table tbody tr:hover {
        transform: translateX(-5px);
        box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
    }

    .modern-table tbody td {
        padding: 1.25rem 1rem;
        border: none;
        vertical-align: middle;
    }

    .modern-table tbody tr td:first-child {
        border-radius: 15px 0 0 15px;
    }

    .modern-table tbody tr td:last-child {
        border-radius: 0 15px 15px 0;
    }

    .action-btn {
        padding: 0.5rem 1rem;
        border-radius: 10px;
        border: none;
        font-size: 0.85rem;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.3s ease;
        text-decoration: none;
        display: inline-block;
        margin: 0 0.25rem;
    }

    .action-btn.primary {
        background: linear-gradient(135deg, #667eea, #764ba2);
        color: white;
    }

    .action-btn.success {
        background: linear-gradient(135deg, #10b981, #059669);
        color: white;
    }

    .action-btn.danger {
        background: linear-gradient(135deg, #ef4444, #dc2626);
        color: white;
    }

    .action-btn:hover {
        transform: translateY(-3px);
        box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
    }

    .chart-card {
        background: white;
        border-radius: 25px;
        padding: 2rem;
        box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
        animation: fadeInUp 0.6s ease-out backwards;
        height: 100%;
    }

    .chart-card:nth-child(1) { animation-delay: 0.7s; }
    .chart-card:nth-child(2) { animation-delay: 0.8s; }

    .chart-header {
        font-size: 1.2rem;
        font-weight: 700;
        color: #1f2937;
        margin-bottom: 1.5rem;
        padding-bottom: 1rem;
        border-bottom: 3px solid #f3f4f6;
    }

    .recent-card {
        background: white;
        border-radius: 25px;
        padding: 2rem;
        box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
        animation: fadeInUp 0.6s ease-out backwards;
        height: 100%;
    }

    .recent-card:nth-child(1) { animation-delay: 0.9s; }
    .recent-card:nth-child(2) { animation-delay: 1s; }

    .recent-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 1.5rem;
        padding-bottom: 1rem;
        border-bottom: 3px solid #f3f4f6;
    }

    .recent-header h3 {
        font-size: 1.2rem;
        font-weight: 700;
        color: #1f2937;
        margin: 0;
    }

    .view-all-btn {
        padding: 0.5rem 1.25rem;
        background: linear-gradient(135deg, #667eea, #764ba2);
        color: white;
        text-decoration: none;
        border-radius: 50px;
        font-size: 0.85rem;
        font-weight: 600;
        transition: all 0.3s ease;
    }

    .view-all-btn:hover {
        transform: translateY(-2px);
        box-shadow: 0 10px 25px rgba(102, 126, 234, 0.3);
    }

    .modern-badge {
        padding: 0.35rem 0.85rem;
        border-radius: 50px;
        font-size: 0.8rem;
        font-weight: 700;
        display: inline-block;
    }

    .modern-badge.success {
        background: linear-gradient(135deg, #d1fae5, #a7f3d0);
        color: #065f46;
    }

    .modern-badge.danger {
        background: linear-gradient(135deg, #fee2e2, #fecaca);
        color: #991b1b;
    }

    .modern-badge.warning {
        background: linear-gradient(135deg, #fef3c7, #fde68a);
        color: #92400e;
    }

    .modern-badge.info {
        background: linear-gradient(135deg, #dbeafe, #bfdbfe);
        color: #1e40af;
    }

    @media (max-width: 768px) {
        .dashboard-header h1 {
            font-size: 1.75rem;
        }

        .quick-actions-grid {
            grid-template-columns: repeat(2, 1fr);
        }

        .stat-value {
            font-size: 2rem;
        }
    }
</style>

<div class="content-wrapper">
    <div class="container-fluid px-4 py-4">
        <!-- هدر داشبورد -->
        <div class="dashboard-header">
            <h1><i class="fas fa-crown"></i> داشبورد مدیریت</h1>
            <p class="lead text-muted mb-0">نمای کلی و مدیریت سیستم بانکی</p>
        </div>

        <!-- دسترسی سریع -->
        <div class="quick-actions-card">
            <h3 class="fw-bold mb-0"><i class="fas fa-bolt text-warning"></i> دسترسی سریع</h3>
            <div class="quick-actions-grid">
                <a href="${pageContext.request.contextPath}/users/list" class="quick-action-btn">
                    <i class="fas fa-users"></i>
                    <span>مدیریت کاربران</span>
                </a>
                <a href="${pageContext.request.contextPath}/accounts/list" class="quick-action-btn">
                    <i class="fas fa-wallet"></i>
                    <span>مدیریت حساب‌ها</span>
                </a>
                <a href="${pageContext.request.contextPath}/loans/list" class="quick-action-btn">
                    <i class="fas fa-hand-holding-usd"></i>
                    <span>مدیریت وام‌ها</span>
                </a>
                <a href="${pageContext.request.contextPath}/cards/list" class="quick-action-btn">
                    <i class="fas fa-credit-card"></i>
                    <span>مدیریت کارت‌ها</span>
                </a>
                <a href="${pageContext.request.contextPath}/transactions/history" class="quick-action-btn">
                    <i class="fas fa-exchange-alt"></i>
                    <span>تراکنش‌ها</span>
                </a>
                <a href="${pageContext.request.contextPath}/users/create" class="quick-action-btn">
                    <i class="fas fa-user-plus"></i>
                    <span>کاربر جدید</span>
                </a>
            </div>
        </div>

        <!-- کارت‌های آمار -->
        <div class="row g-4 mb-4">
            <div class="col-md-3">
                <div class="stat-card purple">
                    <div class="stat-icon">
                        <i class="fas fa-users"></i>
                    </div>
                    <div class="stat-title">کل کاربران</div>
                    <div class="stat-value"><fmt:formatNumber value="${totalUsers}" /></div>
                    <div class="stat-details">فعال: ${activeUsers} | غیرفعال: ${inactiveUsers}</div>
                    <a href="${pageContext.request.contextPath}/users/list" class="stat-action">
                        مشاهده همه <i class="fas fa-arrow-left"></i>
                    </a>
                </div>
            </div>

            <div class="col-md-3">
                <div class="stat-card green">
                    <div class="stat-icon">
                        <i class="fas fa-wallet"></i>
                    </div>
                    <div class="stat-title">کل حساب‌ها</div>
                    <div class="stat-value"><fmt:formatNumber value="${totalAccounts}" /></div>
                    <div class="stat-details">فعال: ${activeAccounts} | فریز: ${frozenAccounts}</div>
                    <a href="${pageContext.request.contextPath}/accounts/list" class="stat-action">
                        مشاهده همه <i class="fas fa-arrow-left"></i>
                    </a>
                </div>
            </div>

            <div class="col-md-3">
                <div class="stat-card blue">
                    <div class="stat-icon">
                        <i class="fas fa-exchange-alt"></i>
                    </div>
                    <div class="stat-title">کل تراکنش‌ها</div>
                    <div class="stat-value"><fmt:formatNumber value="${totalTransactions}" /></div>
                    <div class="stat-details">امروز: ${todayTransactions}</div>
                    <a href="${pageContext.request.contextPath}/transactions/history" class="stat-action">
                        مشاهده همه <i class="fas fa-arrow-left"></i>
                    </a>
                </div>
            </div>

            <div class="col-md-3">
                <div class="stat-card orange">
                    <div class="stat-icon">
                        <i class="fas fa-hand-holding-usd"></i>
                    </div>
                    <div class="stat-title">کل وام‌ها</div>
                    <div class="stat-value"><fmt:formatNumber value="${totalLoans}" /></div>
                    <div class="stat-details">در انتظار: ${pendingLoans}</div>
                    <a href="${pageContext.request.contextPath}/loans/list" class="stat-action">
                        مشاهده همه <i class="fas fa-arrow-left"></i>
                    </a>
                </div>
            </div>
        </div>

        <!-- وام‌های در انتظار -->
        <c:if test="${pendingLoans > 0}">
            <div class="pending-loans-card">
                <div class="pending-loans-header">
                    <h3><i class="fas fa-exclamation-triangle"></i> وام‌های نیازمند بررسی</h3>
                    <div class="pending-count-badge">${pendingLoans}</div>
                </div>

                <div class="table-responsive">
                    <table class="modern-table">
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
                                <td><strong>${loan.loanNumber}</strong></td>
                                <td>${loan.user.firstName} ${loan.user.lastName}</td>
                                <td><strong><fmt:formatNumber value="${loan.principal}" type="number" groupingUsed="true"/> ریال</strong></td>
                                <td>${loan.formattedCreatedAt}</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/loans/detail?id=${loan.id}"
                                       class="action-btn primary">
                                        <i class="fas fa-eye"></i> جزئیات
                                    </a>
                                    <form action="${pageContext.request.contextPath}/loans/approve" method="post" class="d-inline">
                                        <input type="hidden" name="id" value="${loan.id}">
                                        <button type="submit" class="action-btn success"
                                                onclick="return confirm('آیا از تأیید این وام اطمینان دارید؟')">
                                            <i class="fas fa-check"></i> تأیید
                                        </button>
                                    </form>
                                    <form action="${pageContext.request.contextPath}/loans/reject" method="post" class="d-inline">
                                        <input type="hidden" name="id" value="${loan.id}">
                                        <button type="submit" class="action-btn danger"
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
        </c:if>

        <!-- نمودارها -->
        <div class="row g-4 mb-4">
            <div class="col-md-6">
                <div class="chart-card">
                    <div class="chart-header">
                        <i class="fas fa-chart-pie text-primary"></i> توزیع کاربران
                    </div>
                    <canvas id="userChart"></canvas>
                </div>
            </div>
            <div class="col-md-6">
                <div class="chart-card">
                    <div class="chart-header">
                        <i class="fas fa-chart-pie text-success"></i> توزیع حساب‌ها
                    </div>
                    <canvas id="accountChart"></canvas>
                </div>
            </div>
        </div>

        <!-- اطلاعات اخیر -->
        <div class="row g-4">
            <div class="col-md-6">
                <div class="recent-card">
                    <div class="recent-header">
                        <h3><i class="fas fa-user-clock"></i> کاربران اخیر</h3>
                        <a href="${pageContext.request.contextPath}/users/list" class="view-all-btn">
                            مشاهده همه <i class="fas fa-arrow-left"></i>
                        </a>
                    </div>
                    <div class="table-responsive">
                        <table class="modern-table">
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
                                    <td><strong>${user.firstName} ${user.lastName}</strong></td>
                                    <td>${user.username}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${user.active}">
                                                <span class="modern-badge success">فعال</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="modern-badge danger">غیرفعال</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/users/detail?id=${user.id}"
                                           class="action-btn primary">
                                            <i class="fas fa-eye"></i>
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <div class="col-md-6">
                <div class="recent-card">
                    <div class="recent-header">
                        <h3><i class="fas fa-clock"></i> تراکنش‌های اخیر</h3>
                        <a href="${pageContext.request.contextPath}/transactions/history" class="view-all-btn">
                            مشاهده همه <i class="fas fa-arrow-left"></i>
                        </a>
                    </div>
                    <div class="table-responsive">
                        <table class="modern-table">
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
                                            <c:when test="${tx.type == 'DEPOSIT'}">
                                                <span class="modern-badge success"><i class="fas fa-arrow-down"></i> واریز</span>
                                            </c:when>
                                            <c:when test="${tx.type == 'WITHDRAWAL'}">
                                                <span class="modern-badge danger"><i class="fas fa-arrow-up"></i> برداشت</span>
                                            </c:when>
                                            <c:when test="${tx.type == 'TRANSFER'}">
                                                <span class="modern-badge info"><i class="fas fa-exchange-alt"></i> انتقال</span>
                                            </c:when>
                                        </c:choose>
                                    </td>
                                    <td><strong><fmt:formatNumber value="${tx.amount}" type="number" groupingUsed="true"/> ریال</strong></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${tx.status == 'COMPLETED'}">
                                                <span class="modern-badge success">موفق</span>
                                            </c:when>
                                            <c:when test="${tx.status == 'FAILED'}">
                                                <span class="modern-badge danger">ناموفق</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="modern-badge warning">${tx.status}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/transactions/detail?id=${tx.id}"
                                           class="action-btn primary">
                                            <i class="fas fa-eye"></i>
                                        </a>
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
    // نمودار کاربران
    const userCtx = document.getElementById('userChart').getContext('2d');
    new Chart(userCtx, {
        type: 'doughnut',
        data: {
            labels: ['فعال', 'غیرفعال'],
            datasets: [{
                data: [${activeUsers}, ${inactiveUsers}],
                backgroundColor: [
                    'rgba(16, 185, 129, 0.8)',
                    'rgba(239, 68, 68, 0.8)'
                ],
                borderWidth: 0,
                hoverOffset: 20
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        padding: 20,
                        font: {
                            family: 'Vazirmatn',
                            size: 14,
                            weight: '600'
                        }
                    }
                }
            }
        }
    });

    // نمودار حساب‌ها
    const accountCtx = document.getElementById('accountChart').getContext('2d');
    new Chart(accountCtx, {
        type: 'doughnut',
        data: {
            labels: ['فعال', 'فریز', 'بسته'],
            datasets: [{
                data: [${activeAccounts}, ${frozenAccounts}, ${closedAccounts}],
                backgroundColor: [
                    'rgba(16, 185, 129, 0.8)',
                    'rgba(245, 158, 11, 0.8)',
                    'rgba(239, 68, 68, 0.8)'
                ],
                borderWidth: 0,
                hoverOffset: 20
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        padding: 20,
                        font: {
                            family: 'Vazirmatn',
                            size: 14,
                            weight: '600'
                        }
                    }
                }
            }
        }
    });
</script>
</body>
</html>
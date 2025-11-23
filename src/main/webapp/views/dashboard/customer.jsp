<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/views/common/header.jsp">
    <jsp:param name="title" value="داشبورد مشتری" />
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

    @keyframes slideInRight {
        from {
            opacity: 0;
            transform: translateX(-30px);
        }
        to {
            opacity: 1;
            transform: translateX(0);
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

    .content-wrapper {
        background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
        min-height: 100vh;
    }

    .welcome-header {
        animation: fadeInUp 0.6s ease-out;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        padding: 2rem;
        border-radius: 20px;
        margin-bottom: 2rem;
        box-shadow: 0 10px 40px rgba(102, 126, 234, 0.3);
    }

    .welcome-header h1 {
        font-size: 2.5rem;
        font-weight: 800;
        margin-bottom: 0.5rem;
    }

    .welcome-header p {
        opacity: 0.9;
        font-size: 1.1rem;
    }

    /* کارت‌های آماری پیشرفته */
    .stat-card {
        background: white;
        border-radius: 20px;
        padding: 2rem;
        box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
        transition: all 0.3s ease;
        animation: fadeInUp 0.6s ease-out;
        position: relative;
        overflow: hidden;
    }

    .stat-card::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 5px;
        background: linear-gradient(90deg, var(--card-color-1), var(--card-color-2));
    }

    .stat-card:hover {
        transform: translateY(-10px);
        box-shadow: 0 20px 50px rgba(0, 0, 0, 0.15);
    }

    .stat-card:nth-child(1) {
        animation-delay: 0.1s;
    }

    .stat-card:nth-child(2) {
        animation-delay: 0.2s;
    }

    .stat-card:nth-child(3) {
        animation-delay: 0.3s;
    }

    .stat-card-icon {
        width: 70px;
        height: 70px;
        border-radius: 15px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 2rem;
        margin-bottom: 1rem;
        background: linear-gradient(135deg, var(--card-color-1), var(--card-color-2));
        color: white;
        box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
    }

    .stat-card.balance {
        --card-color-1: #10b981;
        --card-color-2: #059669;
    }

    .stat-card.transactions {
        --card-color-1: #3b82f6;
        --card-color-2: #2563eb;
    }

    .stat-card.loans {
        --card-color-1: #f59e0b;
        --card-color-2: #d97706;
    }

    .stat-card-title {
        font-size: 0.9rem;
        color: #6b7280;
        font-weight: 600;
        text-transform: uppercase;
        letter-spacing: 1px;
        margin-bottom: 0.5rem;
    }

    .stat-card-value {
        font-size: 2rem;
        font-weight: 800;
        color: #1f2937;
        margin-bottom: 0.5rem;
    }

    .stat-card-subtitle {
        font-size: 0.9rem;
        color: #9ca3af;
    }

    /* نوتیفیکیشن زیبا */
    .notification-banner {
        background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
        border: none;
        border-radius: 15px;
        padding: 1.5rem;
        margin-bottom: 2rem;
        box-shadow: 0 5px 20px rgba(245, 158, 11, 0.2);
        animation: slideInRight 0.6s ease-out;
    }

    .notification-banner i {
        font-size: 1.5rem;
        color: #d97706;
    }

    /* کارت‌های محتوای اصلی */
    .content-card {
        background: white;
        border-radius: 20px;
        box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
        overflow: hidden;
        animation: fadeInUp 0.6s ease-out;
        animation-delay: 0.4s;
    }

    .content-card-header {
        background: linear-gradient(135deg, #f3f4f6 0%, #e5e7eb 100%);
        padding: 1.5rem;
        border-bottom: 2px solid #e5e7eb;
    }

    .content-card-header h5 {
        margin: 0;
        font-weight: 700;
        color: #1f2937;
        display: flex;
        align-items: center;
        gap: 0.75rem;
    }

    .content-card-header i {
        width: 40px;
        height: 40px;
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        background: white;
        color: var(--primary-color);
        font-size: 1.2rem;
    }

    .content-card-body {
        padding: 1.5rem;
    }

    /* جدول زیبا */
    .modern-table {
        width: 100%;
        border-collapse: separate;
        border-spacing: 0;
    }

    .modern-table thead th {
        background: #f9fafb;
        color: #6b7280;
        font-weight: 600;
        text-transform: uppercase;
        font-size: 0.75rem;
        letter-spacing: 0.5px;
        padding: 1rem;
        border-bottom: 2px solid #e5e7eb;
    }

    .modern-table tbody tr {
        transition: all 0.3s ease;
    }

    .modern-table tbody tr:hover {
        background: #f9fafb;
        transform: scale(1.01);
    }

    .modern-table tbody td {
        padding: 1rem;
        border-bottom: 1px solid #f3f4f6;
        color: #374151;
    }

    .modern-table tbody tr:last-child td {
        border-bottom: none;
    }

    /* بج‌های وضعیت */
    .status-badge {
        padding: 0.4rem 0.8rem;
        border-radius: 20px;
        font-size: 0.8rem;
        font-weight: 600;
        display: inline-flex;
        align-items: center;
        gap: 0.3rem;
    }

    .status-badge.active {
        background: #d1fae5;
        color: #065f46;
    }

    .status-badge.approved {
        background: #dbeafe;
        color: #1e40af;
    }

    .status-badge.pending {
        background: #fef3c7;
        color: #92400e;
    }

    /* نمودار زیبا */
    .chart-container {
        position: relative;
        padding: 2rem;
        background: linear-gradient(135deg, #f9fafb 0%, #ffffff 100%);
        border-radius: 15px;
        margin-top: 1.5rem;
    }

    /* خلاصه مالی */
    .financial-summary {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        border-radius: 20px;
        padding: 2rem;
        margin-bottom: 2rem;
        box-shadow: 0 15px 40px rgba(102, 126, 234, 0.3);
        animation: fadeInUp 0.6s ease-out;
        animation-delay: 0.3s;
    }

    .financial-summary h4 {
        font-size: 1.5rem;
        margin-bottom: 1rem;
        opacity: 0.9;
    }

    .financial-summary .net-worth {
        font-size: 3rem;
        font-weight: 800;
        margin-bottom: 1rem;
    }

    .financial-summary .progress-bar-custom {
        height: 10px;
        border-radius: 10px;
        background: rgba(255, 255, 255, 0.3);
        overflow: hidden;
    }

    .financial-summary .progress-fill {
        height: 100%;
        background: white;
        border-radius: 10px;
        transition: width 1s ease;
    }

    /* آیکون‌های انیمیشن دار */
    @keyframes float {
        0%, 100% {
            transform: translateY(0);
        }
        50% {
            transform: translateY(-10px);
        }
    }

    .floating-icon {
        animation: float 3s ease-in-out infinite;
    }

    /* امتی دیتا */
    .empty-state {
        text-align: center;
        padding: 3rem;
        color: #9ca3af;
    }

    .empty-state i {
        font-size: 4rem;
        margin-bottom: 1rem;
        opacity: 0.3;
    }

    /* ریسپانسیو */
    @media (max-width: 768px) {
        .welcome-header h1 {
            font-size: 1.8rem;
        }

        .stat-card-value {
            font-size: 1.5rem;
        }

        .financial-summary .net-worth {
            font-size: 2rem;
        }
    }
</style>

<div class="content-wrapper">
    <div class="container-fluid px-4 py-4">
        <!-- هدر خوش‌آمدگویی -->
        <div class="welcome-header">
            <h1>
                <i class="fas fa-hand-sparkles floating-icon"></i>
                سلام، <c:out value="${fullName}" />!
            </h1>
            <p>امیدواریم روز عالی‌ای داشته باشید. اینجا نمای کلی از وضعیت مالی شماست.</p>
        </div>

        <!-- اعلان‌ها -->
        <c:if test="${not empty notifications}">
            <div class="notification-banner alert alert-dismissible fade show" role="alert">
                <i class="fas fa-bell"></i>
                <strong class="ms-2">اعلان‌های مهم:</strong>
                <c:out value="${notifications}" />
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <!-- کارت‌های آمار -->
        <div class="row g-4 mb-4">
            <!-- موجودی کل -->
            <div class="col-md-4">
                <div class="stat-card balance">
                    <div class="stat-card-icon">
                        <i class="fas fa-wallet"></i>
                    </div>
                    <div class="stat-card-title">موجودی کل</div>
                    <div class="stat-card-value">
                        <fmt:formatNumber value="${totalBalance}" pattern="#,###" /> ریال
                    </div>
                    <div class="stat-card-subtitle">
                        <i class="fas fa-check-circle"></i>
                        <fmt:formatNumber value="${activeAccountCount}" /> حساب فعال
                    </div>
                </div>
            </div>

            <!-- تراکنش‌ها -->
            <div class="col-md-4">
                <div class="stat-card transactions">
                    <div class="stat-card-icon">
                        <i class="fas fa-exchange-alt"></i>
                    </div>
                    <div class="stat-card-title">تراکنش‌های این ماه</div>
                    <div class="stat-card-value">
                        <fmt:formatNumber value="${monthTransactions}" /> تراکنش
                    </div>
                    <div class="stat-card-subtitle">
                        <i class="fas fa-coins"></i>
                        حجم: <fmt:formatNumber value="${monthTransactionVolume}" pattern="#,###" /> ریال
                    </div>
                </div>
            </div>

            <!-- وام‌ها -->
            <div class="col-md-4">
                <div class="stat-card loans">
                    <div class="stat-card-icon">
                        <i class="fas fa-hand-holding-usd"></i>
                    </div>
                    <div class="stat-card-title">بدهی وام‌ها</div>
                    <div class="stat-card-value">
                        <fmt:formatNumber value="${totalLoanDebt}" pattern="#,###" /> ریال
                    </div>
                    <div class="stat-card-subtitle">
                        <i class="fas fa-list"></i>
                        <fmt:formatNumber value="${activeLoans.size()}" /> فعال |
                        <fmt:formatNumber value="${pendingLoans.size()}" /> در انتظار
                    </div>
                </div>
            </div>
        </div>

        <!-- خلاصه مالی -->
        <div class="financial-summary">
            <h4>
                <i class="fas fa-chart-line me-2"></i>
                خالص دارایی شما
            </h4>
            <div class="net-worth">
                <fmt:formatNumber value="${netWorth}" pattern="#,###" /> ریال
            </div>
            <div class="progress-bar-custom">
                <div class="progress-fill" style="width: ${totalBalance.doubleValue() / (totalBalance.doubleValue() + totalLoanDebt.doubleValue()) * 100}%"></div>
            </div>
            <div class="mt-3" style="opacity: 0.9;">
                <small>نسبت دارایی به بدهی: ${String.format("%.1f", totalBalance.doubleValue() / (totalLoanDebt.doubleValue() > 0 ? totalLoanDebt.doubleValue() : 1))}x</small>
            </div>
        </div>

        <!-- نمودار مالی -->
        <div class="content-card mb-4">
            <div class="content-card-header">
                <h5>
                    <i class="fas fa-chart-pie"></i>
                    نمای کلی وضعیت مالی
                </h5>
            </div>
            <div class="content-card-body">
                <div class="chart-container">
                    <canvas id="financialChart" height="100"></canvas>
                </div>
            </div>
        </div>

        <!-- لیست‌های اخیر -->
        <div class="row g-4">
            <!-- تراکنش‌های اخیر -->
            <div class="col-lg-6">
                <div class="content-card">
                    <div class="content-card-header">
                        <h5>
                            <i class="fas fa-history"></i>
                            تراکنش‌های اخیر
                        </h5>
                    </div>
                    <div class="content-card-body">
                        <c:choose>
                            <c:when test="${not empty recentTransactions}">
                                <table class="modern-table">
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
                                            <td>
                                                <span class="status-badge active">
                                                    <i class="fas fa-circle" style="font-size: 0.5rem;"></i>
                                                    <c:out value="${tx.type}" />
                                                </span>
                                            </td>
                                            <td>
                                                <strong><fmt:formatNumber value="${tx.amount}" pattern="#,###" /></strong> ریال
                                            </td>
                                            <td>
                                                <small>${tx.transactionDate.toLocalDate()}</small>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </c:when>
                            <c:otherwise>
                                <div class="empty-state">
                                    <i class="fas fa-inbox"></i>
                                    <p>تراکنشی یافت نشد</p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>

            <!-- وام‌های فعال -->
            <div class="col-lg-6">
                <div class="content-card">
                    <div class="content-card-header">
                        <h5>
                            <i class="fas fa-money-bill-wave"></i>
                            وام‌های فعال
                        </h5>
                    </div>
                    <div class="content-card-body">
                        <c:choose>
                            <c:when test="${not empty activeLoans}">
                                <table class="modern-table">
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
                                            <td><strong><c:out value="${loan.loanNumber}" /></strong></td>
                                            <td>
                                                <fmt:formatNumber value="${loan.principal}" pattern="#,###" /> ریال
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${loan.status == 'ACTIVE'}">
                                                        <span class="status-badge active">
                                                            <i class="fas fa-check"></i>
                                                            فعال
                                                        </span>
                                                    </c:when>
                                                    <c:when test="${loan.status == 'APPROVED'}">
                                                        <span class="status-badge approved">
                                                            <i class="fas fa-thumbs-up"></i>
                                                            تایید شده
                                                        </span>
                                                    </c:when>
                                                </c:choose>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </c:when>
                            <c:otherwise>
                                <div class="empty-state">
                                    <i class="fas fa-hand-holding-usd"></i>
                                    <p>وام فعالی وجود ندارد</p>
                                </div>
                            </c:otherwise>
                        </c:choose>
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
    // چارت خلاصه مالی با طراحی بهتر
    const financialCtx = document.getElementById('financialChart').getContext('2d');
    new Chart(financialCtx, {
        type: 'doughnut',
        data: {
            labels: ['موجودی', 'بدهی وام‌ها'],
            datasets: [{
                data: [${totalBalance}, ${totalLoanDebt}],
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
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        padding: 20,
                        font: {
                            size: 14,
                            family: 'Vazirmatn'
                        }
                    }
                },
                tooltip: {
                    backgroundColor: 'rgba(0, 0, 0, 0.8)',
                    padding: 12,
                    titleFont: {
                        size: 14,
                        family: 'Vazirmatn'
                    },
                    bodyFont: {
                        size: 13,
                        family: 'Vazirmatn'
                    },
                    callbacks: {
                        label: function(context) {
                            let label = context.label || '';
                            if (label) {
                                label += ': ';
                            }
                            label += new Intl.NumberFormat('fa-IR').format(context.parsed) + ' ریال';
                            return label;
                        }
                    }
                }
            },
            cutout: '70%'
        }
    });

    // انیمیشن برای نمودار
    setTimeout(() => {
        financialCtx.canvas.style.opacity = '1';
    }, 500);
</script>
</body>
</html>
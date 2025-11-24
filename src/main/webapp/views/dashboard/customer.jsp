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
    @import url('https://fonts.googleapis.com/css2?family=Vazirmatn:wght@300;400;500;600;700;800;900&display=swap');

    * {
        font-family: 'Vazirmatn', sans-serif;
    }

    /* ==================== Animations ==================== */
    @keyframes fadeInUp {
        from {
            opacity: 0;
            transform: translateY(40px);
        }
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }

    @keyframes slideInRight {
        from {
            opacity: 0;
            transform: translateX(-40px);
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

    @keyframes float {
        0%, 100% {
            transform: translateY(0) rotate(0deg);
        }
        50% {
            transform: translateY(-15px) rotate(5deg);
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

    @keyframes glow {
        0%, 100% {
            box-shadow: 0 0 20px rgba(102, 126, 234, 0.4);
        }
        50% {
            box-shadow: 0 0 40px rgba(102, 126, 234, 0.8);
        }
    }

    /* ==================== Background ==================== */
    .content-wrapper {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
        min-height: 100vh;
        position: relative;
        overflow-x: hidden;
    }

    .content-wrapper::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background:
                radial-gradient(circle at 20% 50%, rgba(255, 255, 255, 0.1) 0%, transparent 50%),
                radial-gradient(circle at 80% 80%, rgba(255, 255, 255, 0.1) 0%, transparent 50%);
        pointer-events: none;
    }

    /* ==================== Welcome Header ==================== */
    .welcome-header {
        animation: fadeInUp 0.8s cubic-bezier(0.4, 0, 0.2, 1);
        background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(255, 255, 255, 0.85) 100%);
        backdrop-filter: blur(20px);
        color: #1a202c;
        padding: 2.5rem;
        border-radius: 25px;
        margin-bottom: 2rem;
        box-shadow:
                0 20px 60px rgba(0, 0, 0, 0.15),
                0 0 0 1px rgba(255, 255, 255, 0.5) inset;
        border: 1px solid rgba(255, 255, 255, 0.3);
        position: relative;
        overflow: hidden;
    }

    .welcome-header::before {
        content: '';
        position: absolute;
        top: -50%;
        right: -50%;
        width: 200%;
        height: 200%;
        background: linear-gradient(
                45deg,
                transparent 30%,
                rgba(255, 255, 255, 0.3) 50%,
                transparent 70%
        );
        animation: shimmer 3s infinite;
    }

    .welcome-header h1 {
        font-size: 2.8rem;
        font-weight: 900;
        margin-bottom: 0.5rem;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
        position: relative;
        z-index: 1;
    }

    .welcome-header p {
        color: #4a5568;
        font-size: 1.2rem;
        font-weight: 500;
        position: relative;
        z-index: 1;
    }

    .welcome-header .floating-icon {
        animation: float 3s ease-in-out infinite;
        display: inline-block;
        margin-left: 0.5rem;
    }

    .current-time {
        position: absolute;
        top: 2rem;
        left: 2rem;
        background: rgba(102, 126, 234, 0.1);
        padding: 0.5rem 1rem;
        border-radius: 50px;
        font-size: 0.9rem;
        color: #667eea;
        font-weight: 600;
    }

    /* ==================== Quick Actions ==================== */
    .quick-actions {
        display: flex;
        gap: 1rem;
        margin-top: 1.5rem;
        flex-wrap: wrap;
    }

    .quick-action-btn {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        border: none;
        padding: 0.8rem 1.5rem;
        border-radius: 50px;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.3s ease;
        box-shadow: 0 5px 15px rgba(102, 126, 234, 0.3);
    }

    .quick-action-btn:hover {
        transform: translateY(-3px);
        box-shadow: 0 10px 25px rgba(102, 126, 234, 0.5);
    }

    .quick-action-btn i {
        margin-left: 0.5rem;
    }

    /* ==================== Stat Cards ==================== */
    .stat-card {
        background: rgba(255, 255, 255, 0.95);
        backdrop-filter: blur(20px);
        border-radius: 25px;
        padding: 2rem;
        box-shadow:
                0 15px 40px rgba(0, 0, 0, 0.1),
                0 0 0 1px rgba(255, 255, 255, 0.5) inset;
        transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
        animation: fadeInUp 0.6s ease-out backwards;
        position: relative;
        overflow: hidden;
        border: 1px solid rgba(255, 255, 255, 0.3);
    }

    .stat-card::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 6px;
        background: linear-gradient(90deg, var(--card-color-1), var(--card-color-2));
    }

    .stat-card::after {
        content: '';
        position: absolute;
        top: -50%;
        right: -50%;
        width: 200%;
        height: 200%;
        background: radial-gradient(circle, rgba(255, 255, 255, 0.3) 0%, transparent 70%);
        opacity: 0;
        transition: opacity 0.4s;
    }

    .stat-card:hover {
        transform: translateY(-15px) scale(1.02);
        box-shadow:
                0 25px 60px rgba(0, 0, 0, 0.2),
                0 0 0 1px rgba(255, 255, 255, 0.8) inset;
    }

    .stat-card:hover::after {
        opacity: 1;
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
        width: 80px;
        height: 80px;
        border-radius: 20px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 2.2rem;
        margin-bottom: 1.5rem;
        background: linear-gradient(135deg, var(--card-color-1), var(--card-color-2));
        color: white;
        box-shadow:
                0 15px 35px rgba(0, 0, 0, 0.15),
                0 0 20px var(--card-color-1);
        transition: all 0.3s ease;
        position: relative;
        z-index: 1;
    }

    .stat-card:hover .stat-card-icon {
        transform: rotate(10deg) scale(1.1);
        animation: pulse 1s infinite;
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
        font-size: 0.85rem;
        color: #6b7280;
        font-weight: 700;
        text-transform: uppercase;
        letter-spacing: 1.5px;
        margin-bottom: 0.8rem;
    }

    .stat-card-value {
        font-size: 2.2rem;
        font-weight: 900;
        background: linear-gradient(135deg, #1f2937 0%, #374151 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
        margin-bottom: 0.8rem;
        line-height: 1.2;
    }

    .stat-card-subtitle {
        font-size: 0.95rem;
        color: #9ca3af;
        font-weight: 600;
        display: flex;
        align-items: center;
        gap: 0.5rem;
    }

    .stat-card-subtitle i {
        color: var(--card-color-1);
    }

    .stat-trend {
        position: absolute;
        top: 1.5rem;
        left: 1.5rem;
        background: rgba(16, 185, 129, 0.1);
        color: #10b981;
        padding: 0.3rem 0.8rem;
        border-radius: 50px;
        font-size: 0.75rem;
        font-weight: 700;
        display: flex;
        align-items: center;
        gap: 0.3rem;
    }

    .stat-trend.down {
        background: rgba(239, 68, 68, 0.1);
        color: #ef4444;
    }

    /* ==================== Notification Banner ==================== */
    .notification-banner {
        background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
        border: 2px solid #fbbf24;
        border-radius: 20px;
        padding: 1.5rem 2rem;
        margin-bottom: 2rem;
        box-shadow:
                0 10px 30px rgba(251, 191, 36, 0.3),
                0 0 0 1px rgba(255, 255, 255, 0.5) inset;
        animation: slideInRight 0.6s ease-out;
        position: relative;
        overflow: hidden;
    }

    .notification-banner::before {
        content: '';
        position: absolute;
        top: 0;
        left: -100%;
        width: 100%;
        height: 100%;
        background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.5), transparent);
        animation: shimmer 2s infinite;
    }

    .notification-banner i {
        font-size: 1.8rem;
        color: #d97706;
        animation: pulse 2s infinite;
    }

    /* ==================== Financial Summary ==================== */
    .financial-summary {
        background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(255, 255, 255, 0.85) 100%);
        backdrop-filter: blur(20px);
        border-radius: 25px;
        padding: 2.5rem;
        margin-bottom: 2rem;
        box-shadow:
                0 20px 60px rgba(0, 0, 0, 0.15),
                0 0 0 1px rgba(255, 255, 255, 0.5) inset;
        animation: fadeInUp 0.6s ease-out;
        animation-delay: 0.3s;
        animation-fill-mode: backwards;
        position: relative;
        overflow: hidden;
        border: 1px solid rgba(255, 255, 255, 0.3);
    }

    .financial-summary::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: radial-gradient(circle at top right, rgba(102, 126, 234, 0.1), transparent);
        pointer-events: none;
    }

    .financial-summary h4 {
        font-size: 1.6rem;
        margin-bottom: 1.5rem;
        color: #1f2937;
        font-weight: 800;
        display: flex;
        align-items: center;
        gap: 1rem;
    }

    .financial-summary h4 i {
        width: 50px;
        height: 50px;
        background: linear-gradient(135deg, #667eea, #764ba2);
        border-radius: 15px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        font-size: 1.5rem;
    }

    .financial-summary .net-worth {
        font-size: 3.5rem;
        font-weight: 900;
        margin-bottom: 1.5rem;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
    }

    .progress-bar-custom {
        height: 12px;
        border-radius: 50px;
        background: rgba(229, 231, 235, 0.5);
        overflow: hidden;
        position: relative;
        box-shadow: 0 0 20px rgba(0, 0, 0, 0.05) inset;
    }

    .progress-fill {
        height: 100%;
        background: linear-gradient(90deg, #10b981, #059669);
        border-radius: 50px;
        transition: width 1.5s cubic-bezier(0.4, 0, 0.2, 1);
        box-shadow: 0 0 20px rgba(16, 185, 129, 0.5);
        position: relative;
        overflow: hidden;
    }

    .progress-fill::after {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
        animation: shimmer 2s infinite;
    }

    .financial-info {
        display: flex;
        justify-content: space-between;
        margin-top: 1.5rem;
        gap: 2rem;
    }

    .financial-info-item {
        flex: 1;
        background: rgba(102, 126, 234, 0.05);
        padding: 1rem;
        border-radius: 15px;
        text-align: center;
    }

    .financial-info-item small {
        display: block;
        color: #6b7280;
        font-weight: 600;
        margin-bottom: 0.5rem;
        font-size: 0.85rem;
    }

    .financial-info-item strong {
        font-size: 1.3rem;
        color: #1f2937;
        font-weight: 800;
    }

    /* ==================== Content Cards ==================== */
    .content-card {
        background: rgba(255, 255, 255, 0.95);
        backdrop-filter: blur(20px);
        border-radius: 25px;
        box-shadow:
                0 15px 40px rgba(0, 0, 0, 0.1),
                0 0 0 1px rgba(255, 255, 255, 0.5) inset;
        overflow: hidden;
        animation: fadeInUp 0.6s ease-out backwards;
        animation-delay: 0.4s;
        border: 1px solid rgba(255, 255, 255, 0.3);
        transition: all 0.3s ease;
    }

    .content-card:hover {
        transform: translateY(-5px);
        box-shadow:
                0 20px 50px rgba(0, 0, 0, 0.15),
                0 0 0 1px rgba(255, 255, 255, 0.8) inset;
    }

    .content-card-header {
        background: linear-gradient(135deg, #f9fafb 0%, #f3f4f6 100%);
        padding: 1.8rem;
        border-bottom: 2px solid rgba(229, 231, 235, 0.5);
    }

    .content-card-header h5 {
        margin: 0;
        font-weight: 800;
        color: #1f2937;
        display: flex;
        align-items: center;
        gap: 1rem;
        font-size: 1.3rem;
    }

    .content-card-header i {
        width: 45px;
        height: 45px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        background: linear-gradient(135deg, #667eea, #764ba2);
        color: white;
        font-size: 1.2rem;
        box-shadow: 0 5px 15px rgba(102, 126, 234, 0.3);
    }

    .content-card-body {
        padding: 2rem;
    }

    /* ==================== Modern Table ==================== */
    .modern-table {
        width: 100%;
        border-collapse: separate;
        border-spacing: 0 0.8rem;
    }

    .modern-table thead th {
        background: linear-gradient(135deg, #f9fafb, #f3f4f6);
        color: #6b7280;
        font-weight: 700;
        text-transform: uppercase;
        font-size: 0.75rem;
        letter-spacing: 1px;
        padding: 1.2rem 1rem;
        border: none;
        position: sticky;
        top: 0;
        z-index: 10;
    }

    .modern-table thead th:first-child {
        border-radius: 12px 0 0 12px;
    }

    .modern-table thead th:last-child {
        border-radius: 0 12px 12px 0;
    }

    .modern-table tbody tr {
        transition: all 0.3s ease;
        background: white;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
        border-radius: 12px;
    }

    .modern-table tbody tr:hover {
        background: linear-gradient(135deg, #f9fafb, #ffffff);
        transform: scale(1.02);
        box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
    }

    .modern-table tbody td {
        padding: 1.2rem 1rem;
        border: none;
        color: #374151;
        font-weight: 500;
    }

    .modern-table tbody td:first-child {
        border-radius: 12px 0 0 12px;
    }

    .modern-table tbody td:last-child {
        border-radius: 0 12px 12px 0;
    }

    /* ==================== Status Badges ==================== */
    .status-badge {
        padding: 0.5rem 1rem;
        border-radius: 50px;
        font-size: 0.8rem;
        font-weight: 700;
        display: inline-flex;
        align-items: center;
        gap: 0.4rem;
        box-shadow: 0 3px 10px rgba(0, 0, 0, 0.1);
        transition: all 0.3s ease;
    }

    .status-badge:hover {
        transform: scale(1.05);
    }

    .status-badge.active {
        background: linear-gradient(135deg, #d1fae5, #a7f3d0);
        color: #065f46;
    }

    .status-badge.approved {
        background: linear-gradient(135deg, #dbeafe, #bfdbfe);
        color: #1e40af;
    }

    .status-badge.pending {
        background: linear-gradient(135deg, #fef3c7, #fde68a);
        color: #92400e;
    }

    /* ==================== Chart Container ==================== */
    .chart-container {
        position: relative;
        padding: 2.5rem;
        background: linear-gradient(135deg, #f9fafb 0%, #ffffff 100%);
        border-radius: 20px;
        margin-top: 1rem;
        box-shadow: 0 5px 20px rgba(0, 0, 0, 0.05) inset;
    }

    /* ==================== Empty State ==================== */
    .empty-state {
        text-align: center;
        padding: 4rem 2rem;
        color: #9ca3af;
    }

    .empty-state i {
        font-size: 5rem;
        margin-bottom: 1.5rem;
        opacity: 0.2;
        background: linear-gradient(135deg, #667eea, #764ba2);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
    }

    .empty-state p {
        font-size: 1.1rem;
        font-weight: 600;
    }

    /* ==================== Floating Particles ==================== */
    .particle {
        position: absolute;
        border-radius: 50%;
        pointer-events: none;
    }

    .particle-1 {
        width: 100px;
        height: 100px;
        background: radial-gradient(circle, rgba(102, 126, 234, 0.1), transparent);
        top: 10%;
        left: 5%;
        animation: float 6s ease-in-out infinite;
    }

    .particle-2 {
        width: 150px;
        height: 150px;
        background: radial-gradient(circle, rgba(118, 75, 162, 0.1), transparent);
        top: 60%;
        right: 10%;
        animation: float 8s ease-in-out infinite reverse;
    }

    .particle-3 {
        width: 80px;
        height: 80px;
        background: radial-gradient(circle, rgba(240, 147, 251, 0.1), transparent);
        bottom: 20%;
        left: 15%;
        animation: float 7s ease-in-out infinite;
    }

    /* ==================== Responsive Design ==================== */
    @media (max-width: 768px) {
        .welcome-header h1 {
            font-size: 2rem;
        }

        .stat-card-value {
            font-size: 1.8rem;
        }

        .financial-summary .net-worth {
            font-size: 2.5rem;
        }

        .current-time {
            position: relative;
            top: auto;
            left: auto;
            display: inline-block;
            margin-top: 1rem;
        }

        .financial-info {
            flex-direction: column;
            gap: 1rem;
        }

        .quick-actions {
            justify-content: center;
        }
    }

    /* ==================== Scrollbar Styling ==================== */
    ::-webkit-scrollbar {
        width: 10px;
    }

    ::-webkit-scrollbar-track {
        background: rgba(255, 255, 255, 0.1);
        border-radius: 10px;
    }

    ::-webkit-scrollbar-thumb {
        background: linear-gradient(135deg, #667eea, #764ba2);
        border-radius: 10px;
    }

    ::-webkit-scrollbar-thumb:hover {
        background: linear-gradient(135deg, #764ba2, #667eea);
    }
</style>

<div class="content-wrapper">
    <!-- Floating Particles -->
    <div class="particle particle-1"></div>
    <div class="particle particle-2"></div>
    <div class="particle particle-3"></div>

    <div class="container-fluid px-4 py-4">
        <!-- Welcome Header -->
        <div class="welcome-header">
            <div class="current-time">
                <i class="fas fa-clock me-1"></i>
                <span id="currentTime"></span>
            </div>
            <h1>
                <i class="fas fa-hand-sparkles floating-icon"></i>
                سلام، <c:out value="${fullName}" />!
            </h1>
            <p>
                <i class="fas fa-sun me-2"></i>
                امیدواریم روز شگفت‌انگیزی داشته باشید. اینجا نمای کلی وضعیت مالی شماست.
            </p>

            <!-- Quick Actions -->
            <div class="quick-actions">
                <button class="quick-action-btn" onclick="location.href='${pageContext.request.contextPath}/customer/transfer'">
                    <i class="fas fa-paper-plane"></i>
                    انتقال وجه
                </button>
                <button class="quick-action-btn" onclick="location.href='${pageContext.request.contextPath}/customer/loan/apply'">
                    <i class="fas fa-hand-holding-usd"></i>
                    درخواست وام
                </button>
                <button class="quick-action-btn" onclick="location.href='${pageContext.request.contextPath}/customer/transactions'">
                    <i class="fas fa-receipt"></i>
                    تاریخچه تراکنش
                </button>
            </div>
        </div>

        <!-- Notifications -->
        <c:if test="${not empty notifications}">
            <div class="notification-banner alert alert-dismissible fade show" role="alert">
                <i class="fas fa-bell"></i>
                <strong class="ms-2">اعلان‌های مهم:</strong>
                <c:out value="${notifications}" />
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <!-- Stat Cards -->
        <div class="row g-4 mb-4">
            <!-- Total Balance -->
            <div class="col-md-4">
                <div class="stat-card balance">
                    <span class="stat-trend">
                        <i class="fas fa-arrow-up"></i>
                        +12.5%
                    </span>
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

            <!-- Transactions -->
            <div class="col-md-4">
                <div class="stat-card transactions">
                    <span class="stat-trend">
                        <i class="fas fa-arrow-up"></i>
                        +8.2%
                    </span>
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

            <!-- Loans -->
            <div class="col-md-4">
                <div class="stat-card loans">
                    <span class="stat-trend down">
                        <i class="fas fa-arrow-down"></i>
                        -5.1%
                    </span>
                    <div class="stat-card-icon">
                        <i class="fas fa-hand-holding-usd"></i>
                    </div>
                    <div class="stat-card-title">بدهی وام‌ها</div>
                    <div class="stat-card-value">
                        <fmt:formatNumber value="${totalLoanDebt}" pattern="#,###" /> ریال
                    </div>
                    <div class="stat-card-subtitle">
                        <i class="fas fa-list"></i>
                        <c:choose>
                            <c:when test="${not empty activeLoans}">
                                ${activeLoans.size()} فعال |
                                <c:if test="${not empty pendingLoans}">
                                    ${pendingLoans.size()} در انتظار
                                </c:if>
                                <c:if test="${empty pendingLoans}">
                                    0 در انتظار
                                </c:if>
                            </c:when>
                            <c:otherwise>
                                0 فعال | 0 در انتظار
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>

        <!-- Financial Summary -->
        <div class="financial-summary">
            <h4>
                <i class="fas fa-chart-line"></i>
                خالص دارایی شما
            </h4>
            <div class="net-worth">
                <i class="fas fa-coins me-2"></i>
                <fmt:formatNumber value="${netWorth}" pattern="#,###" /> ریال
            </div>
            <div class="progress-bar-custom">
                <div class="progress-fill" style="width: ${totalBalance.doubleValue() / (totalBalance.doubleValue() + totalLoanDebt.doubleValue()) * 100}%"></div>
            </div>
            <div class="financial-info">
                <div class="financial-info-item">
                    <small>نسبت دارایی به بدهی</small>
                    <strong>
                        ${String.format("%.1f", totalBalance.doubleValue() / (totalLoanDebt.doubleValue() > 0 ? totalLoanDebt.doubleValue() : 1))}x
                    </strong>
                </div>
                <div class="financial-info-item">
                    <small>درصد موجودی</small>
                    <strong>
                        ${String.format("%.1f", totalBalance.doubleValue() / (totalBalance.doubleValue() + totalLoanDebt.doubleValue()) * 100)}%
                    </strong>
                </div>
                <div class="financial-info-item">
                    <small>امتیاز مالی</small>
                    <strong>
                        <i class="fas fa-star text-warning"></i>
                        عالی
                    </strong>
                </div>
            </div>
        </div>

        <!-- Financial Chart -->
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

        <!-- Recent Lists -->
        <div class="row g-4">
            <!-- Recent Transactions -->
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
                                                <strong>
                                                    <i class="fas fa-coins text-warning me-1"></i>
                                                    <fmt:formatNumber value="${tx.amount}" pattern="#,###" />
                                                </strong> ریال
                                            </td>
                                            <td>
                                                <small>
                                                    <i class="fas fa-calendar-alt me-1"></i>
                                                        ${tx.transactionDate.toLocalDate()}
                                                </small>
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
                                    <button class="quick-action-btn mt-3" onclick="location.href='${pageContext.request.contextPath}/customer/transfer'">
                                        <i class="fas fa-plus"></i>
                                        ایجاد تراکنش جدید
                                    </button>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>

            <!-- Active Loans -->
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
                                            <td>
                                                <strong>
                                                    <i class="fas fa-hashtag me-1"></i>
                                                    <c:out value="${loan.loanNumber}" />
                                                </strong>
                                            </td>
                                            <td>
                                                <strong>
                                                    <i class="fas fa-coins text-warning me-1"></i>
                                                    <fmt:formatNumber value="${loan.principal}" pattern="#,###" />
                                                </strong> ریال
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
                                    <button class="quick-action-btn mt-3" onclick="location.href='${pageContext.request.contextPath}/customer/loan/apply'">
                                        <i class="fas fa-plus"></i>
                                        درخواست وام جدید
                                    </button>
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

<!-- Chart.js -->
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<script>
    // Current Time Display
    function updateTime() {
        const now = new Date();
        const timeString = now.toLocaleTimeString('fa-IR', {
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit'
        });
        const dateString = now.toLocaleDateString('fa-IR', {
            weekday: 'long',
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
        document.getElementById('currentTime').textContent = timeString + ' - ' + dateString;
    }

    updateTime();
    setInterval(updateTime, 1000);

    // Enhanced Financial Chart
    const financialCtx = document.getElementById('financialChart');
    if (financialCtx) {
        const gradient1 = financialCtx.getContext('2d').createLinearGradient(0, 0, 0, 400);
        gradient1.addColorStop(0, 'rgba(16, 185, 129, 0.9)');
        gradient1.addColorStop(1, 'rgba(5, 150, 105, 0.9)');

        const gradient2 = financialCtx.getContext('2d').createLinearGradient(0, 0, 0, 400);
        gradient2.addColorStop(0, 'rgba(239, 68, 68, 0.9)');
        gradient2.addColorStop(1, 'rgba(220, 38, 38, 0.9)');

        new Chart(financialCtx, {
            type: 'doughnut',
            data: {
                labels: ['موجودی', 'بدهی وام‌ها'],
                datasets: [{
                    data: [${totalBalance}, ${totalLoanDebt}],
                    backgroundColor: [gradient1, gradient2],
                    borderWidth: 5,
                    borderColor: '#fff',
                    hoverOffset: 30,
                    hoverBorderWidth: 8
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: {
                            padding: 25,
                            font: {
                                size: 15,
                                family: 'Vazirmatn',
                                weight: '700'
                            },
                            usePointStyle: true,
                            pointStyle: 'circle'
                        }
                    },
                    tooltip: {
                        backgroundColor: 'rgba(0, 0, 0, 0.9)',
                        padding: 16,
                        cornerRadius: 12,
                        titleFont: {
                            size: 16,
                            family: 'Vazirmatn',
                            weight: '700'
                        },
                        bodyFont: {
                            size: 14,
                            family: 'Vazirmatn',
                            weight: '600'
                        },
                        callbacks: {
                            label: function(context) {
                                let label = context.label || '';
                                if (label) {
                                    label += ': ';
                                }
                                label += new Intl.NumberFormat('fa-IR').format(context.parsed) + ' ریال';

                                const total = context.dataset.data.reduce((a, b) => a + b, 0);
                                const percentage = ((context.parsed / total) * 100).toFixed(1);
                                label += ' (' + percentage + '%)';

                                return label;
                            }
                        }
                    }
                },
                cutout: '75%',
                animation: {
                    animateRotate: true,
                    animateScale: true,
                    duration: 2000,
                    easing: 'easeInOutQuart'
                }
            }
        });
    }

    // Animate progress bar on load
    window.addEventListener('load', function() {
        const progressFill = document.querySelector('.progress-fill');
        if (progressFill) {
            const targetWidth = progressFill.style.width;
            progressFill.style.width = '0%';
            setTimeout(() => {
                progressFill.style.width = targetWidth;
            }, 500);
        }
    });

    // Add hover effect to table rows
    document.querySelectorAll('.modern-table tbody tr').forEach(row => {
        row.addEventListener('mouseenter', function() {
            this.style.cursor = 'pointer';
        });
    });

    // Smooth scroll for quick actions
    document.querySelectorAll('.quick-action-btn').forEach(button => {
        button.addEventListener('click', function(e) {
            this.style.transform = 'scale(0.95)';
            setTimeout(() => {
                this.style.transform = '';
            }, 150);
        });
    });
</script>
</body>
</html>
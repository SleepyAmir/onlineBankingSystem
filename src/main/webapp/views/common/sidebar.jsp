<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
    @keyframes slideInRight {
        from {
            opacity: 0;
            transform: translateX(20px);
        }
        to {
            opacity: 1;
            transform: translateX(0);
        }
    }

    .sidebar {
        position: fixed;
        top: var(--navbar-height);
        right: 0;
        width: var(--sidebar-width);
        height: calc(100vh - var(--navbar-height));
        background: white;
        box-shadow: -5px 0 30px rgba(0, 0, 0, 0.08);
        overflow-y: auto;
        overflow-x: hidden;
        z-index: 999;
        transition: all 0.3s ease;
        animation: slideInRight 0.5s ease-out;
    }

    .sidebar::-webkit-scrollbar {
        width: 6px;
    }

    .sidebar::-webkit-scrollbar-track {
        background: #f9fafb;
    }

    .sidebar::-webkit-scrollbar-thumb {
        background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
        border-radius: 10px;
    }

    .sidebar.collapsed {
        transform: translateX(100%);
    }

    .sidebar-menu {
        list-style: none;
        padding: 1.5rem 0;
        margin: 0;
    }

    .menu-section-title {
        padding: 1.75rem 1.5rem 0.75rem;
        font-size: 0.7rem;
        font-weight: 700;
        color: #9ca3af;
        text-transform: uppercase;
        letter-spacing: 1.5px;
        position: relative;
    }

    .menu-section-title::after {
        content: '';
        position: absolute;
        bottom: 0.5rem;
        right: 1.5rem;
        left: 1.5rem;
        height: 1px;
        background: linear-gradient(90deg, transparent, #e5e7eb, transparent);
    }

    .menu-item {
        margin: 0.4rem 1rem;
        animation: slideInRight 0.5s ease-out backwards;
    }

    .menu-item:nth-child(1) { animation-delay: 0.05s; }
    .menu-item:nth-child(2) { animation-delay: 0.1s; }
    .menu-item:nth-child(3) { animation-delay: 0.15s; }
    .menu-item:nth-child(4) { animation-delay: 0.2s; }
    .menu-item:nth-child(5) { animation-delay: 0.25s; }
    .menu-item:nth-child(6) { animation-delay: 0.3s; }
    .menu-item:nth-child(7) { animation-delay: 0.35s; }

    .menu-link {
        display: flex;
        align-items: center;
        gap: 1rem;
        padding: 1rem 1.25rem;
        color: var(--dark-color);
        text-decoration: none;
        border-radius: 15px;
        transition: all 0.3s ease;
        font-weight: 500;
        font-size: 0.95rem;
        position: relative;
        overflow: hidden;
    }

    .menu-link::before {
        content: '';
        position: absolute;
        right: 0;
        top: 0;
        bottom: 0;
        width: 0;
        background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
        transition: all 0.3s ease;
        border-radius: 15px;
    }

    .menu-link:hover {
        color: white;
        transform: translateX(-8px);
        box-shadow: 0 5px 20px rgba(102, 126, 234, 0.25);
    }

    .menu-link:hover::before {
        width: 100%;
    }

    .menu-link.active {
        background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
        color: white;
        box-shadow: 0 8px 25px rgba(102, 126, 234, 0.35);
        transform: translateX(-5px);
    }

    .menu-link.active::before {
        width: 0;
    }

    .menu-icon {
        width: 40px;
        height: 40px;
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 1.1rem;
        background: #f3f4f6;
        transition: all 0.3s ease;
        position: relative;
        z-index: 1;
        flex-shrink: 0;
    }

    .menu-link:hover .menu-icon,
    .menu-link.active .menu-icon {
        background: rgba(255, 255, 255, 0.2);
        transform: rotate(-10deg) scale(1.1);
    }

    .menu-text {
        position: relative;
        z-index: 1;
        flex: 1;
    }

    .menu-badge {
        margin-right: auto;
        padding: 0.35rem 0.65rem;
        background: linear-gradient(135deg, #ef4444, #dc2626);
        color: white;
        border-radius: 50px;
        font-size: 0.7rem;
        font-weight: 700;
        min-width: 24px;
        text-align: center;
        position: relative;
        z-index: 1;
        box-shadow: 0 3px 10px rgba(239, 68, 68, 0.3);
    }

    .menu-link:hover .menu-badge,
    .menu-link.active .menu-badge {
        background: white;
        color: var(--danger-color);
    }

    /* افکت برای آیتم‌های خاص */
    .menu-item-featured .menu-link {
        background: linear-gradient(135deg, #f0f9ff, #e0f2fe);
        border: 2px solid #bfdbfe;
    }

    .menu-item-featured .menu-icon {
        background: #3b82f6;
        color: white;
    }

    @media (max-width: 768px) {
        .sidebar {
            position: fixed;
            top: 0;
            right: 0;
            width: 300px;
            height: 100vh;
            transform: translateX(100%);
            transition: transform 0.3s ease;
            box-shadow: -10px 0 40px rgba(0, 0, 0, 0.2);
        }

        .sidebar.show {
            transform: translateX(0);
        }

        /* Overlay برای موبایل */
        .sidebar-overlay {
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: rgba(0, 0, 0, 0.5);
            opacity: 0;
            visibility: hidden;
            transition: all 0.3s ease;
            z-index: 998;
        }

        .sidebar.show ~ .sidebar-overlay {
            opacity: 1;
            visibility: visible;
        }
    }
</style>

<aside class="sidebar" id="sidebar">
    <ul class="sidebar-menu">
        <!-- Dashboard Section -->
        <li class="menu-section-title">
            <i class="fas fa-grip-horizontal me-2"></i>
            صفحات اصلی
        </li>
        <li class="menu-item">
            <a href="${pageContext.request.contextPath}/customer/user-dashboard"
               class="menu-link ${requestURI.contains('/user-dashboard') ? 'active' : ''}">
                <div class="menu-icon">
                    <i class="fas fa-tachometer-alt"></i>
                </div>
                <span class="menu-text">داشبورد</span>
            </a>
        </li>

        <!-- Banking Operations -->
        <li class="menu-section-title">
            <i class="fas fa-money-bill-wave me-2"></i>
            عملیات بانکی
        </li>
        <li class="menu-item">
            <a href="${pageContext.request.contextPath}/accounts/list"
               class="menu-link ${requestURI.contains('/accounts') ? 'active' : ''}">
                <div class="menu-icon">
                    <i class="fas fa-wallet"></i>
                </div>
                <span class="menu-text">حساب‌های من</span>
            </a>
        </li>
        <li class="menu-item menu-item-featured">
            <a href="${pageContext.request.contextPath}/transactions"
               class="menu-link ${requestURI.contains('/transactions/form') ? 'active' : ''}">
                <div class="menu-icon">
                    <i class="fas fa-exchange-alt"></i>
                </div>
                <span class="menu-text">انتقال وجه</span>
            </a>
        </li>
        <li class="menu-item">
            <a href="${pageContext.request.contextPath}/transactions/history"
               class="menu-link ${requestURI.contains('/transactions/history') ? 'active' : ''}">
                <div class="menu-icon">
                    <i class="fas fa-history"></i>
                </div>
                <span class="menu-text">تاریخچه تراکنش</span>
            </a>
        </li>
        <li class="menu-item">
            <a href="${pageContext.request.contextPath}/loans/apply"
               class="menu-link ${requestURI.contains('/loans/apply') ? 'active' : ''}">
                <div class="menu-icon">
                    <i class="fas fa-hand-holding-usd"></i>
                </div>
                <span class="menu-text">درخواست وام</span>
            </a>
        </li>
        <li class="menu-item">
            <a href="${pageContext.request.contextPath}/loans/list"
               class="menu-link ${requestURI.contains('/loans/list') ? 'active' : ''}">
                <div class="menu-icon">
                    <i class="fas fa-list"></i>
                </div>
                <span class="menu-text">وام‌های من</span>
            </a>
        </li>
        <li class="menu-item">
            <a href="${pageContext.request.contextPath}/cards/list"
               class="menu-link ${requestURI.contains('/cards/list') ? 'active' : ''}">
                <div class="menu-icon">
                    <i class="fas fa-credit-card"></i>
                </div>
                <span class="menu-text">کارت‌های بانکی</span>
            </a>
        </li>

        <!-- Admin Section -->
        <c:if test="${sessionScope.roles.contains('ADMIN') || sessionScope.roles.contains('MANAGER')}">
            <li class="menu-section-title">
                <i class="fas fa-shield-alt me-2"></i>
                پنل مدیریت
            </li>
            <c:if test="${sessionScope.roles.contains('ADMIN')}">
                <li class="menu-item">
                    <a href="${pageContext.request.contextPath}/users/list"
                       class="menu-link ${requestURI.contains('/users/list') ? 'active' : ''}">
                        <div class="menu-icon">
                            <i class="fas fa-users"></i>
                        </div>
                        <span class="menu-text">مدیریت کاربران</span>
                    </a>
                </li>
            </c:if>
            <li class="menu-item">
                <a href="${pageContext.request.contextPath}/loans/list?status=PENDING"
                   class="menu-link ${requestURI.contains('/loans/list?status=PENDING') ? 'active' : ''}">
                    <div class="menu-icon">
                        <i class="fas fa-tasks"></i>
                    </div>
                    <span class="menu-text">تأیید وام‌ها</span>
                    <c:if test="${pendingLoansCount > 0}">
                        <span class="menu-badge">${pendingLoansCount}</span>
                    </c:if>
                </a>
            </li>
        </c:if>

        <!-- Settings Section -->
        <li class="menu-section-title">
            <i class="fas fa-sliders-h me-2"></i>
            تنظیمات و راهنما
        </li>
        <li class="menu-item">
            <a href="${pageContext.request.contextPath}/user-profile"
               class="menu-link ${requestURI.contains('/user-profile') ? 'active' : ''}">
                <div class="menu-icon">
                    <i class="fas fa-user"></i>
                </div>
                <span class="menu-text">پروفایل کاربری</span>
            </a>
        </li>
        <li class="menu-item">
            <a href="${pageContext.request.contextPath}/settings"
               class="menu-link ${requestURI.contains('/settings') ? 'active' : ''}">
                <div class="menu-icon">
                    <i class="fas fa-cog"></i>
                </div>
                <span class="menu-text">تنظیمات</span>
            </a>
        </li>
        <li class="menu-item">
            <a href="${pageContext.request.contextPath}/help"
               class="menu-link ${requestURI.contains('/help') ? 'active' : ''}">
                <div class="menu-icon">
                    <i class="fas fa-question-circle"></i>
                </div>
                <span class="menu-text">راهنما و پشتیبانی</span>
            </a>
        </li>
    </ul>
</aside>

<div class="sidebar-overlay" onclick="toggleSidebar()"></div>

<script>
    function toggleSidebar() {
        const sidebar = document.getElementById('sidebar');
        sidebar.classList.toggle('show');
    }

    // بستن sidebar با کلید ESC
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            const sidebar = document.getElementById('sidebar');
            sidebar.classList.remove('show');
        }
    });
</script>
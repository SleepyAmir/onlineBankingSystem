<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
    .sidebar {
        position: fixed;
        top: var(--navbar-height);
        right: 0;
        width: var(--sidebar-width);
        height: calc(100vh - var(--navbar-height));
        background: white;
        box-shadow: -2px 0 10px rgba(0,0,0,0.1);
        overflow-y: auto;
        z-index: 999;
        transition: all 0.3s ease;
    }
    
    .sidebar.collapsed {
        transform: translateX(100%);
    }
    
    .sidebar-menu {
        list-style: none;
        padding: 1rem 0;
        margin: 0;
    }
    
    .menu-section-title {
        padding: 1.5rem 1.5rem 0.5rem;
        font-size: 0.75rem;
        font-weight: 600;
        color: #9ca3af;
        text-transform: uppercase;
        letter-spacing: 1px;
    }
    
    .menu-item {
        margin: 0.25rem 0.75rem;
    }
    
    .menu-link {
        display: flex;
        align-items: center;
        gap: 1rem;
        padding: 0.875rem 1rem;
        color: var(--dark-color);
        text-decoration: none;
        border-radius: 12px;
        transition: all 0.3s ease;
        font-weight: 500;
    }
    
    .menu-link:hover {
        background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
        color: white;
        transform: translateX(-5px);
    }
    
    .menu-link.active {
        background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
        color: white;
        box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
    }
    
    .menu-icon {
        width: 24px;
        text-align: center;
        font-size: 1.1rem;
    }
    
    .menu-badge {
        margin-right: auto;
        padding: 0.25rem 0.5rem;
        background: var(--danger-color);
        color: white;
        border-radius: 50px;
        font-size: 0.7rem;
        font-weight: 600;
    }
    
    @media (max-width: 768px) {
        .sidebar {
            transform: translateX(100%);
        }
        
        .sidebar.show {
            transform: translateX(0);
        }
    }
</style>

<aside class="sidebar" id="sidebar">
    <ul class="sidebar-menu">
        <!-- Dashboard Section -->
        <li class="menu-section-title">داشبورد</li>
        <li class="menu-item">
            <a href="${pageContext.request.contextPath}/dashboard" class="menu-link ${currentPage == 'dashboard' ? 'active' : ''}">
                <i class="fas fa-home menu-icon"></i>
                <span>خانه</span>
            </a>
        </li>
        
        <!-- Accounts Section -->
        <li class="menu-section-title">مدیریت مالی</li>
        <li class="menu-item">
            <a href="${pageContext.request.contextPath}/accounts/list" class="menu-link ${currentPage == 'accounts' ? 'active' : ''}">
                <i class="fas fa-wallet menu-icon"></i>
                <span>حساب‌های من</span>
            </a>
        </li>
        <li class="menu-item">
            <a href="${pageContext.request.contextPath}/transactions" class="menu-link ${currentPage == 'transactions' ? 'active' : ''}">
                <i class="fas fa-exchange-alt menu-icon"></i>
                <span>تراکنش‌ها</span>
            </a>
        </li>
        <li class="menu-item">
            <a href="${pageContext.request.contextPath}/transactions/history" class="menu-link">
                <i class="fas fa-history menu-icon"></i>
                <span>تاریخچه</span>
            </a>
        </li>
        
        <!-- Cards & Loans Section -->
        <li class="menu-section-title">خدمات بانکی</li>
        <li class="menu-item">
            <a href="${pageContext.request.contextPath}/cards/list" class="menu-link ${currentPage == 'cards' ? 'active' : ''}">
                <i class="fas fa-credit-card menu-icon"></i>
                <span>کارت‌های من</span>
            </a>
        </li>
        <li class="menu-item">
            <a href="${pageContext.request.contextPath}/loans/list" class="menu-link ${currentPage == 'loans' ? 'active' : ''}">
                <i class="fas fa-hand-holding-usd menu-icon"></i>
                <span>وام‌های من</span>
                <c:if test="${pendingLoansCount > 0}">
                    <span class="menu-badge">${pendingLoansCount}</span>
                </c:if>
            </a>
        </li>
        
        <!-- Admin Section (فقط برای Admin و Manager) -->
        <c:if test="${sessionScope.roles.contains('ADMIN') || sessionScope.roles.contains('MANAGER')}">
            <li class="menu-section-title">مدیریت</li>
            <c:if test="${sessionScope.roles.contains('ADMIN')}">
                <li class="menu-item">
                    <a href="${pageContext.request.contextPath}/users/list" class="menu-link ${currentPage == 'users' ? 'active' : ''}">
                        <i class="fas fa-users menu-icon"></i>
                        <span>کاربران</span>
                    </a>
                </li>
            </c:if>
            <c:if test="${sessionScope.roles.contains('MANAGER') || sessionScope.roles.contains('ADMIN')}">
                <li class="menu-item">
                    <a href="${pageContext.request.contextPath}/loans/list?status=PENDING" class="menu-link">
                        <i class="fas fa-tasks menu-icon"></i>
                        <span>تأیید وام‌ها</span>
                        <c:if test="${pendingLoansCount > 0}">
                            <span class="menu-badge">${pendingLoansCount}</span>
                        </c:if>
                    </a>
                </li>
            </c:if>
        </c:if>
        
        <!-- Settings Section -->
        <li class="menu-section-title">تنظیمات</li>
        <li class="menu-item">
            <a href="${pageContext.request.contextPath}/profile" class="menu-link ${currentPage == 'profile' ? 'active' : ''}">
                <i class="fas fa-user menu-icon"></i>
                <span>پروفایل</span>
            </a>
        </li>
        <li class="menu-item">
            <a href="${pageContext.request.contextPath}/settings" class="menu-link">
                <i class="fas fa-cog menu-icon"></i>
                <span>تنظیمات</span>
            </a>
        </li>
        <li class="menu-item">
            <a href="${pageContext.request.contextPath}/help" class="menu-link">
                <i class="fas fa-question-circle menu-icon"></i>
                <span>راهنما</span>
            </a>
        </li>
    </ul>
</aside>

<script>
    function toggleSidebar() {
        document.getElementById('sidebar').classList.toggle('show');
    }
</script>
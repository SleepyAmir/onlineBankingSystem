<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<style>
    .top-navbar {
        position: fixed;
        top: 0;
        right: 0;
        left: 0;
        height: var(--navbar-height);
        background: white;
        box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        z-index: 1000;
        display: flex;
        align-items: center;
        padding: 0 2rem;
        transition: all 0.3s ease;
    }
    
    .navbar-brand {
        display: flex;
        align-items: center;
        gap: 1rem;
        font-size: 1.5rem;
        font-weight: 700;
        color: var(--primary-color);
        text-decoration: none;
    }
    
    .navbar-brand i {
        font-size: 2rem;
        background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
    }
    
    .navbar-search {
        flex: 1;
        max-width: 500px;
        margin: 0 2rem;
    }
    
    .navbar-search input {
        width: 100%;
        padding: 0.75rem 1rem 0.75rem 3rem;
        border: 2px solid #e5e7eb;
        border-radius: 50px;
        font-size: 0.95rem;
        transition: all 0.3s ease;
    }
    
    .navbar-search input:focus {
        outline: none;
        border-color: var(--primary-color);
        box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
    }
    
    .navbar-search i {
        position: absolute;
        right: 1rem;
        top: 50%;
        transform: translateY(-50%);
        color: #9ca3af;
    }
    
    .navbar-actions {
        display: flex;
        align-items: center;
        gap: 1rem;
    }
    
    .nav-icon-btn {
        position: relative;
        width: 45px;
        height: 45px;
        border-radius: 50%;
        border: none;
        background: #f3f4f6;
        color: var(--dark-color);
        font-size: 1.2rem;
        cursor: pointer;
        transition: all 0.3s ease;
        display: flex;
        align-items: center;
        justify-content: center;
    }
    
    .nav-icon-btn:hover {
        background: var(--primary-color);
        color: white;
        transform: translateY(-2px);
    }
    
    .nav-icon-btn .badge {
        position: absolute;
        top: -5px;
        left: -5px;
        background: var(--danger-color);
        color: white;
        font-size: 0.7rem;
        padding: 0.2rem 0.4rem;
        border-radius: 50px;
    }
    
    .user-dropdown {
        position: relative;
    }
    
    .user-avatar {
        width: 45px;
        height: 45px;
        border-radius: 50%;
        background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
        color: white;
        display: flex;
        align-items: center;
        justify-content: center;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.3s ease;
    }
    
    .user-avatar:hover {
        transform: scale(1.1);
        box-shadow: 0 5px 15px rgba(102, 126, 234, 0.3);
    }
    
    .sidebar-toggle {
        display: none;
    }
    
    @media (max-width: 768px) {
        .sidebar-toggle {
            display: block;
        }
        
        .navbar-search {
            display: none;
        }
    }
</style>

<nav class="top-navbar">
    <!-- Toggle Sidebar -->
    <button class="nav-icon-btn sidebar-toggle" onclick="toggleSidebar()">
        <i class="fas fa-bars"></i>
    </button>
    
    <!-- Brand -->
    <a href="${pageContext.request.contextPath}/dashboard" class="navbar-brand">
        <i class="fas fa-university"></i>
        <span>بانک آنلاین</span>
    </a>
    
    <!-- Search Bar -->
    <div class="navbar-search position-relative">
        <i class="fas fa-search"></i>
        <input type="text" placeholder="جستجو در سیستم..." id="globalSearch">
    </div>
    
    <!-- Actions -->
    <div class="navbar-actions">
        <!-- Notifications -->
        <button class="nav-icon-btn" title="اعلان‌ها">
            <i class="fas fa-bell"></i>
            <span class="badge">${notificationCount != null ? notificationCount : 3}</span>
        </button>
        
        <!-- Messages -->
        <button class="nav-icon-btn" title="پیام‌ها">
            <i class="fas fa-envelope"></i>
            <span class="badge">${messageCount != null ? messageCount : 2}</span>
        </button>
        
        <!-- User Profile -->
        <div class="user-dropdown dropdown">
            <div class="user-avatar dropdown-toggle" data-bs-toggle="dropdown">
                ${sessionScope.fullName != null ? sessionScope.fullName.substring(0, 1) : 'U'}
            </div>
            <ul class="dropdown-menu dropdown-menu-end">
                <li>
                    <div class="dropdown-item-text">
                        <strong>${sessionScope.fullName != null ? sessionScope.fullName : 'کاربر'}</strong><br>
                        <small class="text-muted">${sessionScope.username}</small>
                    </div>
                </li>
                <li><hr class="dropdown-divider"></li>
                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/profile"><i class="fas fa-user me-2"></i>پروفایل</a></li>
                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/settings"><i class="fas fa-cog me-2"></i>تنظیمات</a></li>
                <li><hr class="dropdown-divider"></li>
                <li><a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/auth/logout"><i class="fas fa-sign-out-alt me-2"></i>خروج</a></li>
            </ul>
        </div>
    </div>
</nav>
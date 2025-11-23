<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
    @keyframes slideInDown {
        from {
            opacity: 0;
            transform: translateY(-20px);
        }
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }

    @keyframes shake {
        0%, 100% { transform: translateX(0); }
        25% { transform: translateX(-5px); }
        75% { transform: translateX(5px); }
    }

    .modern-alert {
        border: none;
        border-radius: 15px;
        padding: 1.25rem 1.5rem;
        margin-bottom: 1.5rem;
        box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
        animation: slideInDown 0.5s ease-out;
        display: flex;
        align-items: center;
        gap: 1rem;
        position: relative;
        overflow: hidden;
    }

    .modern-alert::before {
        content: '';
        position: absolute;
        left: 0;
        top: 0;
        bottom: 0;
        width: 5px;
    }

    .modern-alert-success {
        background: linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%);
        color: #065f46;
    }

    .modern-alert-success::before {
        background: #10b981;
    }

    .modern-alert-danger {
        background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
        color: #991b1b;
        animation: slideInDown 0.5s ease-out, shake 0.5s ease-out 0.5s;
    }

    .modern-alert-danger::before {
        background: #ef4444;
    }

    .modern-alert-info {
        background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
        color: #1e40af;
    }

    .modern-alert-info::before {
        background: #3b82f6;
    }

    .modern-alert-warning {
        background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
        color: #92400e;
    }

    .modern-alert-warning::before {
        background: #f59e0b;
    }

    .alert-icon {
        width: 45px;
        height: 45px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 1.3rem;
        flex-shrink: 0;
    }

    .modern-alert-success .alert-icon {
        background: #10b981;
        color: white;
    }

    .modern-alert-danger .alert-icon {
        background: #ef4444;
        color: white;
    }

    .modern-alert-info .alert-icon {
        background: #3b82f6;
        color: white;
    }

    .modern-alert-warning .alert-icon {
        background: #f59e0b;
        color: white;
    }

    .alert-content {
        flex: 1;
    }

    .alert-content strong {
        display: block;
        font-weight: 700;
        margin-bottom: 0.25rem;
        font-size: 1rem;
    }

    .alert-content p {
        margin: 0;
        font-size: 0.95rem;
        line-height: 1.5;
    }

    .modern-alert .btn-close {
        background: transparent;
        opacity: 0.6;
        transition: all 0.3s ease;
        padding: 0.5rem;
    }

    .modern-alert .btn-close:hover {
        opacity: 1;
        transform: rotate(90deg);
    }
</style>

<c:if test="${not empty success}">
    <div class="modern-alert modern-alert-success alert-dismissible fade show" role="alert">
        <div class="alert-icon">
            <i class="fas fa-check-circle"></i>
        </div>
        <div class="alert-content">
            <strong>موفقیت!</strong>
            <p>${success}</p>
        </div>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</c:if>

<c:if test="${not empty error}">
    <div class="modern-alert modern-alert-danger alert-dismissible fade show" role="alert">
        <div class="alert-icon">
            <i class="fas fa-exclamation-triangle"></i>
        </div>
        <div class="alert-content">
            <strong>خطا!</strong>
            <p>${error}</p>
        </div>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</c:if>

<c:if test="${not empty info}">
    <div class="modern-alert modern-alert-info alert-dismissible fade show" role="alert">
        <div class="alert-icon">
            <i class="fas fa-info-circle"></i>
        </div>
        <div class="alert-content">
            <strong>اطلاعات</strong>
            <p>${info}</p>
        </div>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</c:if>

<c:if test="${not empty warning}">
    <div class="modern-alert modern-alert-warning alert-dismissible fade show" role="alert">
        <div class="alert-icon">
            <i class="fas fa-exclamation-circle"></i>
        </div>
        <div class="alert-content">
            <strong>هشدار!</strong>
            <p>${warning}</p>
        </div>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</c:if>
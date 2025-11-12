<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="fa" dir="rtl">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ثبت‌نام - بانک آنلاین</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        :root {
            --primary-color: #667eea;
            --secondary-color: #764ba2;
            --success-color: #10b981;
            --error-color: #ef4444;
            --dark-color: #1f2937;
            --warning-color: #f59e0b;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 2rem;
            position: relative;
            overflow-x: hidden;
        }

        /* Animated Background Elements */
        body::before {
            content: '';
            position: absolute;
            width: 600px;
            height: 600px;
            background: rgba(255, 255, 255, 0.08);
            border-radius: 50%;
            top: -300px;
            right: -300px;
            animation: float 8s ease-in-out infinite;
        }

        body::after {
            content: '';
            position: absolute;
            width: 500px;
            height: 500px;
            background: rgba(255, 255, 255, 0.08);
            border-radius: 50%;
            bottom: -250px;
            left: -250px;
            animation: float 10s ease-in-out infinite reverse;
        }

        @keyframes float {
            0%, 100% { transform: translateY(0px) rotate(0deg); }
            50% { transform: translateY(30px) rotate(5deg); }
        }

        .register-container {
            position: relative;
            z-index: 1;
            width: 100%;
            max-width: 1200px;
            display: grid;
            grid-template-columns: 1fr 1.5fr;
            background: white;
            border-radius: 30px;
            box-shadow: 0 25px 70px rgba(0,0,0,0.35);
            overflow: hidden;
            animation: slideIn 0.8s ease;
        }

        @keyframes slideIn {
            from {
                opacity: 0;
                transform: scale(0.95) translateY(30px);
            }
            to {
                opacity: 1;
                transform: scale(1) translateY(0);
            }
        }

        /* Left Side - Info Section */
        .register-info {
            background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
            padding: 4rem 3rem;
            display: flex;
            flex-direction: column;
            justify-content: center;
            color: white;
        }

        .info-header {
            text-align: center;
            margin-bottom: 3rem;
        }

        .info-icon {
            font-size: 4rem;
            margin-bottom: 1.5rem;
            animation: bounce 2s ease-in-out infinite;
        }

        @keyframes bounce {
            0%, 100% { transform: translateY(0) scale(1); }
            50% { transform: translateY(-15px) scale(1.05); }
        }

        .info-header h2 {
            font-size: 2rem;
            font-weight: 800;
            margin-bottom: 0.75rem;
            text-shadow: 0 2px 15px rgba(0,0,0,0.2);
        }

        .info-header p {
            font-size: 1rem;
            opacity: 0.95;
            line-height: 1.5;
        }

        .benefits-list {
            list-style: none;
        }

        .benefit-item {
            display: flex;
            align-items: start;
            gap: 1rem;
            padding: 1.25rem;
            background: rgba(255, 255, 255, 0.1);
            backdrop-filter: blur(10px);
            border-radius: 15px;
            margin-bottom: 1rem;
            transition: all 0.3s ease;
            border: 1px solid rgba(255, 255, 255, 0.2);
        }

        .benefit-item:hover {
            background: rgba(255, 255, 255, 0.2);
            transform: translateX(-8px);
            border-color: rgba(255, 255, 255, 0.4);
        }

        .benefit-icon {
            width: 45px;
            height: 45px;
            background: white;
            color: var(--primary-color);
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.3rem;
            flex-shrink: 0;
            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
        }

        .benefit-content h4 {
            font-size: 1.05rem;
            margin-bottom: 0.35rem;
            font-weight: 700;
        }

        .benefit-content p {
            font-size: 0.9rem;
            opacity: 0.9;
            line-height: 1.4;
        }

        /* Right Side - Register Form */
        .register-form-section {
            padding: 3rem;
            display: flex;
            flex-direction: column;
            justify-content: center;
            max-height: 90vh;
            overflow-y: auto;
        }

        .register-form-section::-webkit-scrollbar {
            width: 8px;
        }

        .register-form-section::-webkit-scrollbar-track {
            background: #f1f1f1;
            border-radius: 10px;
        }

        .register-form-section::-webkit-scrollbar-thumb {
            background: var(--primary-color);
            border-radius: 10px;
        }

        .register-header {
            text-align: center;
            margin-bottom: 2rem;
        }

        .register-header h1 {
            font-size: 2rem;
            color: var(--dark-color);
            margin-bottom: 0.5rem;
            font-weight: 800;
        }

        .register-header p {
            color: #6b7280;
            font-size: 1rem;
        }

        /* Error/Success Message */
        .message {
            padding: 1rem 1.5rem;
            border-radius: 12px;
            margin-bottom: 1.5rem;
            display: flex;
            align-items: center;
            gap: 1rem;
            animation: shake 0.5s ease;
        }

        .error-message {
            background: linear-gradient(135deg, #fee2e2, #fecaca);
            border-right: 4px solid var(--error-color);
            color: #991b1b;
        }

        .success-message {
            background: linear-gradient(135deg, #d1fae5, #a7f3d0);
            border-right: 4px solid var(--success-color);
            color: #065f46;
        }

        .message i {
            font-size: 1.5rem;
        }

        @keyframes shake {
            0%, 100% { transform: translateX(0); }
            25% { transform: translateX(-10px); }
            75% { transform: translateX(10px); }
        }

        /* Form Styles */
        .register-form {
            display: grid;
            gap: 1.25rem;
        }

        .form-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 1rem;
        }

        .form-group {
            position: relative;
        }

        .form-group.full-width {
            grid-column: 1 / -1;
        }

        .form-group label {
            display: block;
            margin-bottom: 0.5rem;
            color: var(--dark-color);
            font-weight: 600;
            font-size: 0.9rem;
        }

        .form-group label .required {
            color: var(--error-color);
            margin-right: 2px;
        }

        .input-wrapper {
            position: relative;
        }

        .input-wrapper i.input-icon {
            position: absolute;
            right: 1rem;
            top: 50%;
            transform: translateY(-50%);
            color: #9ca3af;
            font-size: 1rem;
            transition: all 0.3s ease;
            pointer-events: none;
        }

        .form-control {
            width: 100%;
            padding: 0.9rem 0.9rem 0.9rem 3rem;
            border: 2px solid #e5e7eb;
            border-radius: 12px;
            font-size: 0.95rem;
            transition: all 0.3s ease;
            background: #f9fafb;
        }

        .form-control:focus {
            outline: none;
            border-color: var(--primary-color);
            background: white;
            box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
        }

        .form-control:focus ~ i.input-icon {
            color: var(--primary-color);
        }

        .password-toggle {
            position: absolute;
            left: 1rem;
            top: 50%;
            transform: translateY(-50%);
            color: #9ca3af;
            cursor: pointer;
            transition: all 0.3s ease;
            z-index: 10;
        }

        .password-toggle:hover {
            color: var(--primary-color);
        }

        /* Password Strength Indicator */
        .password-strength {
            margin-top: 0.5rem;
            height: 4px;
            background: #e5e7eb;
            border-radius: 2px;
            overflow: hidden;
        }

        .password-strength-bar {
            height: 100%;
            width: 0;
            transition: all 0.3s ease;
            border-radius: 2px;
        }

        .strength-weak {
            width: 33%;
            background: var(--error-color);
        }

        .strength-medium {
            width: 66%;
            background: var(--warning-color);
        }

        .strength-strong {
            width: 100%;
            background: var(--success-color);
        }

        .password-hint {
            font-size: 0.8rem;
            color: #6b7280;
            margin-top: 0.35rem;
        }

        /* Input Validation */
        .form-control.is-valid {
            border-color: var(--success-color);
            background: #f0fdf4;
        }

        .form-control.is-invalid {
            border-color: var(--error-color);
            background: #fef2f2;
        }

        .validation-icon {
            position: absolute;
            left: 1rem;
            top: 50%;
            transform: translateY(-50%);
            font-size: 1rem;
        }

        .validation-icon.valid {
            color: var(--success-color);
        }

        .validation-icon.invalid {
            color: var(--error-color);
        }

        /* Terms Checkbox */
        .terms-group {
            display: flex;
            align-items: start;
            gap: 0.75rem;
            padding: 1rem;
            background: #f9fafb;
            border-radius: 12px;
            border: 2px solid #e5e7eb;
            transition: all 0.3s ease;
        }

        .terms-group:has(input:checked) {
            background: #eff6ff;
            border-color: var(--primary-color);
        }

        .terms-group input[type="checkbox"] {
            width: 20px;
            height: 20px;
            cursor: pointer;
            accent-color: var(--primary-color);
            margin-top: 2px;
        }

        .terms-group label {
            margin: 0;
            color: #374151;
            font-size: 0.9rem;
            line-height: 1.5;
            cursor: pointer;
        }

        .terms-group a {
            color: var(--primary-color);
            text-decoration: none;
            font-weight: 600;
        }

        .terms-group a:hover {
            text-decoration: underline;
        }

        /* Submit Button */
        .btn-register {
            width: 100%;
            padding: 1rem;
            background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
            color: white;
            border: none;
            border-radius: 50px;
            font-size: 1.05rem;
            font-weight: 700;
            cursor: pointer;
            transition: all 0.3s ease;
            box-shadow: 0 10px 30px rgba(102, 126, 234, 0.3);
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 0.75rem;
        }

        .btn-register:hover:not(:disabled) {
            transform: translateY(-3px);
            box-shadow: 0 15px 40px rgba(102, 126, 234, 0.4);
        }

        .btn-register:active:not(:disabled) {
            transform: translateY(-1px);
        }

        .btn-register:disabled {
            opacity: 0.6;
            cursor: not-allowed;
        }

        .divider {
            display: flex;
            align-items: center;
            text-align: center;
            margin: 1.5rem 0;
            color: #9ca3af;
            font-size: 0.9rem;
        }

        .divider::before,
        .divider::after {
            content: '';
            flex: 1;
            border-bottom: 1px solid #e5e7eb;
        }

        .divider span {
            padding: 0 1rem;
        }

        .login-link {
            text-align: center;
            color: #6b7280;
            font-size: 0.95rem;
        }

        .login-link a {
            color: var(--primary-color);
            text-decoration: none;
            font-weight: 700;
            transition: all 0.3s ease;
        }

        .login-link a:hover {
            color: var(--secondary-color);
        }

        .back-home {
            text-align: center;
            margin-top: 1rem;
        }

        .back-home a {
            color: #6b7280;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 0.5rem;
            transition: all 0.3s ease;
            font-size: 0.9rem;
        }

        .back-home a:hover {
            color: var(--primary-color);
        }

        /* Responsive Design */
        @media (max-width: 1024px) {
            .register-container {
                grid-template-columns: 1fr;
                max-width: 600px;
            }

            .register-info {
                display: none;
            }

            .register-form-section {
                padding: 2.5rem 2rem;
            }

            .form-row {
                grid-template-columns: 1fr;
            }
        }

        @media (max-width: 480px) {
            body {
                padding: 1rem;
            }

            .register-header h1 {
                font-size: 1.5rem;
            }

            .register-form-section {
                padding: 2rem 1.5rem;
            }

            .form-control {
                font-size: 0.9rem;
                padding: 0.85rem 0.85rem 0.85rem 2.75rem;
            }

            .btn-register {
                font-size: 1rem;
            }
        }
    </style>
</head>
<body>
<div class="register-container">
    <!-- Left Side - Info Section -->
    <div class="register-info">
        <div class="info-header">
            <div class="info-icon">
                <i class="fas fa-user-plus"></i>
            </div>
            <h2>به خانواده ما بپیوندید</h2>
            <p>با ثبت‌نام در سیستم بانکداری آنلاین، دنیایی از امکانات را تجربه کنید</p>
        </div>

        <ul class="benefits-list">
            <li class="benefit-item">
                <div class="benefit-icon">
                    <i class="fas fa-rocket"></i>
                </div>
                <div class="benefit-content">
                    <h4>راه‌اندازی سریع</h4>
                    <p>فقط چند دقیقه تا ایجاد حساب کاربری</p>
                </div>
            </li>
            <li class="benefit-item">
                <div class="benefit-icon">
                    <i class="fas fa-shield-alt"></i>
                </div>
                <div class="benefit-content">
                    <h4>امنیت بالا</h4>
                    <p>رمزنگاری پیشرفته و احراز هویت چند مرحله‌ای</p>
                </div>
            </li>
            <li class="benefit-item">
                <div class="benefit-icon">
                    <i class="fas fa-wallet"></i>
                </div>
                <div class="benefit-content">
                    <h4>مدیریت حساب‌ها</h4>
                    <p>ایجاد و مدیریت چندین حساب بانکی</p>
                </div>
            </li>
            <li class="benefit-item">
                <div class="benefit-icon">
                    <i class="fas fa-exchange-alt"></i>
                </div>
                <div class="benefit-content">
                    <h4>تراکنش‌های آسان</h4>
                    <p>انتقال وجه سریع و امن بین حساب‌ها</p>
                </div>
            </li>
            <li class="benefit-item">
                <div class="benefit-icon">
                    <i class="fas fa-chart-line"></i>
                </div>
                <div class="benefit-content">
                    <h4>گزارش‌های کامل</h4>
                    <p>مشاهده تاریخچه و آمار مالی خود</p>
                </div>
            </li>
        </ul>
    </div>

    <!-- Right Side - Register Form -->
    <div class="register-form-section">
        <div class="register-header">
            <h1>ایجاد حساب کاربری</h1>
            <p>اطلاعات خود را با دقت وارد کنید</p>
        </div>

        <!-- Error Message -->
        <c:if test="${not empty error}">
            <div class="message error-message">
                <i class="fas fa-exclamation-circle"></i>
                <span>${error}</span>
            </div>
        </c:if>

        <!-- Register Form -->
        <form class="register-form" action="${pageContext.request.contextPath}/auth/register" method="post" id="registerForm">

            <!-- Username -->
            <div class="form-group full-width">
                <label for="username">
                    نام کاربری <span class="required">*</span>
                </label>
                <div class="input-wrapper">
                    <input
                            type="text"
                            id="username"
                            name="username"
                            class="form-control"
                            placeholder="نام کاربری دلخواه خود را وارد کنید"
                            required
                            minlength="4"
                            maxlength="50"
                            value="${param.username}"
                    >
                    <i class="fas fa-user input-icon"></i>
                </div>
            </div>

            <!-- First Name & Last Name -->
            <div class="form-row">
                <div class="form-group">
                    <label for="firstName">
                        نام <span class="required">*</span>
                    </label>
                    <div class="input-wrapper">
                        <input
                                type="text"
                                id="firstName"
                                name="firstName"
                                class="form-control"
                                placeholder="نام"
                                required
                                value="${param.firstName}"
                        >
                        <i class="fas fa-id-card input-icon"></i>
                    </div>
                </div>

                <div class="form-group">
                    <label for="lastName">
                        نام خانوادگی <span class="required">*</span>
                    </label>
                    <div class="input-wrapper">
                        <input
                                type="text"
                                id="lastName"
                                name="lastName"
                                class="form-control"
                                placeholder="نام خانوادگی"
                                required
                                value="${param.lastName}"
                        >
                        <i class="fas fa-id-card input-icon"></i>
                    </div>
                </div>
            </div>

            <!-- National Code & Phone -->
            <div class="form-row">
                <div class="form-group">
                    <label for="nationalCode">
                        کد ملی <span class="required">*</span>
                    </label>
                    <div class="input-wrapper">
                        <input
                                type="text"
                                id="nationalCode"
                                name="nationalCode"
                                class="form-control"
                                placeholder="0123456789"
                                required
                                pattern="[0-9]{10}"
                                maxlength="10"
                                value="${param.nationalCode}"
                        >
                        <i class="fas fa-fingerprint input-icon"></i>
                    </div>
                </div>

                <div class="form-group">
                    <label for="phone">
                        شماره موبایل <span class="required">*</span>
                    </label>
                    <div class="input-wrapper">
                        <input
                                type="tel"
                                id="phone"
                                name="phone"
                                class="form-control"
                                placeholder="09123456789"
                                required
                                pattern="09[0-9]{9}"
                                maxlength="11"
                                value="${param.phone}"
                        >
                        <i class="fas fa-mobile-alt input-icon"></i>
                    </div>
                </div>
            </div>

            <!-- Password -->
            <div class="form-group full-width">
                <label for="password">
                    رمز عبور <span class="required">*</span>
                </label>
                <div class="input-wrapper">
                    <input
                            type="password"
                            id="password"
                            name="password"
                            class="form-control"
                            placeholder="حداقل 6 کاراکتر"
                            required
                            minlength="6"
                    >
                    <i class="fas fa-lock input-icon"></i>
                    <i class="fas fa-eye password-toggle" id="togglePassword"></i>
                </div>
                <div class="password-strength">
                    <div class="password-strength-bar" id="strengthBar"></div>
                </div>
                <div class="password-hint">حداقل 6 کاراکتر - استفاده از حروف و اعداد توصیه می‌شود</div>
            </div>

            <!-- Confirm Password -->
            <div class="form-group full-width">
                <label for="confirmPassword">
                    تکرار رمز عبور <span class="required">*</span>
                </label>
                <div class="input-wrapper">
                    <input
                            type="password"
                            id="confirmPassword"
                            name="confirmPassword"
                            class="form-control"
                            placeholder="رمز عبور را مجدداً وارد کنید"
                            required
                    >
                    <i class="fas fa-lock input-icon"></i>
                    <i class="fas fa-eye password-toggle" id="toggleConfirmPassword"></i>
                    <i class="fas validation-icon" id="matchIcon"></i>
                </div>
            </div>

            <!-- Terms and Conditions -->
            <div class="form-group full-width">
                <div class="terms-group">
                    <input type="checkbox" id="terms" name="terms" required>
                    <label for="terms">
                        <a href="${pageContext.request.contextPath}/terms" target="_blank">قوانین و مقررات</a>
                        و
                        <a href="${pageContext.request.contextPath}/privacy" target="_blank">حریم خصوصی</a>
                        را مطالعه کرده و می‌پذیرم
                    </label>
                </div>
            </div>

            <!-- Submit Button -->
            <button type="submit" class="btn-register" id="submitBtn">
                <i class="fas fa-user-plus"></i>
                ثبت‌نام
            </button>
        </form>

        <div class="divider">
            <span>یا</span>
        </div>

        <div class="login-link">
            قبلاً ثبت‌نام کرده‌اید؟
            <a href="${pageContext.request.contextPath}/auth/login">
                وارد شوید
            </a>
        </div>

        <div class="back-home">
            <a href="${pageContext.request.contextPath}/">
                <i class="fas fa-arrow-right"></i>
                بازگشت به صفحه اصلی
            </a>
        </div>
    </div>
</div>

<script>
    // Password Toggle
    const togglePassword = document.getElementById('togglePassword');
    const passwordInput = document.getElementById('password');
    const toggleConfirmPassword = document.getElementById('toggleConfirmPassword');
    const confirmPasswordInput = document.getElementById('confirmPassword');

    togglePassword.addEventListener('click', function() {
        const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
        passwordInput.setAttribute('type', type);
        this.classList.toggle('fa-eye');
        this.classList.toggle('fa-eye-slash');
    });

    toggleConfirmPassword.addEventListener('click', function() {
        const type = confirmPasswordInput.getAttribute('type') === 'password' ? 'text' : 'password';
        confirmPasswordInput.setAttribute('type', type);
        this.classList.toggle('fa-eye');
        this.classList.toggle('fa-eye-slash');
    });

    // Password Strength Checker
    const strengthBar = document.getElementById('strengthBar');
    passwordInput.addEventListener('input', function() {
        const password = this.value;
        let strength = 0;

        if (password.length >= 6) strength++;
        if (password.length >= 10) strength++;
        if (/[a-z]/.test(password) && /[A-Z]/.test(password)) strength++;
        if (/[0-9]/.test(password)) strength++;
        if (/[^a-zA-Z0-9]/.test(password)) strength++;

        strengthBar.className = 'password-strength-bar';
        if (strength <= 2) {
            strengthBar.classList.add('strength-weak');
        } else if (strength <= 4) {
            strengthBar.classList.add('strength-medium');
        } else {
            strengthBar.classList.add('strength-strong');
        }
    });

    // Password Match Validation
    const matchIcon = document.getElementById('matchIcon');
    function checkPasswordMatch() {
        const password = passwordInput.value;
        const confirmPassword = confirmPasswordInput.value;

        if (confirmPassword.length === 0) {
            confirmPasswordInput.classList.remove('is-valid', 'is-invalid');
            matchIcon.style.display = 'none';
            return;
        }

        if (password === confirmPassword) {
            confirmPasswordInput.classList.add('is-valid');
            confirmPasswordInput.classList.remove('is-invalid');
            matchIcon.className = 'fas fa-check-circle validation-icon valid';
            matchIcon.style.display = 'block';
        } else {
            confirmPasswordInput.classList.add('is-invalid');
            confirmPasswordInput.classList.remove('is-valid');
            matchIcon.className = 'fas fa-times-circle validation-icon invalid';
            matchIcon.style.display = 'block';
        }
    }

    passwordInput.addEventListener('input', checkPasswordMatch);
    confirmPasswordInput.addEventListener('input', checkPasswordMatch);

    // National Code Validation
    const nationalCodeInput = document.getElementById('nationalCode');
    nationalCodeInput.addEventListener('input', function() {
        this.value = this.value.replace(/[^0-9]/g, '');
        if (this.value.length === 10) {
            this.classList.add('is-valid');
            this.classList.remove('is-invalid');
        } else if (this.value.length > 0) {
            this.classList.add('is-invalid');
            this.classList.remove('is-valid');
        } else {
            this.classList.remove('is-valid', 'is-invalid');
        }
    });

    // Phone Number Validation
    const phoneInput = document.getElementById('phone');
    phoneInput.addEventListener('input', function() {
        this.value = this.value.replace(/[^0-9]/g, '');
        if (/^09[0-9]{9}$/.test(this.value)) {
            this.classList.add('is-valid');
            this.classList.remove('is-invalid');
        } else if (this.value.length > 0) {
            this.classList.add('is-invalid');
            this.classList.remove('is-valid');
        } else {
            this.classList.remove('is-valid', 'is-invalid');
        }
    });

    // Username Validation
    const usernameInput = document.getElementById('username');
    usernameInput.addEventListener('input', function() {
        if (this.value.length >= 4 && this.value.length <= 50) {
            this.classList.add('is-valid');
            this.classList.remove('is-invalid');
        } else if (this.value.length > 0) {
            this.classList.add('is-invalid');
            this.classList.remove('is-valid');
        } else {
            this.classList.remove('is-valid', 'is-invalid');
        }
    });

    // Form Validation
    const form = document.getElementById('registerForm');
    const submitBtn = document.getElementById('submitBtn');

    form.addEventListener('submit', function(e) {
        const password = passwordInput.value;
        const confirmPassword = confirmPasswordInput.value;
        const terms = document.getElementById('terms').checked;

        // Check password match
        if (password !== confirmPassword) {
            e.preventDefault();
            alert('رمز عبور و تکرار آن یکسان نیستند');
            confirmPasswordInput.focus();
            return;
        }

        // Check terms
        if (!terms) {
            e.preventDefault();
            alert('لطفاً قوانین و مقررات را بپذیرید');
            return;
        }

        // Check national code format
        const nationalCode = nationalCodeInput.value;
        if (!/^[0-9]{10}$/.test(nationalCode)) {
            e.preventDefault();
            alert('کد ملی باید 10 رقم باشد');
            nationalCodeInput.focus();
            return;
        }

        // Check phone format
        const phone = phoneInput.value;
        if (!/^09[0-9]{9}$/.test(phone)) {
            e.preventDefault();
            alert('شماره موبایل نامعتبر است');
            phoneInput.focus();
            return;
        }

        // Disable submit button to prevent double submission
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> در حال ثبت‌نام...';
    });

    // Auto-hide error message after 7 seconds
    const errorMessage = document.querySelector('.error-message');
    if (errorMessage) {
        setTimeout(() => {
            errorMessage.style.animation = 'slideOut 0.5s ease';
            setTimeout(() => {
                errorMessage.style.display = 'none';
            }, 500);
        }, 7000);
    }

    // Add slideOut animation
    const style = document.createElement('style');
    style.textContent = `
            @keyframes slideOut {
                to {
                    opacity: 0;
                    transform: translateX(100px);
                }
            }
        `;
    document.head.appendChild(style);

    // Real-time input formatting
    phoneInput.addEventListener('keydown', function(e) {
        // Allow only numbers and control keys
        if (!/[0-9]/.test(e.key) &&
            !['Backspace', 'Delete', 'ArrowLeft', 'ArrowRight', 'Tab'].includes(e.key)) {
            e.preventDefault();
        }
    });

    nationalCodeInput.addEventListener('keydown', function(e) {
        // Allow only numbers and control keys
        if (!/[0-9]/.test(e.key) &&
            !['Backspace', 'Delete', 'ArrowLeft', 'ArrowRight', 'Tab'].includes(e.key)) {
            e.preventDefault();
        }
    });

    // Character counter for username
    usernameInput.addEventListener('input', function() {
        const length = this.value.length;
        const parent = this.closest('.form-group');
        let counter = parent.querySelector('.char-counter');

        if (!counter) {
            counter = document.createElement('div');
            counter.className = 'char-counter';
            counter.style.fontSize = '0.8rem';
            counter.style.color = '#6b7280';
            counter.style.marginTop = '0.35rem';
            parent.appendChild(counter);
        }

        counter.textContent = `${length}/50 کاراکتر`;

        if (length < 4) {
            counter.style.color = '#ef4444';
        } else if (length > 50) {
            counter.style.color = '#ef4444';
        } else {
            counter.style.color = '#10b981';
        }
    });

    // Focus first input on page load
    window.addEventListener('load', function() {
        usernameInput.focus();
    });
</script>
</body>
</html>
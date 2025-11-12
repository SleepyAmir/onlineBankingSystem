<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="fa" dir="rtl">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ورود به سیستم - بانک آنلاین</title>
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
            overflow: hidden;
        }

        /* Animated Background Elements */
        body::before {
            content: '';
            position: absolute;
            width: 500px;
            height: 500px;
            background: rgba(255, 255, 255, 0.1);
            border-radius: 50%;
            top: -250px;
            right: -250px;
            animation: float 6s ease-in-out infinite;
        }

        body::after {
            content: '';
            position: absolute;
            width: 400px;
            height: 400px;
            background: rgba(255, 255, 255, 0.1);
            border-radius: 50%;
            bottom: -200px;
            left: -200px;
            animation: float 8s ease-in-out infinite reverse;
        }

        @keyframes float {
            0%, 100% { transform: translateY(0px); }
            50% { transform: translateY(20px); }
        }

        .login-container {
            position: relative;
            z-index: 1;
            width: 100%;
            max-width: 1000px;
            display: grid;
            grid-template-columns: 1fr 1fr;
            background: white;
            border-radius: 30px;
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
            overflow: hidden;
            animation: slideIn 0.8s ease;
        }

        @keyframes slideIn {
            from {
                opacity: 0;
                transform: translateY(30px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        /* Left Side - Welcome Section */
        .login-welcome {
            background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
            padding: 4rem;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            color: white;
            text-align: center;
        }

        .welcome-icon {
            font-size: 5rem;
            margin-bottom: 2rem;
            animation: bounce 2s ease-in-out infinite;
        }

        @keyframes bounce {
            0%, 100% { transform: translateY(0); }
            50% { transform: translateY(-10px); }
        }

        .login-welcome h2 {
            font-size: 2.5rem;
            font-weight: 800;
            margin-bottom: 1rem;
            text-shadow: 0 2px 10px rgba(0,0,0,0.2);
        }

        .login-welcome p {
            font-size: 1.1rem;
            opacity: 0.9;
            line-height: 1.6;
            margin-bottom: 2rem;
        }

        .welcome-features {
            width: 100%;
            text-align: right;
        }

        .feature-item {
            display: flex;
            align-items: center;
            gap: 1rem;
            padding: 1rem;
            background: rgba(255, 255, 255, 0.1);
            border-radius: 15px;
            margin-bottom: 1rem;
            transition: all 0.3s ease;
        }

        .feature-item:hover {
            background: rgba(255, 255, 255, 0.2);
            transform: translateX(-5px);
        }

        .feature-item i {
            font-size: 1.5rem;
        }

        /* Right Side - Login Form */
        .login-form-section {
            padding: 4rem;
            display: flex;
            flex-direction: column;
            justify-content: center;
        }

        .login-header {
            text-align: center;
            margin-bottom: 3rem;
        }

        .login-header h1 {
            font-size: 2rem;
            color: var(--dark-color);
            margin-bottom: 0.5rem;
            font-weight: 800;
        }

        .login-header p {
            color: #6b7280;
            font-size: 1rem;
        }

        /* Error Message */
        .error-message {
            background: linear-gradient(135deg, #fee2e2, #fecaca);
            border-right: 4px solid var(--error-color);
            color: #991b1b;
            padding: 1rem 1.5rem;
            border-radius: 10px;
            margin-bottom: 2rem;
            display: flex;
            align-items: center;
            gap: 1rem;
            animation: shake 0.5s ease;
        }

        @keyframes shake {
            0%, 100% { transform: translateX(0); }
            25% { transform: translateX(-10px); }
            75% { transform: translateX(10px); }
        }

        .error-message i {
            font-size: 1.5rem;
        }

        /* Form Styles */
        .form-group {
            margin-bottom: 1.5rem;
        }

        .form-group label {
            display: block;
            margin-bottom: 0.5rem;
            color: var(--dark-color);
            font-weight: 600;
            font-size: 0.95rem;
        }

        .input-wrapper {
            position: relative;
        }

        .input-wrapper i {
            position: absolute;
            right: 1.25rem;
            top: 50%;
            transform: translateY(-50%);
            color: #9ca3af;
            font-size: 1.1rem;
            transition: all 0.3s ease;
        }

        .form-control {
            width: 100%;
            padding: 1rem 1rem 1rem 3.5rem;
            border: 2px solid #e5e7eb;
            border-radius: 12px;
            font-size: 1rem;
            transition: all 0.3s ease;
            background: #f9fafb;
        }

        .form-control:focus {
            outline: none;
            border-color: var(--primary-color);
            background: white;
            box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
        }

        .form-control:focus + i {
            color: var(--primary-color);
        }

        .password-toggle {
            position: absolute;
            left: 1.25rem;
            top: 50%;
            transform: translateY(-50%);
            color: #9ca3af;
            cursor: pointer;
            transition: all 0.3s ease;
        }

        .password-toggle:hover {
            color: var(--primary-color);
        }

        .form-options {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 2rem;
        }

        .remember-me {
            display: flex;
            align-items: center;
            gap: 0.5rem;
            cursor: pointer;
        }

        .remember-me input[type="checkbox"] {
            width: 18px;
            height: 18px;
            cursor: pointer;
            accent-color: var(--primary-color);
        }

        .remember-me label {
            margin: 0;
            color: #6b7280;
            font-size: 0.9rem;
            cursor: pointer;
        }

        .forgot-password {
            color: var(--primary-color);
            text-decoration: none;
            font-size: 0.9rem;
            font-weight: 600;
            transition: all 0.3s ease;
        }

        .forgot-password:hover {
            color: var(--secondary-color);
        }

        .btn-login {
            width: 100%;
            padding: 1rem;
            background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
            color: white;
            border: none;
            border-radius: 50px;
            font-size: 1.1rem;
            font-weight: 700;
            cursor: pointer;
            transition: all 0.3s ease;
            box-shadow: 0 10px 30px rgba(102, 126, 234, 0.3);
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 0.75rem;
        }

        .btn-login:hover {
            transform: translateY(-3px);
            box-shadow: 0 15px 40px rgba(102, 126, 234, 0.4);
        }

        .btn-login:active {
            transform: translateY(-1px);
        }

        .divider {
            display: flex;
            align-items: center;
            text-align: center;
            margin: 2rem 0;
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

        .register-link {
            text-align: center;
            color: #6b7280;
            font-size: 0.95rem;
        }

        .register-link a {
            color: var(--primary-color);
            text-decoration: none;
            font-weight: 700;
            transition: all 0.3s ease;
        }

        .register-link a:hover {
            color: var(--secondary-color);
        }

        .back-home {
            text-align: center;
            margin-top: 1.5rem;
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
        @media (max-width: 968px) {
            .login-container {
                grid-template-columns: 1fr;
                max-width: 500px;
            }

            .login-welcome {
                display: none;
            }

            .login-form-section {
                padding: 2rem;
            }

            body {
                padding: 1rem;
            }
        }

        @media (max-width: 480px) {
            .login-header h1 {
                font-size: 1.5rem;
            }

            .form-control {
                font-size: 0.95rem;
            }

            .btn-login {
                font-size: 1rem;
            }
        }
    </style>
</head>
<body>
<div class="login-container">
    <!-- Left Side - Welcome Section -->
    <div class="login-welcome">
        <div class="welcome-icon">
            <i class="fas fa-university"></i>
        </div>
        <h2>خوش آمدید!</h2>
        <p>به سیستم بانکداری آنلاین ما بپیوندید و از خدمات پیشرفته بانکی بهره‌مند شوید</p>

        <div class="welcome-features">
            <div class="feature-item">
                <i class="fas fa-shield-alt"></i>
                <span>امنیت بالا و رمزنگاری پیشرفته</span>
            </div>
            <div class="feature-item">
                <i class="fas fa-clock"></i>
                <span>دسترسی 24/7 به حساب بانکی</span>
            </div>
            <div class="feature-item">
                <i class="fas fa-mobile-alt"></i>
                <span>سازگار با تمام دستگاه‌ها</span>
            </div>
            <div class="feature-item">
                <i class="fas fa-headset"></i>
                <span>پشتیبانی همیشگی</span>
            </div>
        </div>
    </div>

    <!-- Right Side - Login Form -->
    <div class="login-form-section">
        <div class="login-header">
            <h1>ورود به حساب کاربری</h1>
            <p>لطفاً اطلاعات خود را وارد کنید</p>
        </div>

        <!-- Error Message -->
        <c:if test="${not empty error}">
            <div class="error-message">
                <i class="fas fa-exclamation-circle"></i>
                <span>${error}</span>
            </div>
        </c:if>

        <!-- Login Form -->
        <form action="${pageContext.request.contextPath}/auth/login" method="post">
            <div class="form-group">
                <label for="username">نام کاربری</label>
                <div class="input-wrapper">
                    <input
                            type="text"
                            id="username"
                            name="username"
                            class="form-control"
                            placeholder="نام کاربری خود را وارد کنید"
                            required
                            autofocus
                            value="${param.username}"
                    >
                    <i class="fas fa-user"></i>
                </div>
            </div>

            <div class="form-group">
                <label for="password">رمز عبور</label>
                <div class="input-wrapper">
                    <input
                            type="password"
                            id="password"
                            name="password"
                            class="form-control"
                            placeholder="رمز عبور خود را وارد کنید"
                            required
                    >
                    <i class="fas fa-lock"></i>
                    <i class="fas fa-eye password-toggle" id="togglePassword"></i>
                </div>
            </div>

            <div class="form-options">
                <div class="remember-me">
                    <input type="checkbox" id="remember" name="remember">
                    <label for="remember">مرا به خاطر بسپار</label>
                </div>
                <a href="${pageContext.request.contextPath}/auth/forgot-password" class="forgot-password">
                    فراموشی رمز عبور؟
                </a>
            </div>

            <button type="submit" class="btn-login">
                <i class="fas fa-sign-in-alt"></i>
                ورود به سیستم
            </button>
        </form>

        <div class="divider">
            <span>یا</span>
        </div>

        <div class="register-link">
            حساب کاربری ندارید؟
            <a href="${pageContext.request.contextPath}/auth/register">
                ثبت‌نام کنید
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

    togglePassword.addEventListener('click', function() {
        const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
        passwordInput.setAttribute('type', type);

        this.classList.toggle('fa-eye');
        this.classList.toggle('fa-eye-slash');
    });

    // Form Validation
    const form = document.querySelector('form');
    form.addEventListener('submit', function(e) {
        const username = document.getElementById('username').value.trim();
        const password = document.getElementById('password').value.trim();

        if (!username || !password) {
            e.preventDefault();
            alert('لطفاً تمام فیلدها را پر کنید');
        }
    });

    // Auto-hide error message after 5 seconds
    const errorMessage = document.querySelector('.error-message');
    if (errorMessage) {
        setTimeout(() => {
            errorMessage.style.animation = 'slideOut 0.5s ease';
            setTimeout(() => {
                errorMessage.style.display = 'none';
            }, 500);
        }, 5000);
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
</script>
</body>
</html>
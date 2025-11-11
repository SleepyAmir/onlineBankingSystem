<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fa" dir="rtl">
<head>
    <meta charset="UTF-8">
    <title>ورود به بانک</title>
    <style>
        body {
            font-family: Tahoma, Arial, sans-serif;
            background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
        }
        .login-container {
            background: white;
            padding: 40px;
            border-radius: 12px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
            width: 100%;
            max-width: 400px;
        }
        h2 {
            text-align: center;
            color: #2c3e50;
            margin-bottom: 30px;
            font-size: 24px;
        }
        .form-group {
            margin-bottom: 20px;
        }
        label {
            display: block;
            margin-bottom: 8px;
            color: #34495e;
            font-weight: bold;
        }
        input[type="text"], input[type="password"] {
            width: 100%;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 6px;
            font-size: 16px;
            transition: border 0.3s;
        }
        input[type="text"]:focus, input[type="password"]:focus {
            outline: none;
            border-color: #3498db;
            box-shadow: 0 0 0 2px rgba(52,152,219,0.2);
        }
        .error {
            color: #e74c3c;
            font-size: 14px;
            margin-top: 10px;
            text-align: center;
        }
        button {
            width: 100%;
            padding: 12px;
            background: #3498db;
            color: white;
            border: none;
            border-radius: 6px;
            font-size: 16px;
            cursor: pointer;
            transition: background 0.3s;
            margin-top: 10px;
        }
        button:hover {
            background: #2980b9;
        }
        .footer {
            text-align: center;
            margin-top: 20px;
            color: #7f8c8d;
            font-size: 14px;
        }
        .footer a {
            color: #3498db;
            text-decoration: none;
        }
        .footer a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
<div class="login-container">
    <h2>ورود به سیستم بانکداری</h2>

    <form action="${pageContext.request.contextPath}/login.do" method="post">
        <div class="form-group">
            <label for="username">نام کاربری</label>
            <input type="text" id="username" name="username" required placeholder="username وارد کنید">
        </div>

        <div class="form-group">
            <label for="password">رمز عبور</label>
            <input type="password" id="password" name="password" required placeholder="رمز عبور را وارد کنید">
        </div>

        <!-- CSRF Token -->
        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

        <button type="submit">ورود</button>
    </form>

    <!-- نمایش خطا -->
    <c:if test="${not empty error}">
        <div class="error">${error}</div>
    </c:if>

    <div class="footer">
        حساب کاربری ندارید؟ <a href="${pageContext.request.contextPath}/signup.do">ثبت‌نام کنید</a>
    </div>
</div>
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fa" dir="rtl">
<head>
    <meta charset="UTF-8" />
    <title>ورود | بانک اسلیپی</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
    <link href="https://fonts.googleapis.com/css2?family=Vazirmatn:wght@400;500;700&display=swap" rel="stylesheet" />
    <style>
        body { font-family: 'Vazirmatn', sans-serif; background: #f0f4f8; }
        .login-card { max-width: 400px; }
    </style>
</head>
<body class="bg-light d-flex align-items-center min-vh-100">
<div class="container">
    <div class="card login-card shadow-lg mx-auto p-4">
        <h3 class="text-center fw-bold text-primary mb-4">ورود به بانک اسلیپی</h3>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>
        <c:if test="${not empty param.msg}">
            <div class="alert alert-success">${param.msg}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/login" method="post">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
            <div class="mb-3">
                <input type="text" name="username" placeholder="نام کاربری" class="form-control" required />
            </div>
            <div class="mb-3">
                <input type="password" name="password" placeholder="رمز عبور" class="form-control" required />
            </div>
            <button type="submit" class="btn btn-primary w-100">ورود</button>
        </form>

        <div class="text-center mt-3">
            <a href="${pageContext.request.contextPath}/signup">ثبت‌نام</a>
        </div>
    </div>
</div>
</body>
</html>
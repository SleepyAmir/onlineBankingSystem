<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fa" dir="rtl">
<head>
    <meta charset="UTF-8" />
    <title>ثبت‌نام | بانک اسلیپی</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
    <link href="https://fonts.googleapis.com/css2?family=Vazirmatn:wght@400;500;700&display=swap" rel="stylesheet" />
    <style>
        body { font-family: 'Vazirmatn', sans-serif; background: #f0f4f8; }
        .signup-card { max-width: 500px; }
    </style>
</head>
<body class="bg-light d-flex align-items-center min-vh-100">
<div class="container">
    <div class="card signup-card shadow-lg mx-auto p-4">
        <h3 class="text-center fw-bold text-primary mb-4">ثبت‌نام در بانک اسلیپی</h3>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/signup" method="post">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

            <div class="row g-3">
                <div class="col-md-6">
                    <input type="text" name="firstName" placeholder="نام" class="form-control" required />
                </div>
                <div class="col-md-6">
                    <input type="text" name="lastName" placeholder="نام خانوادگی" class="form-control" required />
                </div>
                <div class="col-12">
                    <input type="text" name="username" placeholder="نام کاربری" class="form-control" required />
                </div>
                <div class="col-12">
                    <input type="password" name="password" placeholder="رمز عبور" class="form-control" required />
                </div>
                <div class="col-12">
                    <input type="text" name="phone" placeholder="تلفن" class="form-control" required />
                </div>
                <div class="col-12">
                    <input type="text" name="nationalCode" placeholder="کد ملی" class="form-control" required />
                </div>
                <div class="col-12">
                    <button type="submit" class="btn btn-primary w-100">ثبت‌نام</button>
                </div>
            </div>
        </form>
        <div class="text-center mt-3">
            <a href="${pageContext.request.contextPath}/login">قبلاً ثبت‌نام کرده‌اید؟ ورود</a>
        </div>
    </div>
</div>
</body>
</html>
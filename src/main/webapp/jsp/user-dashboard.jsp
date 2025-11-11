<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fa" dir="rtl">
<head>
    <meta charset="UTF-8" />
    <title>داشبورد کاربر | بانک اسلیپی</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
    <link href="https://fonts.googleapis.com/css2?family=Vazirmatn:wght@400;500;700&display=swap" rel="stylesheet" />
    <style>
        body { font-family: 'Vazirmatn', sans-serif; background: #f8f9fa; }
        .card { border: none; border-radius: 15px; box-shadow: 0 4px 15px rgba(0,0,0,0.05); }
        .stat-card { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; }
    </style>
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm">
    <div class="container-fluid">
        <a class="navbar-brand fw-bold" href="#">بانک اسلیپی</a>
        <div>
            <a href="/profile" class="btn btn-outline-light btn-sm">پروفایل</a>
            <a href="/logout" class="btn btn-outline-light btn-sm">خروج</a>
        </div>
    </div>
</nav>

<div class="container py-4">
    <c:if test="${not empty param.msg}">
        <div class="alert alert-success alert-dismissible fade show">
                ${param.msg}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <h2 class="mb-4">سلام، ${sessionScope.user.firstName}!</h2>

    <div class="row g-4 mb-5">
        <div class="col-md-3">
            <div class="card stat-card p-3 text-center">
                <h5>تعداد حساب‌ها</h5>
                <h3>${accounts.size()}</h3>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card stat-card p-3 text-center">
                <h5>کارت‌ها</h5>
                <h3>${cards.size()}</h3>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card stat-card p-3 text-center">
                <h5>وام‌ها</h5>
                <h3>${loans.size()}</h3>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card stat-card p-3 text-center">
                <h5>تراکنش اخیر</h5>
                <h3>${transactions.size()}</h3>
            </div>
        </div>
    </div>

    <div class="row g-4">
        <div class="col-lg-8">
            <div class="card p-4">
                <h5>حساب‌های شما</h5>
                <c:forEach var="acc" items="${accounts}">
                    <div class="d-flex justify-content-between border-bottom py-2">
                        <div>
                            <strong>${acc.type}</strong> - ${acc.accountNumber}
                        </div>
                        <div class="text-success">
                            <fmt:formatNumber value="${acc.balance}" type="currency" currencySymbol="تومان" />
                        </div>
                    </div>
                </c:forEach>
                <a href="/account/create" class="btn btn-outline-primary mt-3">+ حساب جدید</a>
            </div>
        </div>

        <div class="col-lg-4">
            <div class="card p-4">
                <h5>عملیات سریع</h5>
                <a href="/transfer" class="btn btn-sm btn-primary w-100 mb-2">انتقال وجه</a>
                <a href="/loan" class="btn btn-sm btn-info w-100 mb-2 text-white">درخواست وام</a>
            </div>
        </div>
    </div>
</div>
</body>
</html>
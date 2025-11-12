<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="fa" dir="rtl">
<head>
    <meta charset="UTF-8" />
    <title>داشبورد ادمین | بانک اسلیپی</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
    <link href="https://fonts.googleapis.com/css2?family=Vazirmatn:wght@400;500;700&display=swap" rel="stylesheet" />
    <style> body { font-family: 'Vazirmatn', sans-serif; } </style>
</head>
<body class="bg-light">
<jsp:include page="/templates/navbar.jsp" />
<div class="container py-4">
    <h2 class="mb-4">داشبورد ادمین</h2>

    <c:if test="${not empty param.msg}">
        <div class="alert alert-success">${param.msg}</div>
    </c:if>

    <div class="row g-4">
        <div class="col-md-6">
            <div class="card p-4">
                <h5>کاربران (${users.size()})</h5>
                <div class="table-responsive">
                    <table class="table table-sm">
                        <thead><tr><th>نام</th><th>وضعیت</th></tr></thead>
                        <tbody>
                        <c:forEach var="u" items="${users}">
                            <tr>
                                <td>${u.firstName} ${u.lastName}</td>
                                <td><span class="badge ${u.active ? 'bg-success' : 'bg-warning'}">
                                        ${u.active ? 'فعال' : 'غیرفعال'}
                                </span></td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <div class="col-md-6">
            <div class="card p-4">
                <h5>حساب‌ها (${accounts.size()})</h5>
                <div class="table-responsive">
                    <table class="table table-sm">
                        <thead><tr><th>شماره</th><th>نوع</th><th>موجودی</th><th>عملیات</th></tr></thead>
                        <tbody>
                        <c:forEach var="a" items="${accounts}">
                            <tr>
                                <td>${a.accountNumber}</td>
                                <td>${a.type}</td>
                                <td><fmt:formatNumber value="${a.balance}" type="currency" currencySymbol="تومان"/></td>
                                <td>
                                    <form action="${pageContext.request.contextPath}/admin-dashboard" method="post" class="d-inline">
                                        <input type="hidden" name="action" value="deleteAccount" />
                                        <input type="hidden" name="id" value="${a.id}" />
                                        <button class="btn btn-danger btn-sm" onclick="return confirm('مطمئنید؟')">حذف</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <div class="mt-4">
        <p><strong>کاربران آنلاین:</strong> ${onlineUsers}</p>
    </div>
</div>
</body>
</html>
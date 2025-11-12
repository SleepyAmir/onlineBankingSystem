<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="fa" dir="rtl">
<head>
    <meta charset="UTF-8" />
    <title>پروفایل | بانک اسلیپی</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
    <link href="https://fonts.googleapis.com/css2?family=Vazirmatn:wght@400;500;700&display=swap" rel="stylesheet" />
    <style> body { font-family: 'Vazirmatn', sans-serif; } </style>
</head>
<body class="bg-light">
<jsp:include page="/templates/navbar.jsp" />
<div class="container py-5">
    <div class="card col-md-6 mx-auto p-4 shadow">
        <h4 class="text-center mb-4">ویرایش پروفایل</h4>

        <c:if test="${not empty param.msg}">
            <div class="alert alert-success">${param.msg}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/profile" method="post">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
            <div class="mb-3">
                <label>نام</label>
                <input type="text" name="firstName" value="${user.firstName}" class="form-control" required />
            </div>
            <div class="mb-3">
                <label>نام خانوادگی</label>
                <input type="text" name="lastName" value="${user.lastName}" class="form-control" required />
            </div>
            <div class="mb-3">
                <label>تلفن</label>
                <input type="text" name="phone" value="${user.phone}" class="form-control" required />
            </div>
            <button type="submit" class="btn btn-primary w-100">ذخیره تغییرات</button>
        </form>
    </div>
</div>
</body>
</html>
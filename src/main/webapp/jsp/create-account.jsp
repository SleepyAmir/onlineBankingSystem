<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fa" dir="rtl">
<head>
    <meta charset="UTF-8" />
    <title>ایجاد حساب جدید</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
    <style> body { font-family: 'Vazirmatn', sans-serif; background: #f8f9fa; } </style>
</head>
<body class="bg-light">
<jsp:include page="/templates/navbar.jsp" />
<div class="container py-5">
    <div class="card col-md-6 mx-auto p-4 shadow">
        <h4 class="text-center mb-4">ایجاد حساب جدید</h4>
        <form action="/account/create" method="post">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
            <div class="mb-3">
                <label>نوع حساب</label>
                <select name="type" class="form-select" required>
                    <option value="SAVINGS">پس‌انداز</option>
                    <option value="CHECKING">جاری</option>
                </select>
            </div>
            <button type="submit" class="btn btn-success w-100">ایجاد حساب</button>
        </form>
    </div>
</div>
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fa" dir="rtl">
<head>
    <meta charset="UTF-8" />
    <title>درخواست وام</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
</head>
<body class="bg-light">
<jsp:include page="/templates/navbar.jsp" />
<div class="container py-5">
    <div class="card col-md-6 mx-auto p-4 shadow">
        <h4 class="text-center mb-4">درخواست وام</h4>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <form action="/jsp/loan" method="post">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
            <div class="mb-3">
                <label>حساب مقصد</label>
                <select name="account" class="form-select" required>
                    <c:forEach var="acc" items="${sessionScope.user.accounts}">
                        <option value="${acc.accountNumber}">${acc.accountNumber}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="mb-3">
                <label>مبلغ وام (تومان)</label>
                <input type="number" name="principal" class="form-control" min="1000000" required />
            </div>
            <div class="mb-3">
                <label>تعداد ماه</label>
                <input type="number" name="months" class="form-control" min="3" max="60" value="12" required />
            </div>
            <button type="submit" class="btn btn-warning w-100 text-dark">درخواست وام</button>
        </form>
    </div>
</div>
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fa" dir="rtl">
<head>
    <meta charset="UTF-8" />
    <title>انتقال وجه</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
</head>
<body class="bg-light">
<jsp:include page="/templates/navbar.jsp" />
<div class="container py-5">
    <div class="card col-md-6 mx-auto p-4 shadow">
        <h4 class="text-center mb-4">انتقال وجه</h4>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <form action="/transfer" method="post">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
            <div class="mb-3">
                <label>از حساب</label>
                <select name="fromAccount" class="form-select" required>
                    <c:forEach var="acc" items="${sessionScope.user.accounts}">
                        <option value="${acc.accountNumber}">${acc.accountNumber} (${acc.type})</option>
                    </c:forEach>
                </select>
            </div>
            <div class="mb-3">
                <label>به حساب</label>
                <input type="text" name="toAccount" class="form-control" placeholder="شماره حساب مقصد" required />
            </div>
            <div class="mb-3">
                <label>مبلغ (تومان)</label>
                <input type="number" name="amount" class="form-control" min="1000" required />
            </div>
            <button type="submit" class="btn btn-primary w-100">انتقال</button>
        </form>
    </div>
</div>
</body>
</html>
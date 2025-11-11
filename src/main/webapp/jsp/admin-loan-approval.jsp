<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fa" dir="rtl">
<head>
    <meta charset="UTF-8" />
    <title>تأیید وام</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
</head>
<body>
<div class="container py-4">
    <h3>وام‌های در انتظار تأیید</h3>
    <div class="table-responsive">
        <table class="table table-striped">
            <thead>
            <tr>
                <th>شماره وام</th>
                <th>کاربر</th>
                <th>مبلغ</th>
                <th>مدت</th>
                <th>عملیات</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="loan" items="${pendingLoans}">
                <tr>
                    <td>${loan.loanNumber}</td>
                    <td>${loan.account.user.firstName} ${loan.account.user.lastName}</td>
                    <td>${loan.principal}</td>
                    <td>${loan.durationMonths} ماه</td>
                    <td>
                        <form action="/admin/loan-approval" method="post" class="d-inline">
                            <input type="hidden" name="loanId" value="${loan.id}" />
                            <input type="hidden" name="action" value="approve" />
                            <button class="btn btn-success btn-sm">تأیید</button>
                        </form>
                        <form action="/admin/loan-approval" method="post" class="d-inline">
                            <input type="hidden" name="loanId" value="${loan.id}" />
                            <input type="hidden" name="action" value="reject" />
                            <button class="btn btn-danger btn-sm">رد</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>
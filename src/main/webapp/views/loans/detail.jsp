<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="fa" dir="rtl">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>جزئیات وام</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.rtl.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-4">
    <c:if test="${not empty loan}">
        <div class="card">
            <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
                <h4 class="mb-0">جزئیات وام</h4>
                <span class="badge
                    <c:choose>
                        <c:when test="${loan.status == 'PENDING'}">bg-warning</c:when>
                        <c:when test="${loan.status == 'APPROVED'}">bg-info</c:when>
                        <c:when test="${loan.status == 'ACTIVE'}">bg-success</c:when>
                        <c:when test="${loan.status == 'PAID'}">bg-secondary</c:when>
                        <c:when test="${loan.status == 'REJECTED'}">bg-danger</c:when>
                        <c:otherwise>bg-dark</c:otherwise>
                    </c:choose>
                    fs-6">
                    <c:choose>
                        <c:when test="${loan.status == 'PENDING'}">در انتظار تأیید</c:when>
                        <c:when test="${loan.status == 'APPROVED'}">تأیید شده</c:when>
                        <c:when test="${loan.status == 'ACTIVE'}">فعال</c:when>
                        <c:when test="${loan.status == 'PAID'}">تسویه شده</c:when>
                        <c:when test="${loan.status == 'REJECTED'}">رد شده</c:when>
                        <c:otherwise>${loan.status}</c:otherwise>
                    </c:choose>
                </span>
            </div>
            <div class="card-body">
                <div class="row">
                    <div class="col-md-6">
                        <table class="table table-bordered">
                            <tr>
                                <th>شماره وام</th>
                                <td>${loan.loanNumber}</td>
                            </tr>
                            <tr>
                                <th>مبلغ اصل وام</th>
                                <td>
                                    <fmt:formatNumber value="${loan.principal}" type="number" groupingUsed="true"/> ریال
                                </td>
                            </tr>
                            <tr>
                                <th>نرخ سود سالانه</th>
                                <td>${loan.annualInterestRate}%</td>
                            </tr>
                            <tr>
                                <th>مدت بازپرداخت</th>
                                <td>${loan.durationMonths} ماه</td>
                            </tr>
                            <tr>
                                <th>قسط ماهانه</th>
                                <td>
                                    <strong class="text-primary">
                                        <fmt:formatNumber value="${loan.monthlyPayment}" type="number" groupingUsed="true"/> ریال
                                    </strong>
                                </td>
                            </tr>
                        </table>
                    </div>
                    <div class="col-md-6">
                        <table class="table table-bordered">
                            <tr>
                                <th>تاریخ شروع</th>
                                <td>
                                        <%-- ✅ استفاده از متد کمکی --%>
                                    <fmt:formatDate value="${loan.startDateAsDate}" pattern="yyyy/MM/dd"/>
                                </td>
                            </tr>
                            <tr>
                                <th>تاریخ پایان</th>
                                <td>
                                        <%-- ✅ استفاده از متد کمکی --%>
                                    <fmt:formatDate value="${loan.endDateAsDate}" pattern="yyyy/MM/dd"/>
                                </td>
                            </tr>
                            <tr>
                                <th>کل بازپرداخت</th>
                                <td>
                                    <fmt:formatNumber value="${loan.totalRepayment}" type="number" groupingUsed="true"/> ریال
                                </td>
                            </tr>
                            <tr>
                                <th>کل سود</th>
                                <td>
                                    <fmt:formatNumber value="${loan.totalInterest}" type="number" groupingUsed="true"/> ریال
                                </td>
                            </tr>
                            <tr>
                                <th>تاریخ درخواست</th>
                                <td>
                                        <%-- ✅ استفاده از متد Base --%>
                                    <fmt:formatDate value="${loan.createdAtAsDate}" pattern="yyyy/MM/dd HH:mm"/>
                                </td>
                            </tr>
                        </table>
                    </div>
                </div>

                    <%-- اطلاعات حساب و کاربر --%>
                <div class="card mt-3">
                    <div class="card-header">اطلاعات متقاضی</div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-6">
                                <p><strong>نام:</strong> ${loan.user.firstName} ${loan.user.lastName}</p>
                                <p><strong>کد ملی:</strong> ${loan.user.nationalCode}</p>
                            </div>
                            <div class="col-md-6">
                                <p>
                                    <strong>شماره حساب:</strong>
                                    <a href="${pageContext.request.contextPath}/accounts/detail?id=${loan.account.id}">
                                            ${loan.account.accountNumber}
                                    </a>
                                </p>
                                <p>
                                    <strong>موجودی حساب:</strong>
                                    <fmt:formatNumber value="${loan.account.balance}" type="number" groupingUsed="true"/> ریال
                                </p>
                            </div>
                        </div>
                    </div>
                </div>

                    <%-- دکمه‌های عملیات --%>
                <div class="mt-4">
                        <%-- دکمه‌های مدیر --%>
                    <c:if test="${loan.status == 'PENDING'}">
                        <c:if test="${sessionScope.roles.contains('ADMIN') or sessionScope.roles.contains('MANAGER')}">
                            <form action="${pageContext.request.contextPath}/loans/approve" method="post" class="d-inline">
                                <input type="hidden" name="id" value="${loan.id}">
                                <button type="submit" class="btn btn-success"
                                        onclick="return confirm('آیا از تأیید این وام اطمینان دارید؟')">
                                    تأیید وام
                                </button>
                            </form>
                            <form action="${pageContext.request.contextPath}/loans/reject" method="post" class="d-inline">
                                <input type="hidden" name="id" value="${loan.id}">
                                <button type="submit" class="btn btn-danger"
                                        onclick="return confirm('آیا از رد این وام اطمینان دارید؟')">
                                    رد وام
                                </button>
                            </form>
                        </c:if>
                    </c:if>

                        <%-- دکمه پرداخت قسط --%>
                    <c:if test="${loan.status == 'APPROVED' or loan.status == 'ACTIVE'}">
                        <a href="${pageContext.request.contextPath}/loans/payment?id=${loan.id}"
                           class="btn btn-primary">
                            پرداخت قسط
                        </a>
                    </c:if>

                    <a href="${pageContext.request.contextPath}/loans/list" class="btn btn-secondary">
                        بازگشت به لیست
                    </a>
                </div>
            </div>
        </div>
    </c:if>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
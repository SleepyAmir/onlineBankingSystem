<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="fa" dir="rtl">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>جزئیات تراکنش</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.rtl.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-4">
    <div class="card">
        <div class="card-header bg-primary text-white">
            <h4>جزئیات تراکنش</h4>
        </div>
        <div class="card-body">
            <c:if test="${not empty transaction}">
                <table class="table table-bordered">
                    <tr>
                        <th>شناسه تراکنش</th>
                        <td>${transaction.transactionId}</td>
                    </tr>
                    <tr>
                        <th>نوع تراکنش</th>
                        <td>
                            <c:choose>
                                <c:when test="${transaction.type == 'DEPOSIT'}">
                                    <span class="badge bg-success">واریز</span>
                                </c:when>
                                <c:when test="${transaction.type == 'WITHDRAWAL'}">
                                    <span class="badge bg-danger">برداشت</span>
                                </c:when>
                                <c:when test="${transaction.type == 'TRANSFER'}">
                                    <span class="badge bg-info">انتقال</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-secondary">${transaction.type}</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                    <tr>
                        <th>مبلغ</th>
                        <td>
                            <fmt:formatNumber value="${transaction.amount}" type="number" groupingUsed="true"/> ریال
                        </td>
                    </tr>
                    <tr>
                        <th>حساب مبدأ</th>
                        <td>
                            <c:choose>
                                <c:when test="${transaction.fromAccount != null}">
                                    ${transaction.fromAccount.accountNumber}
                                    <small class="text-muted">
                                        (${transaction.fromAccount.user.firstName} ${transaction.fromAccount.user.lastName})
                                    </small>
                                </c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                    <tr>
                        <th>حساب مقصد</th>
                        <td>
                            <c:choose>
                                <c:when test="${transaction.toAccount != null}">
                                    ${transaction.toAccount.accountNumber}
                                    <small class="text-muted">
                                        (${transaction.toAccount.user.firstName} ${transaction.toAccount.user.lastName})
                                    </small>
                                </c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                    <tr>
                        <th>تاریخ و ساعت</th>
                        <td>
                                <%-- ✅ روش 1: استفاده از متد کمکی با fmt:formatDate --%>
                            <fmt:formatDate value="${transaction.transactionDateAsDate}"
                                            pattern="yyyy/MM/dd HH:mm:ss"/>

                                <%-- ✅ روش 2: استفاده از متد فرمت‌شده (بدون fmt:formatDate) --%>
                                <%-- ${transaction.formattedTransactionDate} --%>
                        </td>
                    </tr>
                    <tr>
                        <th>وضعیت</th>
                        <td>
                            <c:choose>
                                <c:when test="${transaction.status == 'COMPLETED'}">
                                    <span class="badge bg-success">موفق</span>
                                </c:when>
                                <c:when test="${transaction.status == 'PENDING'}">
                                    <span class="badge bg-warning">در انتظار</span>
                                </c:when>
                                <c:when test="${transaction.status == 'FAILED'}">
                                    <span class="badge bg-danger">ناموفق</span>
                                </c:when>
                                <c:when test="${transaction.status == 'REVERSED'}">
                                    <span class="badge bg-secondary">برگشت خورده</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-secondary">${transaction.status}</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                    <tr>
                        <th>شماره مرجع</th>
                        <td>${transaction.referenceNumber}</td>
                    </tr>
                    <tr>
                        <th>توضیحات</th>
                        <td>${transaction.description}</td>
                    </tr>
                    <tr>
                        <th>تاریخ ایجاد</th>
                        <td>
                                <%-- ✅ استفاده از متد Base برای createdAt --%>
                            <fmt:formatDate value="${transaction.createdAtAsDate}"
                                            pattern="yyyy/MM/dd HH:mm:ss"/>
                        </td>
                    </tr>
                </table>
            </c:if>

            <div class="mt-3">
                <a href="${pageContext.request.contextPath}/transactions/history" class="btn btn-secondary">
                    بازگشت به لیست
                </a>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
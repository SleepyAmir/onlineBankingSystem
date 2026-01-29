<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="fa" dir="rtl">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>تاریخچه تراکنش‌ها</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.rtl.min.css" rel="stylesheet">
</head>
<body>
<div class="container-fluid mt-4">
    <div class="card">
        <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
            <h4 class="mb-0">تاریخچه تراکنش‌ها</h4>
            <a href="${pageContext.request.contextPath}/transactions" class="btn btn-light btn-sm">
                بازگشت
            </a>
        </div>
        <div class="card-body">
            <c:if test="${not empty error}">
                <div class="alert alert-danger">${error}</div>
            </c:if>

            <c:if test="${not empty message}">
                <div class="alert alert-success">${message}</div>
            </c:if>

            <c:choose>
                <c:when test="${empty transactions}">
                    <div class="alert alert-info">تراکنشی یافت نشد.</div>
                </c:when>
                <c:otherwise>
                    <div class="table-responsive">
                        <table class="table table-striped table-hover">
                            <thead class="table-dark">
                            <tr>
                                <th>شناسه</th>
                                <th>نوع</th>
                                <th>مبلغ</th>
                                <th>از حساب</th>
                                <th>به حساب</th>
                                <th>تاریخ</th>
                                <th>وضعیت</th>
                                <th>عملیات</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="transaction" items="${transactions}">
                                <tr>
                                    <td>
                                        <small>${transaction.transactionId}</small>
                                    </td>
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
                                    <td>
                                        <fmt:formatNumber value="${transaction.amount}"
                                                          type="number"
                                                          groupingUsed="true"/>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${transaction.fromAccount != null}">
                                                ${transaction.fromAccount.accountNumber}
                                            </c:when>
                                            <c:otherwise>-</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${transaction.toAccount != null}">
                                                ${transaction.toAccount.accountNumber}
                                            </c:when>
                                            <c:otherwise>-</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                            <%-- ✅ استفاده از متد کمکی --%>
                                        <fmt:formatDate value="${transaction.transactionDateAsDate}"
                                                        pattern="yyyy/MM/dd HH:mm"/>
                                    </td>
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
                                                <span class="badge bg-secondary">برگشت</span>
                                            </c:when>
                                            <c:otherwise>
                                                ${transaction.status}
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/transactions/detail?id=${transaction.id}"
                                           class="btn btn-sm btn-outline-primary">
                                            جزئیات
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>

                    <%-- صفحه‌بندی --%>
                    <nav aria-label="Page navigation">
                        <ul class="pagination justify-content-center">
                            <c:if test="${currentPage > 0}">
                                <li class="page-item">
                                    <a class="page-link"
                                       href="${pageContext.request.contextPath}/transactions/history?page=${currentPage - 1}">
                                        قبلی
                                    </a>
                                </li>
                            </c:if>
                            <li class="page-item active">
                                <span class="page-link">${currentPage + 1}</span>
                            </li>
                            <c:if test="${transactions.size() >= pageSize}">
                                <li class="page-item">
                                    <a class="page-link"
                                       href="${pageContext.request.contextPath}/transactions/history?page=${currentPage + 1}">
                                        بعدی
                                    </a>
                                </li>
                            </c:if>
                        </ul>
                    </nav>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
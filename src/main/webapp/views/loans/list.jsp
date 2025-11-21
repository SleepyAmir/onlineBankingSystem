<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="fa" dir="rtl">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>لیست وام‌ها</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.rtl.min.css" rel="stylesheet">
</head>
<body>
<div class="container-fluid mt-4">
    <div class="card">
        <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
            <h4 class="mb-0">لیست وام‌ها</h4>
            <a href="${pageContext.request.contextPath}/loans/apply" class="btn btn-light btn-sm">
                درخواست وام جدید
            </a>
        </div>
        <div class="card-body">
            <c:if test="${not empty error}">
                <div class="alert alert-danger">${error}</div>
            </c:if>

            <c:if test="${not empty message}">
                <div class="alert alert-success">${message}</div>
            </c:if>

            <%-- فیلتر وضعیت --%>
            <form method="get" class="row g-3 mb-4">
                <div class="col-md-4">
                    <label class="form-label">فیلتر بر اساس وضعیت:</label>
                    <select name="status" class="form-select" onchange="this.form.submit()">
                        <option value="">همه</option>
                        <c:forEach var="status" items="${loanStatuses}">
                            <option value="${status}" ${selectedStatus == status ? 'selected' : ''}>
                                <c:choose>
                                    <c:when test="${status == 'PENDING'}">در انتظار</c:when>
                                    <c:when test="${status == 'APPROVED'}">تأیید شده</c:when>
                                    <c:when test="${status == 'ACTIVE'}">فعال</c:when>
                                    <c:when test="${status == 'PAID'}">تسویه شده</c:when>
                                    <c:when test="${status == 'REJECTED'}">رد شده</c:when>
                                    <c:otherwise>${status}</c:otherwise>
                                </c:choose>
                            </option>
                        </c:forEach>
                    </select>
                </div>
            </form>

            <c:choose>
                <c:when test="${empty loans}">
                    <div class="alert alert-info">وامی یافت نشد.</div>
                </c:when>
                <c:otherwise>
                    <div class="table-responsive">
                        <table class="table table-striped table-hover">
                            <thead class="table-dark">
                            <tr>
                                <th>شماره وام</th>
                                <th>مبلغ اصل</th>
                                <th>نرخ سود</th>
                                <th>مدت (ماه)</th>
                                <th>قسط ماهانه</th>
                                <th>تاریخ شروع</th>
                                <th>وضعیت</th>
                                <th>عملیات</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="loan" items="${loans}">
                                <tr>
                                    <td>${loan.loanNumber}</td>
                                    <td>
                                        <fmt:formatNumber value="${loan.principal}"
                                                          type="number"
                                                          groupingUsed="true"/>
                                    </td>
                                    <td>
                                            <%-- ✅ اصلاح شد: از annualInterestRate استفاده کنید --%>
                                        <fmt:formatNumber value="${loan.annualInterestRate}"
                                                          type="number"
                                                          maxFractionDigits="2"/>%
                                    </td>
                                    <td>${loan.durationMonths}</td>
                                    <td>
                                        <fmt:formatNumber value="${loan.monthlyPayment}"
                                                          type="number"
                                                          groupingUsed="true"/>
                                    </td>
                                    <td>
                                            <%-- ✅ استفاده از متد کمکی برای تاریخ --%>
                                        <fmt:formatDate value="${loan.startDateAsDate}"
                                                        pattern="yyyy/MM/dd"/>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${loan.status == 'PENDING'}">
                                                <span class="badge bg-warning text-dark">در انتظار</span>
                                            </c:when>
                                            <c:when test="${loan.status == 'APPROVED'}">
                                                <span class="badge bg-info">تأیید شده</span>
                                            </c:when>
                                            <c:when test="${loan.status == 'ACTIVE'}">
                                                <span class="badge bg-success">فعال</span>
                                            </c:when>
                                            <c:when test="${loan.status == 'PAID'}">
                                                <span class="badge bg-secondary">تسویه شده</span>
                                            </c:when>
                                            <c:when test="${loan.status == 'REJECTED'}">
                                                <span class="badge bg-danger">رد شده</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-dark">${loan.status}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/loans/detail?id=${loan.id}"
                                           class="btn btn-sm btn-outline-primary">
                                            جزئیات
                                        </a>

                                            <%-- دکمه پرداخت قسط --%>
                                        <c:if test="${loan.status == 'APPROVED' or loan.status == 'ACTIVE'}">
                                            <a href="${pageContext.request.contextPath}/loans/payment?id=${loan.id}"
                                               class="btn btn-sm btn-outline-success">
                                                پرداخت
                                            </a>
                                        </c:if>
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
                                       href="${pageContext.request.contextPath}/loans/list?page=${currentPage - 1}">
                                        قبلی
                                    </a>
                                </li>
                            </c:if>
                            <li class="page-item active">
                                <span class="page-link">${currentPage + 1}</span>
                            </li>
                            <c:if test="${loans.size() >= pageSize}">
                                <li class="page-item">
                                    <a class="page-link"
                                       href="${pageContext.request.contextPath}/loans/list?page=${currentPage + 1}">
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
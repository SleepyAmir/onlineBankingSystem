<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/views/common/header.jsp">
    <jsp:param name="title" value="تاریخچه تراکنش‌ها" />
</jsp:include>

<jsp:include page="/views/common/navbar.jsp" />
<jsp:include page="/views/common/sidebar.jsp" />

<div class="content-wrapper">
    <div class="container-fluid px-4 py-4">
        <h1 class="display-5 fw-bold">تاریخچه تراکنش‌ها</h1>
        <p class="lead text-muted">مشاهده تمام تراکنش‌های انجام شده</p>

        <!-- فیلترها -->
        <div class="card mb-4">
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/transactions/history" method="get">
                    <div class="row">
                        <c:if test="${not empty userAccounts}">
                            <div class="col-md-4 mb-3">
                                <label for="accountId" class="form-label">حساب</label>
                                <select class="form-select" id="accountId" name="accountId">
                                    <option value="">همه حساب‌ها</option>
                                    <c:forEach items="${userAccounts}" var="acc">
                                        <option value="${acc.id}">${acc.accountNumber}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </c:if>
                        <div class="col-md-3 mb-3">
                            <label for="startDate" class="form-label">از تاریخ</label>
                            <input type="date" class="form-control" id="startDate" name="startDate">
                        </div>
                        <div class="col-md-3 mb-3">
                            <label for="endDate" class="form-label">تا تاریخ</label>
                            <input type="date" class="form-control" id="endDate" name="endDate">
                        </div>
                        <div class="col-md-2 mt-4">
                            <button type="submit" class="btn btn-primary w-100">فیلتر</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <!-- جدول تراکنش‌ها -->
        <div class="card shadow-lg border-0 rounded-3">
            <div class="card-body">
                <c:if test="${empty transactions}">
                    <div class="alert alert-info text-center">
                        <i class="fas fa-info-circle me-2"></i>هیچ تراکنشی یافت نشد
                    </div>
                </c:if>
                <c:if test="${not empty transactions}">
                    <table class="table table-hover table-striped">
                        <thead>
                        <tr>
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
                        <c:forEach items="${transactions}" var="tx">
                            <tr>
                                <td><c:out value="${tx.type}" /></td>
                                <td><fmt:formatNumber value="${tx.amount}" type="currency" currencySymbol="ریال" /></td>
                                <td><c:out value="${tx.fromAccount != null ? tx.fromAccount.accountNumber : '-'}" /></td>
                                <td><c:out value="${tx.toAccount != null ? tx.toAccount.accountNumber : '-'}" /></td>
                                <td><fmt:formatDate value="${tx.transactionDate}" pattern="yyyy/MM/dd HH:mm" /></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${tx.status == 'COMPLETED'}">
                                            <span class="badge bg-success">موفق</span>
                                        </c:when>
                                        <c:when test="${tx.status == 'PENDING'}">
                                            <span class="badge bg-warning">در انتظار</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-danger">ناموفق</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/transactions/detail?id=${tx.id}"
                                       class="btn btn-sm btn-info">
                                        <i class="fas fa-eye"></i>
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </c:if>
            </div>
        </div>

        <!-- صفحه‌بندی -->
        <nav aria-label="Pagination" class="mt-4">
            <ul class="pagination justify-content-center">
                <li class="page-item ${currentPage == 0 ? 'disabled' : ''}">
                    <a class="page-link" href="?page=${currentPage - 1}">قبلی</a>
                </li>
                <li class="page-item active">
                    <span class="page-link">${currentPage + 1}</span>
                </li>
                <li class="page-item">
                    <a class="page-link" href="?page=${currentPage + 1}">بعدی</a>
                </li>
            </ul>
        </nav>
    </div>
</div>

<jsp:include page="/views/common/footer.jsp" />
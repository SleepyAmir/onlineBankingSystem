<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/views/common/header.jsp">
    <jsp:param name="title" value="لیست وام‌ها" />
</jsp:include>

<jsp:include page="/views/common/navbar.jsp" />
<jsp:include page="/views/common/sidebar.jsp" />

<div class="content-wrapper">
    <div class="container-fluid px-4 py-4">
        <h1 class="display-5 fw-bold">لیست وام‌ها</h1>
        <p class="lead text-muted">مدیریت درخواست‌های وام</p>

        <!-- پیام‌های موفقیت/خطا -->
        <c:if test="${not empty param.message}">
            <div class="alert alert-success">
                <c:choose>
                    <c:when test="${param.message == 'applied'}">درخواست وام ثبت شد</c:when>
                    <c:when test="${param.message == 'approved'}">وام تأیید شد</c:when>
                </c:choose>
            </div>
        </c:if>
        <c:if test="${not empty param.error}">
            <div class="alert alert-danger">
                <c:choose>
                    <c:when test="${param.error == 'missing_id'}">شناسه مشخص نشده</c:when>
                    <c:when test="${param.error == 'not_found'}">وام یافت نشد</c:when>
                    <c:when test="${param.error == 'not_pending'}">وام در وضعیت انتظار نیست</c:when>
                    <c:when test="${param.error == 'approval_failed'}">تأیید ناموفق بود</c:when>
                </c:choose>
            </div>
        </c:if>

        <!-- فیلتر وضعیت -->
        <div class="mb-4">
            <form action="${pageContext.request.contextPath}/loans/list" method="get">
                <div class="row">
                    <div class="col-md-4">
                        <select class="form-select" name="status">
                            <option value="">همه وضعیت‌ها</option>
                            <c:forEach items="${loanStatuses}" var="status">
                                <option value="${status}" ${selectedStatus == status ? 'selected' : ''}><c:out value="${status}" /></option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <button type="submit" class="btn btn-primary">فیلتر</button>
                    </div>
                </div>
            </form>
        </div>

        <!-- جدول وام‌ها -->
        <div class="card shadow-lg border-0 rounded-3">
            <div class="card-body">
                <table class="table table-hover table-striped">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>شماره وام</th>
                        <th>مبلغ</th>
                        <th>نرخ بهره</th>
                        <th>مدت (ماه)</th>
                        <th>وضعیت</th>
                        <th>کاربر</th>
                        <th>تاریخ درخواست</th>
                        <th>عملیات</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${loans}" var="loan">
                        <tr>
                            <td><c:out value="${loan.id}" /></td>
                            <td><c:out value="${loan.loanNumber}" /></td>
                            <td><fmt:formatNumber value="${loan.principal}" type="currency" currencySymbol="ریال" /></td>
                            <td><fmt:formatNumber value="${loan.interestRate}" type="percent" /></td>
                            <td><c:out value="${loan.durationMonths}" /></td>
                            <td><c:out value="${loan.status}" /></td>
                            <td><c:out value="${loan.user.username}" /></td>
                            <td><fmt:formatDate value="${loan.createdAt}" pattern="yyyy/MM/dd HH:mm" /></td>
                            <td>
                                <a href="${pageContext.request.contextPath}/loans/detail?id=${loan.id}" class="btn btn-sm btn-info me-1">
                                    <i class="fas fa-eye"></i>
                                </a>
                                <c:if test="${loan.status == 'PENDING' && (sessionScope.roles.contains('ADMIN') || sessionScope.roles.contains('MANAGER'))}">
                                    <form action="${pageContext.request.contextPath}/loans/approve" method="post" style="display: inline;">
                                        <input type="hidden" name="id" value="${loan.id}">
                                        <button type="submit" class="btn btn-sm btn-success me-1">
                                            <i class="fas fa-check"></i>
                                        </button>
                                    </form>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- صفحه‌بندی -->
        <nav aria-label="Pagination" class="mt-4">
            <ul class="pagination justify-content-center">
                <li class="page-item ${currentPage == 0 ? 'disabled' : ''}">
                    <a class="page-link" href="?page=${currentPage - 1}&status=${selectedStatus}">قبلی</a>
                </li>
                <li class="page-item active">
                    <span class="page-link">${currentPage + 1}</span>
                </li>
                <li class="page-item">
                    <a class="page-link" href="?page=${currentPage + 1}&status=${selectedStatus}">بعدی</a>
                </li>
            </ul>
        </nav>
    </div>
</div>

<jsp:include page="/views/common/footer.jsp" />
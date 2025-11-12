<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/views/common/header.jsp">
    <jsp:param name="title" value="لیست حساب‌ها" />
</jsp:include>

<jsp:include page="/views/common/navbar.jsp" />
<jsp:include page="/views/common/sidebar.jsp" />

<div class="content-wrapper">
    <div class="container-fluid px-4 py-4">
        <h1 class="display-5 fw-bold">لیست حساب‌ها</h1>
        <p class="lead text-muted">مدیریت حساب‌های بانکی</p>

        <!-- پیام‌های موفقیت/خطا -->
        <c:if test="${not empty param.message}">
            <div class="alert alert-success">
                <c:choose>
                    <c:when test="${param.message == 'created'}">حساب با موفقیت ایجاد شد</c:when>
                    <c:when test="${param.message == 'updated'}">حساب با موفقیت به‌روزرسانی شد</c:when>
                </c:choose>
            </div>
        </c:if>
        <c:if test="${not empty param.error}">
            <div class="alert alert-danger">
                <c:choose>
                    <c:when test="${param.error == 'missing_id'}">شناسه حساب مشخص نشده است</c:when>
                    <c:when test="${param.error == 'invalid_id'}">شناسه نامعتبر</c:when>
                    <c:when test="${param.error == 'not_found'}">حساب یافت نشد</c:when>
                </c:choose>
            </div>
        </c:if>

        <!-- دکمه ایجاد حساب جدید -->
        <div class="mb-4">
            <a href="${pageContext.request.contextPath}/accounts/create" class="btn btn-primary">
                <i class="fas fa-plus me-2"></i>ایجاد حساب جدید
            </a>
        </div>

        <!-- جدول حساب‌ها -->
        <div class="card shadow-lg border-0 rounded-3">
            <div class="card-body">
                <table class="table table-hover table-striped">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>شماره حساب</th>
                        <th>نوع</th>
                        <th>موجودی</th>
                        <th>وضعیت</th>
                        <th>کاربر</th>
                        <th>تاریخ ایجاد</th>
                        <th>عملیات</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${accounts}" var="account">
                        <tr>
                            <td><c:out value="${account.id}" /></td>
                            <td><c:out value="${account.accountNumber}" /></td>
                            <td><c:out value="${account.type}" /></td>
                            <td><fmt:formatNumber value="${account.balance}" type="currency" currencySymbol="ریال" /></td>
                            <td>
                                <c:choose>
                                    <c:when test="${account.status == 'ACTIVE'}"><span class="badge bg-success">فعال</span></c:when>
                                    <c:when test="${account.status == 'FROZEN'}"><span class="badge bg-warning">فریز</span></c:when>
                                    <c:when test="${account.status == 'CLOSED'}"><span class="badge bg-danger">بسته</span></c:when>
                                </c:choose>
                            </td>
                            <td><c:out value="${account.user.username}" /></td>
                            <td><fmt:formatDate value="${account.createdAt}" pattern="yyyy/MM/dd HH:mm" /></td>
                            <td>
                                <a href="${pageContext.request.contextPath}/accounts/detail?id=${account.id}" class="btn btn-sm btn-info me-1">
                                    <i class="fas fa-eye"></i>
                                </a>
                                <a href="${pageContext.request.contextPath}/accounts/update?id=${account.id}" class="btn btn-sm btn-warning me-1">
                                    <i class="fas fa-edit"></i>
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- صفحه‌بندی (اگر صفحه‌بندی فعال باشه) -->
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
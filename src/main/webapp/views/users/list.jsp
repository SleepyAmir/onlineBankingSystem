<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/views/common/header.jsp">
    <jsp:param name="title" value="لیست کاربران" />
</jsp:include>

<jsp:include page="/views/common/navbar.jsp" />
<jsp:include page="/views/common/sidebar.jsp" />

<div class="content-wrapper">
    <div class="container-fluid px-4 py-4">
        <h1 class="display-5 fw-bold">لیست کاربران</h1>
        <p class="lead text-muted">مدیریت کاربران سیستم</p>

        <!-- پیام‌های موفقیت/خطا -->
        <c:if test="${not empty param.message}">
            <div class="alert alert-success">
                <c:choose>
                    <c:when test="${param.message == 'created'}">کاربر با موفقیت ایجاد شد</c:when>
                    <c:when test="${param.message == 'updated'}">کاربر با موفقیت به‌روزرسانی شد</c:when>
                    <c:when test="${param.message == 'deleted'}">کاربر با موفقیت حذف شد</c:when>
                </c:choose>
            </div>
        </c:if>
        <c:if test="${not empty param.error}">
            <div class="alert alert-danger">
                <c:choose>
                    <c:when test="${param.error == 'missing_id'}">شناسه کاربر مشخص نشده است</c:when>
                    <c:when test="${param.error == 'invalid_id'}">شناسه نامعتبر</c:when>
                    <c:when test="${param.error == 'not_found'}">کاربر یافت نشد</c:when>
                    <c:when test="${param.error == 'cannot_delete_self'}">نمی‌توانید خودتان را حذف کنید</c:when>
                    <c:when test="${param.error == 'delete_failed'}">حذف ناموفق بود</c:when>
                </c:choose>
            </div>
        </c:if>

        <!-- دکمه ایجاد کاربر جدید -->
        <div class="mb-4">
            <a href="${pageContext.request.contextPath}/users/create" class="btn btn-primary">
                <i class="fas fa-plus me-2"></i>ایجاد کاربر جدید
            </a>
        </div>

        <!-- جدول کاربران -->
        <div class="card shadow-lg border-0 rounded-3">
            <div class="card-body">
                <table class="table table-hover table-striped">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>نام کاربری</th>
                        <th>نام کامل</th>
                        <th>شماره تلفن</th>
                        <th>کد ملی</th>
                        <th>وضعیت</th>
                        <th>تاریخ ایجاد</th>
                        <th>عملیات</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${users}" var="user">
                        <tr>
                            <td><c:out value="${user.id}" /></td>
                            <td><c:out value="${user.username}" /></td>
                            <td><c:out value="${user.firstName} ${user.lastName}" /></td>
                            <td><c:out value="${user.phone}" /></td>
                            <td><c:out value="${user.nationalCode}" /></td>
                            <td>
                                <c:choose>
                                    <c:when test="${user.active}">
                                        <span class="badge bg-success">فعال</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-danger">غیرفعال</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td><fmt:formatDate value="${user.createdAt}" pattern="yyyy/MM/dd HH:mm" /></td>
                            <td>
                                <a href="${pageContext.request.contextPath}/users/detail?id=${user.id}" class="btn btn-sm btn-info me-1">
                                    <i class="fas fa-eye"></i>
                                </a>
                                <a href="${pageContext.request.contextPath}/users/edit?id=${user.id}" class="btn btn-sm btn-warning me-1">
                                    <i class="fas fa-edit"></i>
                                </a>
                                <form action="${pageContext.request.contextPath}/users/delete" method="post" style="display: inline;">
                                    <input type="hidden" name="id" value="${user.id}">
                                    <button type="submit" class="btn btn-sm btn-danger" onclick="return confirm('آیا مطمئن هستید؟');">
                                        <i class="fas fa-trash"></i>
                                    </button>
                                </form>
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
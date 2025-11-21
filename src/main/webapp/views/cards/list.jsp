<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<jsp:include page="/views/common/header.jsp">
    <jsp:param name="title" value="لیست کارت‌ها" />
</jsp:include>

<jsp:include page="/views/common/navbar.jsp" />
<jsp:include page="/views/common/sidebar.jsp" />

<div class="content-wrapper">
    <div class="container-fluid px-4 py-4">
        <h1 class="display-5 fw-bold">لیست کارت‌ها</h1>
        <p class="lead text-muted">مدیریت کارت‌های بانکی</p>

        <!-- پیام‌های موفقیت/خطا -->
        <c:if test="${not empty param.message}">
            <div class="alert alert-success">کارت با موفقیت صادر شد</div>
        </c:if>
        <c:if test="${not empty param.error}">
            <div class="alert alert-danger">
                <c:choose>
                    <c:when test="${param.error == 'missing_id'}">شناسه مشخص نشده</c:when>
                    <c:when test="${param.error == 'not_found'}">کارت یافت نشد</c:when>
                </c:choose>
            </div>
        </c:if>

        <!-- دکمه ایجاد کارت جدید -->
        <div class="mb-4">
            <a href="${pageContext.request.contextPath}/cards/create" class="btn btn-primary">
                <i class="fas fa-plus me-2"></i>صدور کارت جدید
            </a>
        </div>

        <!-- فیلترها (برای ادمین/مدیر) -->
        <c:if test="${sessionScope.roles.contains('ADMIN') || sessionScope.roles.contains('MANAGER')}">
            <div class="card mb-4">
                <div class="card-body">
                    <form action="${pageContext.request.contextPath}/cards/list" method="get">
                        <div class="row">
                            <div class="col-md-4 mb-3">
                                <label for="userId" class="form-label">کاربر</label>
                                <select class="form-select" id="userId" name="userId">
                                    <option value="">همه کاربران</option>
                                    <c:forEach items="${users}" var="user">
                                        <option value="${user.id}" ${selectedUser.id == user.id ? 'selected' : ''}><c:out value="${user.username}" /></option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-4 mb-3">
                                <label for="active" class="form-label">وضعیت</label>
                                <select class="form-select" id="active" name="active">
                                    <option value="">همه</option>
                                    <option value="true" ${selectedActive == true ? 'selected' : ''}>فعال</option>
                                    <option value="false" ${selectedActive == false ? 'selected' : ''}>غیرفعال</option>
                                </select>
                            </div>
                            <div class="col-md-4 mt-4">
                                <button type="submit" class="btn btn-primary">فیلتر</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </c:if>

        <!-- جدول کارت‌ها -->
        <div class="card shadow-lg border-0 rounded-3">
            <div class="card-body">
                <c:if test="${empty cards}">
                    <div class="alert alert-info text-center">
                        <i class="fas fa-info-circle me-2"></i>هیچ کارتی یافت نشد. لطفاً کارت جدیدی صادر کنید.
                    </div>
                </c:if>
                <c:if test="${not empty cards}">
                    <table class="table table-hover table-striped">
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>شماره کارت</th>
                            <th>نوع</th>
                            <th>تاریخ انقضا</th>
                            <th>وضعیت</th>
                            <th>حساب</th>
                            <th>کاربر</th>
                            <th>عملیات</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${cards}" var="card">
                            <tr>
                                <td><c:out value="${card.id}" /></td>
                                <td><c:out value="${card.cardNumber}" /></td>
                                <td><c:out value="${card.type}" /></td>
                                <!-- بعد (درست) -->
                                <fmt:formatDate value="${card.expiryDateAsDate}" pattern="yyyy-MM-dd"/>                                    <c:choose>
                                        <c:when test="${card.active}"><span class="badge bg-success">فعال</span></c:when>
                                        <c:otherwise><span class="badge bg-danger">غیرفعال</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td><c:out value="${card.account.accountNumber}" /></td>
                                <td><c:out value="${card.account.user.username}" /></td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/cards/detail?id=${card.id}" class="btn btn-sm btn-info">
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
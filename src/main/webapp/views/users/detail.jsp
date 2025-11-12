<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<jsp:include page="/views/common/header.jsp">
    <jsp:param name="title" value="جزئیات کاربر" />
</jsp:include>

<jsp:include page="/views/common/navbar.jsp" />
<jsp:include page="/views/common/sidebar.jsp" />

<div class="content-wrapper">
    <div class="container-fluid px-4 py-4">
        <h1 class="display-5 fw-bold">جزئیات کاربر: <c:out value="${user.username}" /></h1>
        <p class="lead text-muted">اطلاعات کامل کاربر</p>

        <!-- پیام‌های موفقیت -->
        <c:if test="${not empty param.message}">
            <div class="alert alert-success">
                <c:choose>
                    <c:when test="${param.message == 'created'}">کاربر ایجاد شد</c:when>
                    <c:when test="${param.message == 'updated'}">به‌روزرسانی موفق</c:when>
                </c:choose>
            </div>
        </c:if>

        <!-- کارت جزئیات -->
        <div class="card shadow-lg border-0 rounded-3">
            <div class="card-body">
                <div class="row">
                    <div class="col-md-6">
                        <h5 class="card-title">اطلاعات شخصی</h5>
                        <ul class="list-group list-group-flush">
                            <li class="list-group-item"><strong>ID:</strong> <c:out value="${user.id}" /></li>
                            <li class="list-group-item"><strong>نام:</strong> <c:out value="${user.firstName}" /></li>
                            <li class="list-group-item"><strong>نام خانوادگی:</strong> <c:out value="${user.lastName}" /></li>
                            <li class="list-group-item"><strong>شماره تلفن:</strong> <c:out value="${user.phone}" /></li>
                            <li class="list-group-item"><strong>کد ملی:</strong> <c:out value="${user.nationalCode}" /></li>
                            <li class="list-group-item"><strong>وضعیت:</strong>
                                <c:choose>
                                    <c:when test="${user.active}">فعال</c:when>
                                    <c:otherwise>غیرفعال</c:otherwise>
                                </c:choose>
                            </li>
                        </ul>
                    </div>
                    <div class="col-md-6">
                        <h5 class="card-title">اطلاعات حساب</h5>
                        <ul class="list-group list-group-flush">
                            <li class="list-group-item"><strong>نام کاربری:</strong> <c:out value="${user.username}" /></li>
                            <li class="list-group-item"><strong>نقش‌ها:</strong>
                                <c:forEach items="${roles}" var="role" varStatus="status">
                                    <span class="badge bg-primary"><c:out value="${role.role}" /></span>
                                    <c:if test="${!status.last}">, </c:if>
                                </c:forEach>
                            </li>
                            <li class="list-group-item"><strong>تاریخ ایجاد:</strong> <fmt:formatDate value="${user.createdAt}" pattern="yyyy/MM/dd HH:mm" /></li>
                            <li class="list-group-item"><strong>آخرین به‌روزرسانی:</strong> <fmt:formatDate value="${user.updatedAt}" pattern="yyyy/MM/dd HH:mm" /></li>
                        </ul>
                    </div>
                </div>
            </div>
            <div class="card-footer">
                <a href="${pageContext.request.contextPath}/users/edit?id=${user.id}" class="btn btn-warning me-2">
                    <i class="fas fa-edit me-2"></i>ویرایش
                </a>
                <a href="${pageContext.request.contextPath}/users/list" class="btn btn-secondary">
                    <i class="fas fa-arrow-right me-2"></i>بازگشت به لیست
                </a>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/views/common/footer.jsp" />
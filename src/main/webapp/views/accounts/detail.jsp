<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<jsp:include page="/views/common/header.jsp">
    <jsp:param name="title" value="جزئیات حساب" />
</jsp:include>

<jsp:include page="/views/common/navbar.jsp" />
<jsp:include page="/views/common/sidebar.jsp" />

<div class="content-wrapper">
    <div class="container-fluid px-4 py-4">
        <h1 class="display-5 fw-bold">جزئیات حساب: <c:out value="${account.accountNumber}" /></h1>
        <p class="lead text-muted">اطلاعات کامل حساب</p>

        <!-- پیام‌های موفقیت -->
        <c:if test="${not empty param.message}">
            <div class="alert alert-success">
                <c:choose>
                    <c:when test="${param.message == 'created'}">حساب ایجاد شد</c:when>
                    <c:when test="${param.message == 'updated'}">به‌روزرسانی موفق</c:when>
                </c:choose>
            </div>
        </c:if>

        <!-- کارت جزئیات -->
        <div class="card shadow-lg border-0 rounded-3">
            <div class="card-body">
                <div class="row">
                    <div class="col-md-6">
                        <h5 class="card-title">اطلاعات حساب</h5>
                        <ul class="list-group list-group-flush">
                            <li class="list-group-item"><strong>ID:</strong> <c:out value="${account.id}" /></li>
                            <li class="list-group-item"><strong>شماره حساب:</strong> <c:out value="${account.accountNumber}" /></li>
                            <li class="list-group-item"><strong>نوع:</strong> <c:out value="${account.type}" /></li>
                            <li class="list-group-item"><strong>موجودی:</strong> <fmt:formatNumber value="${account.balance}" type="currency" currencySymbol="ریال" /></li>
                            <li class="list-group-item"><strong>وضعیت:</strong> <c:out value="${account.status}" /></li>
                        </ul>
                    </div>
                    <div class="col-md-6">
                        <h5 class="card-title">اطلاعات مالک</h5>
                        <ul class="list-group list-group-flush">
                            <li class="list-group-item"><strong>کاربر:</strong> <c:out value="${account.user.username}" /></li>
                            <li class="list-group-item"><strong>نام کامل:</strong> <c:out value="${account.user.firstName} ${account.user.lastName}" /></li>
                            <li class="list-group-item"><strong>تاریخ ایجاد:</strong> <fmt:formatDate value="${account.createdAt}" pattern="yyyy/MM/dd HH:mm" /></li>
                            <li class="list-group-item"><strong>آخرین به‌روزرسانی:</strong> <fmt:formatDate value="${account.updatedAt}" pattern="yyyy/MM/dd HH:mm" /></li>
                        </ul>
                    </div>
                </div>
            </div>
            <div class="card-footer">
                <a href="${pageContext.request.contextPath}/accounts/update?id=${account.id}" class="btn btn-warning me-2">
                    <i class="fas fa-edit me-2"></i>ویرایش
                </a>
                <a href="${pageContext.request.contextPath}/accounts/list" class="btn btn-secondary">
                    <i class="fas fa-arrow-right me-2"></i>بازگشت به لیست
                </a>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/views/common/footer.jsp" />
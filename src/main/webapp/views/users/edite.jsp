<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/views/common/header.jsp">
    <jsp:param name="title" value="ویرایش کاربر" />
</jsp:include>

<jsp:include page="/views/common/navbar.jsp" />
<jsp:include page="/views/common/sidebar.jsp" />

<div class="content-wrapper">
    <div class="container-fluid px-4 py-4">
        <h1 class="display-5 fw-bold">ویرایش کاربر: <c:out value="${user.username}" /></h1>
        <p class="lead text-muted">به‌روزرسانی اطلاعات کاربر</p>

        <!-- پیام خطا -->
        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <!-- فرم ویرایش -->
        <div class="card shadow-lg border-0 rounded-3">
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/users/edit" method="post">
                    <input type="hidden" name="id" value="${user.id}">
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="firstName" class="form-label">نام</label>
                            <input type="text" class="form-control" id="firstName" name="firstName" value="<c:out value='${user.firstName}' />" required>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="lastName" class="form-label">نام خانوادگی</label>
                            <input type="text" class="form-control" id="lastName" name="lastName" value="<c:out value='${user.lastName}' />" required>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="phone" class="form-label">شماره تلفن</label>
                            <input type="text" class="form-control" id="phone" name="phone" value="<c:out value='${user.phone}' />" required pattern="09[0-9]{9}">
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="nationalCode" class="form-label">کد ملی</label>
                            <input type="text" class="form-control" id="nationalCode" name="nationalCode" value="<c:out value='${user.nationalCode}' />" required pattern="[0-9]{10}">
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="password" class="form-label">رمز عبور جدید (اختیاری)</label>
                            <input type="password" class="form-control" id="password" name="password" minlength="6">
                        </div>
                        <div class="col-md-6 mb-3">
                            <div class="form-check mt-4">
                                <input class="form-check-input" type="checkbox" id="active" name="active" ${user.active ? 'checked' : ''}>
                                <label class="form-check-label" for="active">فعال</label>
                            </div>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">نقش‌های فعلی</label>
                        <div>
                            <c:forEach items="${userRoles}" var="role">
                                <span class="badge bg-primary"><c:out value="${role.role}" /></span>
                            </c:forEach>
                        </div>
                    </div>
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-save me-2"></i>ذخیره تغییرات
                    </button>
                    <a href="${pageContext.request.contextPath}/users/detail?id=${user.id}" class="btn btn-secondary ms-2">
                        <i class="fas fa-arrow-right me-2"></i>لغو
                    </a>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/views/common/footer.jsp" />
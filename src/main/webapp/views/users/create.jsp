<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/views/common/header.jsp">
    <jsp:param name="title" value="ایجاد کاربر جدید" />
</jsp:include>

<jsp:include page="/views/common/navbar.jsp" />
<jsp:include page="/views/common/sidebar.jsp" />

<div class="content-wrapper">
    <div class="container-fluid px-4 py-4">
        <h1 class="display-5 fw-bold">ایجاد کاربر جدید</h1>
        <p class="lead text-muted">فرم ثبت کاربر</p>

        <!-- پیام خطا -->
        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <!-- فرم ایجاد -->
        <div class="card shadow-lg border-0 rounded-3">
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/users/create" method="post">
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="username" class="form-label">نام کاربری</label>
                            <input type="text" class="form-control" id="username" name="username" required minlength="4" maxlength="50">
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="password" class="form-label">رمز عبور</label>
                            <input type="password" class="form-control" id="password" name="password" required minlength="6">
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="firstName" class="form-label">نام</label>
                            <input type="text" class="form-control" id="firstName" name="firstName" required>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="lastName" class="form-label">نام خانوادگی</label>
                            <input type="text" class="form-control" id="lastName" name="lastName" required>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="phone" class="form-label">شماره تلفن</label>
                            <input type="text" class="form-control" id="phone" name="phone" required pattern="09[0-9]{9}">
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="nationalCode" class="form-label">کد ملی</label>
                            <input type="text" class="form-control" id="nationalCode" name="nationalCode" required pattern="[0-9]{10}">
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="role" class="form-label">نقش</label>
                            <select class="form-select" id="role" name="role" required>
                                <c:forEach items="${availableRoles}" var="role">
                                    <option value="${role}"><c:out value="${role}" /></option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-6 mb-3">
                            <div class="form-check mt-4">
                                <input class="form-check-input" type="checkbox" id="active" name="active" checked>
                                <label class="form-check-label" for="active">فعال</label>
                            </div>
                        </div>
                    </div>
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-save me-2"></i>ایجاد کاربر
                    </button>
                    <a href="${pageContext.request.contextPath}/users/list" class="btn btn-secondary ms-2">
                        <i class="fas fa-arrow-right me-2"></i>لغو
                    </a>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/views/common/footer.jsp" />
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/views/common/header.jsp">
    <jsp:param name="title" value="پروفایل کاربر" />
</jsp:include>

<jsp:include page="/views/common/navbar.jsp" />
<jsp:include page="/views/common/sidebar.jsp" />

<div class="content-wrapper">
    <div class="container-fluid px-4 py-4">
        <h1 class="display-5 fw-bold">پروفایل: <c:out value="${user.username}" /></h1>
        <p class="lead text-muted">ویرایش اطلاعات شخصی</p>

        <!-- پیام موفقیت/خطا -->
        <c:if test="${not empty success}">
            <div class="alert alert-success">${success}</div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <!-- فرم پروفایل -->
        <div class="card shadow-lg border-0 rounded-3">
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/user-profile" method="post">
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
                    <div class="mb-3">
                        <label for="phone" class="form-label">شماره تلفن</label>
                        <input type="text" class="form-control" id="phone" name="phone" value="<c:out value='${user.phone}' />" required pattern="09[0-9]{9}">
                    </div>
                    <div class="mb-3">
                        <label class="form-label">نقش‌ها</label>
                        <div>
                            <c:forEach items="${roles}" var="role">
                                <span class="badge bg-primary"><c:out value="${role.role}" /></span>
                            </c:forEach>
                        </div>
                    </div>
                    <hr>
                    <h5 class="mb-3">تغییر رمز عبور (اختیاری)</h5>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="oldPassword" class="form-label">رمز عبور فعلی</label>
                            <input type="password" class="form-control" id="oldPassword" name="oldPassword">
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="newPassword" class="form-label">رمز عبور جدید</label>
                            <input type="password" class="form-control" id="newPassword" name="newPassword" minlength="6">
                        </div>
                    </div>
                    <div class="mb-3">
                        <label for="confirmPassword" class="form-label">تکرار رمز عبور جدید</label>
                        <input type="password" class="form-control" id="confirmPassword" name="confirmPassword">
                    </div>
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-save me-2"></i>ذخیره تغییرات
                    </button>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/views/common/footer.jsp" />
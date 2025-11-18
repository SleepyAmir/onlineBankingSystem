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
            <div class="alert alert-danger alert-dismissible fade show">
                <i class="fas fa-exclamation-triangle me-2"></i>${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <!-- فرم ویرایش -->
        <div class="card shadow-lg border-0 rounded-3">
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/users/edit" method="post">
                    <input type="hidden" name="id" value="${user.id}">

                    <h5 class="mb-3">
                        <i class="fas fa-user me-2"></i>اطلاعات شخصی
                    </h5>

                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="firstName" class="form-label">نام</label>
                            <input type="text" class="form-control" id="firstName" name="firstName"
                                   value="<c:out value='${user.firstName}' />" required>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="lastName" class="form-label">نام خانوادگی</label>
                            <input type="text" class="form-control" id="lastName" name="lastName"
                                   value="<c:out value='${user.lastName}' />" required>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="phone" class="form-label">شماره تلفن</label>
                            <input type="text" class="form-control" id="phone" name="phone"
                                   value="<c:out value='${user.phone}' />" required pattern="09[0-9]{9}">
                            <small class="text-muted">فرمت: 09123456789</small>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="nationalCode" class="form-label">کد ملی</label>
                            <input type="text" class="form-control" id="nationalCode" name="nationalCode"
                                   value="<c:out value='${user.nationalCode}' />" required pattern="[0-9]{10}">
                            <small class="text-muted">10 رقم</small>
                        </div>
                    </div>

                    <hr class="my-4">

                    <h5 class="mb-3">
                        <i class="fas fa-key me-2"></i>تغییر رمز عبور
                    </h5>

                    <div class="alert alert-info">
                        <i class="fas fa-info-circle me-2"></i>
                        فقط در صورتی که می‌خواهید رمز عبور را تغییر دهید، فیلد زیر را پر کنید.
                    </div>

                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="password" class="form-label">رمز عبور جدید (اختیاری)</label>
                            <input type="password" class="form-control" id="password" name="password" minlength="6">
                            <small class="text-muted">حداقل 6 کاراکتر - خالی بگذارید اگر نمی‌خواهید تغییر دهید</small>
                        </div>
                        <div class="col-md-6 mb-3">
                            <div class="form-check mt-4 pt-2">
                                <input class="form-check-input" type="checkbox" id="active" name="active"
                                ${user.active ? 'checked' : ''}>
                                <label class="form-check-label" for="active">
                                    <i class="fas fa-toggle-on me-1"></i>حساب فعال
                                </label>
                            </div>
                        </div>
                    </div>

                    <hr class="my-4">

                    <h5 class="mb-3">
                        <i class="fas fa-shield-alt me-2"></i>نقش‌های کاربر
                    </h5>

                    <div class="mb-3">
                        <label class="form-label">نقش‌های فعلی</label>
                        <div>
                            <c:forEach items="${userRoles}" var="role">
                                <span class="badge bg-primary me-1" style="font-size: 1rem; padding: 0.5rem 1rem;">
                                    <i class="fas fa-user-tag me-1"></i>
                                    <c:out value="${role.role}" />
                                </span>
                            </c:forEach>
                        </div>
                    </div>

                    <div class="d-flex gap-2 mt-4">
                        <button type="submit" class="btn btn-primary btn-lg flex-fill">
                            <i class="fas fa-save me-2"></i>ذخیره تغییرات
                        </button>
                        <a href="${pageContext.request.contextPath}/users/detail?id=${user.id}"
                           class="btn btn-secondary btn-lg flex-fill">
                            <i class="fas fa-times me-2"></i>لغو
                        </a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/views/common/footer.jsp" />

<script>
    // اعتبارسنجی فرم
    document.querySelector('form').addEventListener('submit', function(e) {
        const password = document.getElementById('password').value;

        // اگر رمز عبور جدید وارد شده، بررسی طول
        if (password && password.length < 6) {
            e.preventDefault();
            alert('رمز عبور باید حداقل 6 کاراکتر باشد');
            return;
        }
    });

    // اعتبارسنجی شماره تلفن
    document.getElementById('phone').addEventListener('input', function() {
        this.value = this.value.replace(/[^0-9]/g, '');
    });

    // اعتبارسنجی کد ملی
    document.getElementById('nationalCode').addEventListener('input', function() {
        this.value = this.value.replace(/[^0-9]/g, '');
        if (this.value.length > 10) {
            this.value = this.value.substring(0, 10);
        }
    });
</script>
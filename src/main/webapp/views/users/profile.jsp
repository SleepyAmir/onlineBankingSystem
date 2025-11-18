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
            <div class="alert alert-success alert-dismissible fade show">
                <i class="fas fa-check-circle me-2"></i>${success}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show">
                <i class="fas fa-exclamation-triangle me-2"></i>${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <!-- فرم پروفایل -->
        <div class="card shadow-lg border-0 rounded-3">
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/user-profile" method="post">
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="firstName" class="form-label">
                                <i class="fas fa-user me-2"></i>نام
                            </label>
                            <input type="text" class="form-control" id="firstName" name="firstName"
                                   value="<c:out value='${user.firstName}' />" required>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="lastName" class="form-label">
                                <i class="fas fa-user me-2"></i>نام خانوادگی
                            </label>
                            <input type="text" class="form-control" id="lastName" name="lastName"
                                   value="<c:out value='${user.lastName}' />" required>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label for="phone" class="form-label">
                            <i class="fas fa-phone me-2"></i>شماره تلفن
                        </label>
                        <input type="text" class="form-control" id="phone" name="phone"
                               value="<c:out value='${user.phone}' />" required pattern="09[0-9]{9}">
                        <small class="text-muted">فرمت: 09123456789</small>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">
                            <i class="fas fa-shield-alt me-2"></i>نقش‌ها
                        </label>
                        <div>
                            <c:forEach items="${roles}" var="role">
                                <span class="badge bg-primary me-1">
                                    <c:out value="${role.role}" />
                                </span>
                            </c:forEach>
                        </div>
                    </div>

                    <hr class="my-4">

                    <h5 class="mb-3">
                        <i class="fas fa-key me-2"></i>تغییر رمز عبور (اختیاری)
                    </h5>
                    <div class="alert alert-info">
                        <i class="fas fa-info-circle me-2"></i>
                        فقط در صورت نیاز به تغییر رمز عبور، فیلدهای زیر را پر کنید.
                    </div>

                    <div class="row">
                        <div class="col-md-4 mb-3">
                            <label for="currentPassword" class="form-label">رمز عبور فعلی</label>
                            <input type="password" class="form-control" id="currentPassword" name="currentPassword">
                        </div>
                        <div class="col-md-4 mb-3">
                            <label for="newPassword" class="form-label">رمز عبور جدید</label>
                            <input type="password" class="form-control" id="newPassword" name="newPassword" minlength="6">
                            <small class="text-muted">حداقل 6 کاراکتر</small>
                        </div>
                        <div class="col-md-4 mb-3">
                            <label for="confirmPassword" class="form-label">تکرار رمز عبور جدید</label>
                            <input type="password" class="form-control" id="confirmPassword" name="confirmPassword">
                        </div>
                    </div>

                    <div class="d-flex gap-2 mt-4">
                        <button type="submit" class="btn btn-primary btn-lg flex-fill">
                            <i class="fas fa-save me-2"></i>ذخیره تغییرات
                        </button>
                        <a href="${pageContext.request.contextPath}/user-dashboard"
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
        const newPassword = document.getElementById('newPassword').value;
        const confirmPassword = document.getElementById('confirmPassword').value;
        const currentPassword = document.getElementById('currentPassword').value;

        // اگر می‌خواهد رمز عبور را تغییر دهد
        if (newPassword || confirmPassword || currentPassword) {
            // باید هر سه فیلد پر شوند
            if (!currentPassword || !newPassword || !confirmPassword) {
                e.preventDefault();
                alert('برای تغییر رمز عبور، باید هر سه فیلد را پر کنید');
                return;
            }

            // بررسی تطابق رمز جدید
            if (newPassword !== confirmPassword) {
                e.preventDefault();
                alert('رمز عبور جدید و تکرار آن یکسان نیستند');
                return;
            }

            // حداقل طول رمز عبور
            if (newPassword.length < 6) {
                e.preventDefault();
                alert('رمز عبور جدید باید حداقل 6 کاراکتر باشد');
                return;
            }
        }
    });

    // اعتبارسنجی شماره تلفن
    document.getElementById('phone').addEventListener('input', function() {
        this.value = this.value.replace(/[^0-9]/g, '');
    });
</script>
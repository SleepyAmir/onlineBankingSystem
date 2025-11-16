<!-- accounts/create.jsp -->

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/views/common/header.jsp">
    <jsp:param name="title" value="ایجاد حساب جدید" />
</jsp:include>

<jsp:include page="/views/common/navbar.jsp" />
<jsp:include page="/views/common/sidebar.jsp" />

<div class="content-wrapper">
    <div class="container-fluid px-4 py-4">
        <h1 class="display-5 fw-bold">ایجاد حساب جدید</h1>
        <p class="lead text-muted">فرم ثبت حساب بانکی</p>

        <!-- پیام خطا -->
        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show">
                <i class="fas fa-exclamation-triangle me-2"></i>${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <!-- پیام اطلاعاتی برای کاربر عادی -->
        <c:if test="${!sessionScope.roles.contains('ADMIN') && !sessionScope.roles.contains('MANAGER')}">
            <div class="alert alert-info">
                <i class="fas fa-info-circle me-2"></i>
                <strong>توجه:</strong> حساب با موجودی صفر ایجاد می‌شود. برای واریز وجه به صفحه تراکنش‌ها مراجعه کنید.
            </div>
        </c:if>

        <!-- فرم ایجاد -->
        <div class="card shadow-lg border-0 rounded-3">
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/accounts/create" method="post">

                    <!-- انتخاب کاربر (فقط برای ادمین/مدیر) -->
                    <c:if test="${not empty users}">
                        <div class="mb-3">
                            <label for="userId" class="form-label">
                                <i class="fas fa-user me-2"></i>کاربر
                            </label>
                            <select class="form-select" id="userId" name="userId">
                                <option value="">حساب برای خودم</option>
                                <c:forEach items="${users}" var="user">
                                    <option value="${user.id}">
                                        <c:out value="${user.username} - ${user.firstName} ${user.lastName}" />
                                    </option>
                                </c:forEach>
                            </select>
                            <small class="text-muted">اگر انتخاب نکنید، حساب برای خودتان ایجاد می‌شود</small>
                        </div>
                    </c:if>

                    <!-- نوع حساب -->
                    <div class="mb-3">
                        <label for="accountType" class="form-label">
                            <i class="fas fa-wallet me-2"></i>نوع حساب <span class="text-danger">*</span>
                        </label>
                        <select class="form-select" id="accountType" name="accountType" required>
                            <option value="">انتخاب کنید</option>
                            <c:forEach items="${accountTypes}" var="type">
                                <option value="${type}">
                                    <c:choose>
                                        <c:when test="${type == 'SAVINGS'}">حساب پس‌انداز</c:when>
                                        <c:when test="${type == 'CHECKING'}">حساب جاری</c:when>
                                        <c:otherwise><c:out value="${type}" /></c:otherwise>
                                    </c:choose>
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <!-- موجودی اولیه (فقط برای ادمین) -->
                    <c:if test="${sessionScope.roles.contains('ADMIN')}">
                        <div class="mb-3">
                            <label for="initialBalance" class="form-label">
                                <i class="fas fa-money-bill-wave me-2"></i>موجودی اولیه (اختیاری - فقط ادمین)
                            </label>
                            <input type="number"
                                   class="form-control"
                                   id="initialBalance"
                                   name="initialBalance"
                                   min="0"
                                   step="1000"
                                   placeholder="0">
                            <small class="text-muted">
                                اگر خالی بگذارید، حساب با موجودی صفر ایجاد می‌شود
                            </small>
                        </div>
                    </c:if>

                    <!-- توضیحات اضافی -->
                    <div class="alert alert-light border">
                        <h6><i class="fas fa-lightbulb me-2"></i>راهنما:</h6>
                        <ul class="mb-0">
                            <li><strong>حساب پس‌انداز:</strong> برای ذخیره وجه با سود بانکی</li>
                            <li><strong>حساب جاری:</strong> برای تراکنش‌های روزمره</li>
                            <c:if test="${!sessionScope.roles.contains('ADMIN')}">
                                <li><strong>موجودی اولیه:</strong> صفر (برای واریز به صفحه تراکنش‌ها مراجعه کنید)</li>
                            </c:if>
                        </ul>
                    </div>

                    <!-- دکمه‌ها -->
                    <div class="d-flex gap-2">
                        <button type="submit" class="btn btn-primary flex-fill">
                            <i class="fas fa-check me-2"></i>ایجاد حساب
                        </button>
                        <a href="${pageContext.request.contextPath}/accounts/list"
                           class="btn btn-secondary flex-fill">
                            <i class="fas fa-times me-2"></i>لغو
                        </a>
                    </div>
                </form>
            </div>
        </div>

        <!-- کارت راهنما -->
        <div class="card mt-4 border-info">
            <div class="card-header bg-info text-white">
                <i class="fas fa-question-circle me-2"></i>مراحل بعدی
            </div>
            <div class="card-body">
                <ol>
                    <li>پس از ایجاد حساب، به صفحه تراکنش‌ها بروید</li>
                    <li>گزینه "واریز وجه" را انتخاب کنید</li>
                    <li>مبلغ دلخواه را واریز کنید</li>
                    <li>می‌توانید از خدمات بانکی استفاده کنید</li>
                </ol>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/views/common/footer.jsp" />
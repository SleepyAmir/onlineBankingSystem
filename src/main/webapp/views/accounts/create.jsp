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
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <!-- فرم ایجاد -->
        <div class="card shadow-lg border-0 rounded-3">
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/accounts/create" method="post">
                    <c:if test="${not empty users}">
                        <div class="mb-3">
                            <label for="userId" class="form-label">کاربر</label>
                            <select class="form-select" id="userId" name="userId" required>
                                <option value="">انتخاب کاربر</option>
                                <c:forEach items="${users}" var="user">
                                    <option value="${user.id}"><c:out value="${user.username} - ${user.firstName} ${user.lastName}" /></option>
                                </c:forEach>
                            </select>
                        </div>
                    </c:if>
                    <div class="mb-3">
                        <label for="accountType" class="form-label">نوع حساب</label>
                        <select class="form-select" id="accountType" name="accountType" required>
                            <c:forEach items="${accountTypes}" var="type">
                                <option value="${type}"><c:out value="${type}" /></option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label for="initialBalance" class="form-label">موجودی اولیه (اختیاری)</label>
                        <input type="number" class="form-control" id="initialBalance" name="initialBalance" min="0" step="any">
                    </div>
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-save me-2"></i>ایجاد حساب
                    </button>
                    <a href="${pageContext.request.contextPath}/accounts/list" class="btn btn-secondary ms-2">
                        <i class="fas fa-arrow-right me-2"></i>لغو
                    </a>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/views/common/footer.jsp" />
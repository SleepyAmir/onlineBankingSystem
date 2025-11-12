<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<jsp:include page="/views/common/header.jsp">
    <jsp:param name="title" value="به‌روزرسانی حساب" />
</jsp:include>

<jsp:include page="/views/common/navbar.jsp" />
<jsp:include page="//views/common/sidebar.jsp" />

<div class="content-wrapper">
    <div class="container-fluid px-4 py-4">
        <h1 class="display-5 fw-bold">به‌روزرسانی حساب: <c:out value="${account.accountNumber}" /></h1>
        <p class="lead text-muted">ویرایش اطلاعات حساب</p>

        <!-- پیام خطا -->
        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <!-- فرم به‌روزرسانی -->
        <div class="card shadow-lg border-0 rounded-3">
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/accounts/update" method="post">
                    <input type="hidden" name="id" value="${account.id}">
                    <div class="mb-3">
                        <label for="accountType" class="form-label">نوع حساب</label>
                        <select class="form-select" id="accountType" name="accountType" required>
                            <c:forEach items="${accountTypes}" var="type">
                                <option value="${type}" ${account.type == type ? 'selected' : ''}><c:out value="${type}" /></option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label for="accountStatus" class="form-label">وضعیت حساب</label>
                        <select class="form-select" id="accountStatus" name="accountStatus" required>
                            <c:forEach items="${accountStatuses}" var="status">
                                <option value="${status}" ${account.status == status ? 'selected' : ''}><c:out value="${status}" /></option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label for="balance" class="form-label">موجودی (فقط ادمین)</label>
                        <input type="number" class="form-control" id="balance" name="balance" value="${account.balance}" min="0" step="any">
                    </div>
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-save me-2"></i>ذخیره تغییرات
                    </button>
                    <a href="${pageContext.request.contextPath}/accounts/detail?id=${account.id}" class="btn btn-secondary ms-2">
                        <i class="fas fa-arrow-right me-2"></i>لغو
                    </a>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/views/common/footer.jsp" />
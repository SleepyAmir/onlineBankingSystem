<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<jsp:include page="/views/common/header.jsp">
    <jsp:param name="title" value="صدور کارت جدید" />
</jsp:include>

<jsp:include page="/views/common/navbar.jsp" />
<jsp:include page="/views/common/sidebar.jsp" />

<div class="content-wrapper">
    <div class="container-fluid px-4 py-4">
        <h1 class="display-5 fw-bold">صدور کارت جدید</h1>
        <p class="lead text-muted">فرم صدور کارت بانکی</p>

        <!-- پیام خطا -->
        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <!-- فرم صدور -->
        <div class="card shadow-lg border-0 rounded-3">
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/cards/create" method="post">
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
                        <label for="accountId" class="form-label">حساب</label>
                        <select class="form-select" id="accountId" name="accountId" required>
                            <option value="">انتخاب حساب</option>
                            <c:forEach items="${accounts}" var="account">
                                <option value="${account.id}"><c:out value="${account.accountNumber} - ${account.type}" /></option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label for="cardType" class="form-label">نوع کارت</label>
                        <select class="form-select" id="cardType" name="cardType" required>
                            <c:forEach items="${cardTypes}" var="type">
                                <option value="${type}"><c:out value="${type}" /></option>
                            </c:forEach>
                        </select>
                    </div>
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-credit-card me-2"></i>صدور کارت
                    </button>
                    <a href="${pageContext.request.contextPath}/cards/list" class="btn btn-secondary ms-2">
                        <i class="fas fa-arrow-right me-2"></i>لغو
                    </a>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/views/common/footer.jsp" />
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/views/common/header.jsp">
    <jsp:param name="title" value="پرداخت قسط وام" />
</jsp:include>

<jsp:include page="/views/common/navbar.jsp" />
<jsp:include page="/views/common/sidebar.jsp" />

<div class="content-wrapper">
    <div class="container-fluid px-4 py-4">
        <h1 class="display-5 fw-bold">پرداخت قسط وام: <c:out value="${loan.loanNumber}" /></h1>
        <p class="lead text-muted">فرم پرداخت</p>

        <!-- پیام خطا -->
        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <!-- اطلاعات وام -->
        <div class="card mb-4">
            <div class="card-body">
                <p><strong>قسط ماهانه:</strong> <fmt:formatNumber value="${loan.monthlyPayment}" type="currency" currencySymbol="ریال" /></p>
                <p><strong>بدهی باقی‌مانده:</strong> <fmt:formatNumber value="${loan.remainingBalance}" type="currency" currencySymbol="ریال" /></p>
            </div>
        </div>

        <!-- فرم پرداخت -->
        <div class="card shadow-lg border-0 rounded-3">
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/loans/payment" method="post">
                    <input type="hidden" name="id" value="${loan.id}">
                    <div class="mb-3">
                        <label for="paymentAmount" class="form-label">مبلغ پرداخت (ریال)</label>
                        <input type="number" class="form-control" id="paymentAmount" name="paymentAmount" required min="1">
                    </div>
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-money-check-alt me-2"></i>پرداخت
                    </button>
                    <a href="${pageContext.request.contextPath}/loans/detail?id=${loan.id}" class="btn btn-secondary ms-2">
                        <i class="fas fa-arrow-right me-2"></i>لغو
                    </a>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/views/common/footer.jsp" />
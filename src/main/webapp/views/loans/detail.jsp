<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/views/common/header.jsp">
    <jsp:param name="title" value="جزئیات وام" />
</jsp:include>

<jsp:include page="/views/common/navbar.jsp" />
<jsp:include page="/views/common/sidebar.jsp" />

<div class="content-wrapper">
    <div class="container-fluid px-4 py-4">
        <h1 class="display-5 fw-bold">جزئیات وام: <c:out value="${loan.loanNumber}" /></h1>
        <p class="lead text-muted">اطلاعات کامل وام</p>

        <!-- پیام‌های موفقیت/خطا -->
        <c:if test="${not empty param.message}">
            <div class="alert alert-success">
                <c:choose>
                    <c:when test="${param.message == 'approved'}">وام تأیید شد</c:when>
                    <c:when test="${param.message == 'payment_success'}">پرداخت موفق</c:when>
                </c:choose>
            </div>
        </c:if>
        <c:if test="${not empty param.error}">
            <div class="alert alert-danger">${param.error}</div>
        </c:if>

        <!-- کارت جزئیات -->
        <div class="card shadow-lg border-0 rounded-3">
            <div class="card-body">
                <div class="row">
                    <div class="col-md-6">
                        <h5>اطلاعات وام</h5>
                        <ul class="list-group list-group-flush">
                            <li class="list-group-item"><strong>مبلغ اصل:</strong> <fmt:formatNumber value="${loan.principal}" type="currency" currencySymbol="ریال" /></li>
                            <li class="list-group-item"><strong>نرخ بهره:</strong> <fmt:formatNumber value="${loan.interestRate}" type="percent" /></li>
                            <li class="list-group-item"><strong>مدت (ماه):</strong> <c:out value="${loan.durationMonths}" /></li>
                            <li class="list-group-item"><strong>قسط ماهانه:</strong> <fmt:formatNumber value="${loan.monthlyPayment}" type="currency" currencySymbol="ریال" /></li>
                            <li class="list-group-item"><strong>وضعیت:</strong> <c:out value="${loan.status}" /></li>
                        </ul>
                    </div>
                    <div class="col-md-6">
                        <h5>اطلاعات مالک</h5>
                        <ul class="list-group list-group-flush">
                            <li class="list-group-item"><strong>کاربر:</strong> <c:out value="${loan.user.username}" /></li>
                            <li class="list-group-item"><strong>حساب:</strong> <c:out value="${loan.account.accountNumber}" /></li>
                            <li class="list-group-item"><strong>تاریخ درخواست:</strong> <fmt:formatDate value="${loan.createdAt}" pattern="yyyy/MM/dd" /></li>
                            <li class="list-group-item"><strong>تاریخ سررسید:</strong> <fmt:formatDate value="${loan.dueDate}" pattern="yyyy/MM/dd" /></li>
                        </ul>
                    </div>
                </div>
            </div>
            <div class="card-footer">
                <c:if test="${loan.status == 'APPROVED' || loan.status == 'ACTIVE'}">
                    <a href="${pageContext.request.contextPath}/loans/payment?id=${loan.id}" class="btn btn-primary me-2">
                        <i class="fas fa-money-bill-wave me-2"></i>پرداخت قسط
                    </a>
                </c:if>
                <a href="${pageContext.request.contextPath}/loans/list" class="btn btn-secondary">
                    <i class="fas fa-arrow-right me-2"></i>بازگشت به لیست
                </a>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/views/common/footer.jsp" />
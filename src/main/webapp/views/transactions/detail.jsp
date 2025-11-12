<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/views/common/header.jsp">
    <jsp:param name="title" value="جزئیات تراکنش" />
</jsp:include>

<jsp:include page="/views/common/navbar.jsp" />
<jsp:include page="/views/common/sidebar.jsp" />

<div class="content-wrapper">
    <div class="container-fluid px-4 py-4">
        <h1 class="display-5 fw-bold">جزئیات تراکنش: <c:out value="${transaction.transactionId}" /></h1>
        <p class="lead text-muted">اطلاعات کامل تراکنش</p>

        <!-- پیام موفقیت -->
        <c:if test="${not empty param.message}">
            <div class="alert alert-success">انتقال با موفقیت انجام شد</div>
        </c:if>

        <!-- کارت جزئیات -->
        <div class="card shadow-lg border-0 rounded-3">
            <div class="card-body">
                <ul class="list-group list-group-flush">
                    <li class="list-group-item"><strong>نوع:</strong> <c:out value="${transaction.type}" /></li>
                    <li class="list-group-item"><strong>مبلغ:</strong> <fmt:formatNumber value="${transaction.amount}" type="currency" currencySymbol="ریال" /></li>
                    <li class="list-group-item"><strong>از حساب:</strong> <c:out value="${transaction.fromAccount != null ? transaction.fromAccount.accountNumber : '-'}" /></li>
                    <li class="list-group-item"><strong>به حساب:</strong> <c:out value="${transaction.toAccount != null ? transaction.toAccount.accountNumber : '-'}" /></li>
                    <li class="list-group-item"><strong>تاریخ:</strong> <fmt:formatDate value="${transaction.transactionDate}" pattern="yyyy/MM/dd HH:mm" /></li>
                    <li class="list-group-item"><strong>وضعیت:</strong> <c:out value="${transaction.status}" /></li>
                    <li class="list-group-item"><strong>توضیحات:</strong> <c:out value="${transaction.description}" /></li>
                    <li class="list-group-item"><strong>شماره مرجع:</strong> <c:out value="${transaction.referenceNumber}" /></li>
                </ul>
            </div>
            <div class="card-footer">
                <a href="${pageContext.request.contextPath}/transactions/history" class="btn btn-secondary">
                    <i class="fas fa-arrow-right me-2"></i>بازگشت به تاریخچه
                </a>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/views/common/footer.jsp" />
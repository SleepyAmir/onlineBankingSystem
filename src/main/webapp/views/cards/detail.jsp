<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<jsp:include page="/views/common/header.jsp">
    <jsp:param name="title" value="جزئیات کارت" />
</jsp:include>

<jsp:include page="/views/common/navbar.jsp" />
<jsp:include page="/views/common/sidebar.jsp" />

<div class="content-wrapper">
    <div class="container-fluid px-4 py-4">
        <h1 class="display-5 fw-bold">جزئیات کارت: **** **** **** <c:out value="${fn:substring(card.cardNumber, 12, 16)}" /></h1>
        <p class="lead text-muted">اطلاعات کامل کارت</p>

        <!-- پیام موفقیت -->
        <c:if test="${not empty param.message}">
            <div class="alert alert-success">کارت صادر شد</div>
        </c:if>

        <!-- کارت جزئیات -->
        <div class="card shadow-lg border-0 rounded-3">
            <div class="card-body">
                <div class="row">
                    <div class="col-md-6">
                        <h5 class="card-title">اطلاعات کارت</h5>
                        <ul class="list-group list-group-flush">
                            <li class="list-group-item"><strong>ID:</strong> <c:out value="${card.id}" /></li>
                            <li class="list-group-item"><strong>شماره کارت:</strong> **** **** **** <c:out value="${fn:substring(card.cardNumber, 12, 16)}" /></li>
                            <li class="list-group-item"><strong>CVV:</strong> <c:out value="${card.cvv}" /></li>
                            <li class="list-group-item"><strong>تاریخ انقضا:</strong> <fmt:formatDate value="${card.expiryDate}" pattern="yyyy/MM/dd" /></li>
                            <li class="list-group-item"><strong>نوع:</strong> <c:out value="${card.type}" /></li>
                            <li class="list-group-item"><strong>وضعیت:</strong>
                                <c:choose>
                                    <c:when test="${card.active}">فعال</c:when>
                                    <c:otherwise>غیرفعال</c:otherwise>
                                </c:choose>
                            </li>
                        </ul>
                    </div>
                    <div class="col-md-6">
                        <h5 class="card-title">اطلاعات حساب</h5>
                        <ul class="list-group list-group-flush">
                            <li class="list-group-item"><strong>حساب:</strong> <c:out value="${card.account.accountNumber}" /></li>
                            <li class="list-group-item"><strong>کاربر:</strong> <c:out value="${card.account.user.username}" /></li>
                            <li class="list-group-item"><strong>تاریخ ایجاد:</strong> <fmt:formatDate value="${card.createdAt}" pattern="yyyy/MM/dd HH:mm" /></li>
                        </ul>
                    </div>
                </div>
            </div>
            <div class="card-footer">
                <a href="${pageContext.request.contextPath}/cards/list" class="btn btn-secondary">
                    <i class="fas fa-arrow-right me-2"></i>بازگشت به لیست
                </a>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/views/common/footer.jsp" />
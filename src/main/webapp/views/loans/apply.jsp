<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/views/common/header.jsp">
    <jsp:param name="title" value="درخواست وام" />
</jsp:include>

<jsp:include page="/views/common/navbar.jsp" />
<jsp:include page="/views/common/sidebar.jsp" />

<div class="content-wrapper">
    <div class="container-fluid px-4 py-4">
        <h1 class="display-5 fw-bold">درخواست وام جدید</h1>
        <p class="lead text-muted">فرم ثبت درخواست وام</p>

        <!-- پیام خطا -->
        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <!-- فرم درخواست -->
        <div class="card shadow-lg border-0 rounded-3">
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/loans/apply" method="post">
                    <div class="mb-3">
                        <label for="accountId" class="form-label">حساب</label>
                        <select class="form-select" id="accountId" name="accountId" required>
                            <option value="">انتخاب حساب</option>
                            <c:forEach items="${activeAccounts}" var="account">
                                <option value="${account.id}"><c:out value="${account.accountNumber} - موجودی: ${account.balance}" /></option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label for="principal" class="form-label">مبلغ وام (ریال)</label>
                        <input type="number" class="form-control" id="principal" name="principal" required min="1000000" max="10000000000">
                    </div>
                    <div class="mb-3">
                        <label for="interestRate" class="form-label">نرخ بهره سالانه (%)</label>
                        <input type="number" class="form-control" id="interestRate" name="interestRate" required min="0" max="30" step="0.1">
                    </div>
                    <div class="mb-3">
                        <label for="duration" class="form-label">مدت وام (ماه)</label>
                        <input type="number" class="form-control" id="duration" name="duration" required min="1" max="360">
                    </div>
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-hand-holding-usd me-2"></i>ثبت درخواست
                    </button>
                    <a href="${pageContext.request.contextPath}/loans/list" class="btn btn-secondary ms-2">
                        <i class="fas fa-arrow-right me-2"></i>لغو
                    </a>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/views/common/footer.jsp" />
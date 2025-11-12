<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<jsp:include page="/views/common/header.jsp">
    <jsp:param name="title" value="انتقال وجه" />
</jsp:include>

<jsp:include page="/views/common/navbar.jsp" />
<jsp:include page="/views/common/sidebar.jsp" />

<div class="content-wrapper">
    <div class="container-fluid px-4 py-4">
        <h1 class="display-5 fw-bold">انتقال وجه</h1>
        <p class="lead text-muted">فرم انجام تراکنش</p>

        <!-- پیام خطا -->
        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <!-- فرم انتقال -->
        <div class="card shadow-lg border-0 rounded-3">
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/transactions" method="post">
                    <div class="mb-3">
                        <label for="fromAccountId" class="form-label">حساب مبدأ</label>
                        <select class="form-select" id="fromAccountId" name="fromAccountId" required>
                            <option value="">انتخاب حساب</option>
                            <c:forEach items="${userAccounts}" var="account">
                                <option value="${account.id}"><c:out value="${account.accountNumber} - موجودی: ${account.balance}" /></option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label for="toAccountNumber" class="form-label">شماره حساب مقصد</label>
                        <input type="text" class="form-control" id="toAccountNumber" name="toAccountNumber" required pattern="[0-9]{16}">
                    </div>
                    <div class="mb-3">
                        <label for="amount" class="form-label">مبلغ (ریال)</label>
                        <input type="number" class="form-control" id="amount" name="amount" required min="1">
                    </div>
                    <div class="mb-3">
                        <label for="description" class="form-label">توضیحات (اختیاری)</label>
                        <textarea class="form-control" id="description" name="description"></textarea>
                    </div>
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-exchange-alt me-2"></i>انجام انتقال
                    </button>
                    <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-secondary ms-2">
                        <i class="fas fa-arrow-right me-2"></i>لغو
                    </a>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/views/common/footer.jsp" />
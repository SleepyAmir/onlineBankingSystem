<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/views/common/header.jsp">
    <jsp:param name="title" value="خطا" />
</jsp:include>

<jsp:include page="/views/common/navbar.jsp" />
<jsp:include page="/views/common/sidebar.jsp" />

<div class="content-wrapper">
    <div class="container-fluid px-4 py-4 text-center">
        <i class="fas fa-exclamation-triangle fa-5x text-danger mb-4"></i>
        <h1 class="display-4 fw-bold">خطایی رخ داده است!</h1>
        <p class="lead text-muted">
            <c:choose>
                <c:when test="${not empty error}">
                    ${error}
                </c:when>
                <c:otherwise>
                    لطفاً بعداً امتحان کنید یا با پشتیبانی تماس بگیرید.
                </c:otherwise>
            </c:choose>
        </p>
        <a href="${pageContext.request.contextPath}/" class="btn btn-primary mt-3">
            <i class="fas fa-home me-2"></i>بازگشت به صفحه اصلی
        </a>
    </div>
</div>

<jsp:include page="/views/common/footer.jsp" />
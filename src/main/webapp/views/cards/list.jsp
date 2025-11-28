<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/views/common/header.jsp">
    <jsp:param name="title" value="لیست کارت‌ها" />
</jsp:include>

<jsp:include page="/views/common/navbar.jsp" />
<jsp:include page="/views/common/sidebar.jsp" />

<div class="content-wrapper">
    <div class="container-fluid px-4 py-4">
        <h1 class="display-5 fw-bold">لیست کارت‌ها</h1>
        <p class="lead text-muted">مدیریت کارت‌های بانکی</p>

        <!-- پیام‌های موفقیت/خطا -->
        <c:if test="${not empty param.message}">
            <div class="alert alert-success">کارت با موفقیت صادر شد</div>
        </c:if>
        <c:if test="${not empty param.error}">
            <div class="alert alert-danger">
                <c:choose>
                    <c:when test="${param.error == 'missing_id'}">شناسه مشخص نشده</c:when>
                    <c:when test="${param.error == 'not_found'}">کارت یافت نشد</c:when>
                    <c:otherwise>خطا در نمایش کارت‌ها</c:otherwise>
                </c:choose>
            </div>
        </c:if>

        <!-- دکمه ایجاد کارت جدید -->
        <div class="mb-4">
            <a href="${pageContext.request.contextPath}/cards/create" class="btn btn-primary">
                <i class="fas fa-plus me-2"></i>صدور کارت جدید
            </a>
        </div>

        <!-- فیلترها (برای ادمین/مدیر) -->
        <c:if test="${sessionScope.roles.contains('ADMIN') || sessionScope.roles.contains('MANAGER')}">
            <div class="card mb-4">
                <div class="card-body">
                    <form action="${pageContext.request.contextPath}/cards/list" method="get">
                        <div class="row">
                            <div class="col-md-4 mb-3">
                                <label for="userId" class="form-label">کاربر</label>
                                <select class="form-select" id="userId" name="userId">
                                    <option value="">همه کاربران</option>
                                    <c:forEach items="${users}" var="user">
                                        <option value="${user.id}" ${param.userId == user.id ? 'selected' : ''}>
                                            <c:out value="${user.username}" />
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-4 mb-3">
                                <label for="active" class="form-label">وضعیت</label>
                                <select class="form-select" id="active" name="active">
                                    <option value="">همه</option>
                                    <option value="true" ${param.active == 'true' ? 'selected' : ''}>فعال</option>
                                    <option value="false" ${param.active == 'false' ? 'selected' : ''}>غیرفعال</option>
                                </select>
                            </div>
                            <div class="col-md-4 mt-4">
                                <button type="submit" class="btn btn-primary">
                                    <i class="fas fa-filter me-2"></i>فیلتر
                                </button>
                                <a href="${pageContext.request.contextPath}/cards/list" class="btn btn-secondary">
                                    <i class="fas fa-redo me-2"></i>پاک کردن
                                </a>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </c:if>

        <!-- جدول کارت‌ها -->
        <div class="card shadow-lg border-0 rounded-3">
            <div class="card-body">
                <c:choose>
                    <c:when test="${empty cards}">
                        <div class="alert alert-info text-center">
                            <i class="fas fa-info-circle me-2"></i>هیچ کارتی یافت نشد. لطفاً کارت جدیدی صادر کنید.
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-hover table-striped">
                                <thead class="table-dark">
                                <tr>
                                    <th>ID</th>
                                    <th>شماره کارت</th>
                                    <th>نوع</th>
                                    <th>CVV2</th>
                                    <th>تاریخ انقضا</th>
                                    <th>وضعیت</th>
                                    <th>شماره حساب</th>
                                    <th>کاربر</th>
                                    <th>عملیات</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach items="${cards}" var="card">
                                    <tr>
                                        <td>${card.id}</td>
                                        <td>
                                            <code>${card.cardNumber}</code>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${card.type == 'DEBIT'}">
                                                    <span class="badge bg-primary">دبیت</span>
                                                </c:when>
                                                <c:when test="${card.type == 'CREDIT'}">
                                                    <span class="badge bg-info">اعتباری</span>
                                                </c:when>
                                                <c:otherwise>
                                                    ${card.type}
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <code>${card.cvv}</code>
                                        </td>
                                        <td>
                                                ${card.expiryDate}
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${card.active}">
                                                    <span class="badge bg-success">
                                                        <i class="fas fa-check-circle me-1"></i>فعال
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-danger">
                                                        <i class="fas fa-times-circle me-1"></i>غیرفعال
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/accounts/detail?id=${card.account.id}">
                                                    ${card.account.accountNumber}
                                            </a>
                                        </td>
                                        <td>
                                                ${card.account.user.username}
                                        </td>
                                        <td>
                                            <div class="btn-group" role="group">
                                                <a href="${pageContext.request.contextPath}/cards/detail?id=${card.id}"
                                                   class="btn btn-sm btn-info" title="مشاهده جزئیات">
                                                    <i class="fas fa-eye"></i>
                                                </a>
                                                <c:if test="${card.active}">
                                                    <form action="${pageContext.request.contextPath}/cards/block"
                                                          method="post" class="d-inline"
                                                          onsubmit="return confirm('آیا از مسدود کردن این کارت اطمینان دارید؟')">
                                                        <input type="hidden" name="id" value="${card.id}">
                                                        <button type="submit" class="btn btn-sm btn-warning" title="مسدود کردن">
                                                            <i class="fas fa-lock"></i>
                                                        </button>
                                                    </form>
                                                </c:if>
                                                <c:if test="${!card.active && (sessionScope.roles.contains('ADMIN') || sessionScope.roles.contains('MANAGER'))}">
                                                    <form action="${pageContext.request.contextPath}/cards/activate"
                                                          method="post" class="d-inline">
                                                        <input type="hidden" name="id" value="${card.id}">
                                                        <button type="submit" class="btn btn-sm btn-success" title="فعال‌سازی">
                                                            <i class="fas fa-unlock"></i>
                                                        </button>
                                                    </form>
                                                </c:if>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>

                        <!-- تعداد کارت‌ها -->
                        <div class="mt-3">
                            <p class="text-muted">
                                <i class="fas fa-info-circle me-2"></i>
                                تعداد کل کارت‌ها: <strong>${cards.size()}</strong>
                            </p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- صفحه‌بندی -->
        <c:if test="${not empty cards && cards.size() >= pageSize}">
            <nav aria-label="Pagination" class="mt-4">
                <ul class="pagination justify-content-center">
                    <li class="page-item ${currentPage == 0 ? 'disabled' : ''}">
                        <a class="page-link" href="?page=${currentPage - 1}${not empty param.userId ? '&userId='.concat(param.userId) : ''}${not empty param.active ? '&active='.concat(param.active) : ''}">
                            <i class="fas fa-chevron-right me-1"></i>قبلی
                        </a>
                    </li>
                    <li class="page-item active">
                        <span class="page-link">صفحه ${currentPage + 1}</span>
                    </li>
                    <li class="page-item">
                        <a class="page-link" href="?page=${currentPage + 1}${not empty param.userId ? '&userId='.concat(param.userId) : ''}${not empty param.active ? '&active='.concat(param.active) : ''}">
                            بعدی<i class="fas fa-chevron-left ms-1"></i>
                        </a>
                    </li>
                </ul>
            </nav>
        </c:if>
    </div>
</div>

<jsp:include page="/views/common/footer.jsp" />
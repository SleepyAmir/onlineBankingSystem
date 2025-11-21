<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="fa" dir="rtl">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>جزئیات کارت</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.rtl.min.css" rel="stylesheet">
    <style>
        .card-preview {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-radius: 15px;
            padding: 25px;
            color: white;
            max-width: 400px;
            margin: 0 auto 20px;
        }
        .card-number {
            font-size: 1.4rem;
            letter-spacing: 3px;
            margin: 20px 0;
        }
        .card-info {
            display: flex;
            justify-content: space-between;
        }
    </style>
</head>
<body>
<div class="container mt-4">
    <c:if test="${not empty card}">
        <%-- نمایش گرافیکی کارت --%>
        <div class="card-preview">
            <div class="d-flex justify-content-between align-items-center">
                <span>سیستم بانکداری آنلاین</span>
                <span>
                    <c:choose>
                        <c:when test="${card.type == 'DEBIT'}">دبیت</c:when>
                        <c:when test="${card.type == 'CREDIT'}">اعتباری</c:when>
                        <c:otherwise>${card.type}</c:otherwise>
                    </c:choose>
                </span>
            </div>
            <div class="card-number text-center">
                    ${card.cardNumber}
            </div>
            <div class="card-info">
                <div>
                    <small>صاحب کارت</small>
                    <div>${card.account.user.firstName} ${card.account.user.lastName}</div>
                </div>
                <div>
                    <small>تاریخ انقضا</small>
                        <%-- ✅ استفاده از متد فرمت‌شده --%>
                    <div>${card.formattedExpiryDate}</div>
                </div>
            </div>
        </div>

        <%-- جزئیات کارت --%>
        <div class="card">
            <div class="card-header bg-primary text-white">
                <h4>جزئیات کارت</h4>
            </div>
            <div class="card-body">
                <table class="table table-bordered">
                    <tr>
                        <th>شماره کارت</th>
                        <td>${card.cardNumber}</td>
                    </tr>
                    <tr>
                        <th>نوع کارت</th>
                        <td>
                            <c:choose>
                                <c:when test="${card.type == 'DEBIT'}">
                                    <span class="badge bg-primary">کارت دبیت</span>
                                </c:when>
                                <c:when test="${card.type == 'CREDIT'}">
                                    <span class="badge bg-info">کارت اعتباری</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-secondary">${card.type}</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                    <tr>
                        <th>CVV2</th>
                        <td>***</td>
                    </tr>
                    <tr>
                        <th>تاریخ انقضا</th>
                        <td>
                                <%-- ✅ روش 1: استفاده از متد کمکی --%>
                            <fmt:formatDate value="${card.expiryDateAsDate}" pattern="MM/yy"/>

                                <%-- بررسی انقضا --%>
                            <c:if test="${card.expired}">
                                <span class="badge bg-danger ms-2">منقضی شده</span>
                            </c:if>
                            <c:if test="${card.expiringSoon and not card.expired}">
                                <span class="badge bg-warning ms-2">نزدیک به انقضا</span>
                            </c:if>
                        </td>
                    </tr>
                    <tr>
                        <th>وضعیت</th>
                        <td>
                            <c:choose>
                                <c:when test="${card.active}">
                                    <span class="badge bg-success">فعال</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-danger">غیرفعال</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                    <tr>
                        <th>شماره حساب</th>
                        <td>
                            <a href="${pageContext.request.contextPath}/accounts/detail?id=${card.account.id}">
                                    ${card.account.accountNumber}
                            </a>
                        </td>
                    </tr>
                    <tr>
                        <th>موجودی حساب</th>
                        <td>
                            <fmt:formatNumber value="${card.account.balance}" type="number" groupingUsed="true"/> ریال
                        </td>
                    </tr>
                    <tr>
                        <th>صاحب کارت</th>
                        <td>${card.account.user.firstName} ${card.account.user.lastName}</td>
                    </tr>
                    <tr>
                        <th>تاریخ صدور</th>
                        <td>
                                <%-- ✅ استفاده از متد Base --%>
                            <fmt:formatDate value="${card.createdAtAsDate}" pattern="yyyy/MM/dd HH:mm"/>
                        </td>
                    </tr>
                </table>

                    <%-- دکمه‌های عملیات --%>
                <div class="mt-3">
                    <c:if test="${card.active}">
                        <form action="${pageContext.request.contextPath}/cards/block" method="post" class="d-inline">
                            <input type="hidden" name="id" value="${card.id}">
                            <button type="submit" class="btn btn-danger"
                                    onclick="return confirm('آیا از مسدود کردن این کارت اطمینان دارید؟')">
                                مسدود کردن
                            </button>
                        </form>
                    </c:if>

                    <c:if test="${not card.active and sessionScope.roles.contains('ADMIN') or sessionScope.roles.contains('MANAGER')}">
                        <form action="${pageContext.request.contextPath}/cards/activate" method="post" class="d-inline">
                            <input type="hidden" name="id" value="${card.id}">
                            <button type="submit" class="btn btn-success">
                                فعال‌سازی
                            </button>
                        </form>
                    </c:if>

                    <a href="${pageContext.request.contextPath}/cards/list" class="btn btn-secondary">
                        بازگشت به لیست
                    </a>
                </div>
            </div>
        </div>
    </c:if>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isErrorPage="true" %>

<!DOCTYPE html>
<html lang="fa" dir="rtl">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>خطا - سیستم بانکداری آنلاین</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.rtl.min.css" rel="stylesheet">
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 2rem;
        }

        .error-container {
            background: white;
            padding: 3rem;
            border-radius: 20px;
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
            text-align: center;
            max-width: 600px;
        }

        .error-icon {
            font-size: 5rem;
            color: #ef4444;
            margin-bottom: 2rem;
            animation: bounce 1s infinite;
        }

        @keyframes bounce {
            0%, 100% { transform: translateY(0); }
            50% { transform: translateY(-20px); }
        }

        .error-code {
            font-size: 3rem;
            font-weight: 800;
            color: #667eea;
            margin-bottom: 1rem;
        }

        .error-message {
            font-size: 1.2rem;
            color: #6b7280;
            margin-bottom: 2rem;
        }

        .btn-home {
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: white;
            padding: 1rem 2rem;
            border-radius: 50px;
            text-decoration: none;
            font-weight: 600;
            transition: all 0.3s ease;
            display: inline-block;
        }

        .btn-home:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 30px rgba(0,0,0,0.2);
            color: white;
        }
    </style>
</head>
<body>
<div class="error-container">
    <div class="error-icon">
        <i class="fas fa-exclamation-triangle"></i>
    </div>

    <c:choose>
        <c:when test="${pageContext.response.status == 404}">
            <h1 class="error-code">404</h1>
            <p class="error-message">صفحه مورد نظر یافت نشد</p>
        </c:when>
        <c:when test="${pageContext.response.status == 403}">
            <h1 class="error-code">403</h1>
            <p class="error-message">دسترسی غیرمجاز</p>
        </c:when>
        <c:when test="${pageContext.response.status == 500}">
            <h1 class="error-code">500</h1>
            <p class="error-message">خطای سرور</p>
        </c:when>
        <c:otherwise>
            <h1 class="error-code">خطا</h1>
            <p class="error-message">
                <c:choose>
                    <c:when test="${not empty error}">
                        ${error}
                    </c:when>
                    <c:when test="${not empty pageContext.exception}">
                        ${pageContext.exception.message}
                    </c:when>
                    <c:otherwise>
                        متأسفانه خطایی رخ داده است. لطفاً بعداً امتحان کنید.
                    </c:otherwise>
                </c:choose>
            </p>
        </c:otherwise>
    </c:choose>

    <a href="${pageContext.request.contextPath}/" class="btn-home">
        <i class="fas fa-home me-2"></i>بازگشت به صفحه اصلی
    </a>
</div>
</body>
</html>
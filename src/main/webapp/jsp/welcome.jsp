<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fa" dir="rtl">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>بانک اسلیپی | خوش‌آمدگویی</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
    <link href="https://fonts.googleapis.com/css2?family=Vazirmatn:wght@400;500;700&display=swap" rel="stylesheet" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" />
    <style>
        body { font-family: 'Vazirmatn', sans-serif; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); min-height: 100vh; }
        .hero { background: rgba(255,255,255,0.95); border-radius: 20px; box-shadow: 0 15px 35px rgba(0,0,0,0.1); }
        .btn-primary { background: #5a67d8; border: none; }
        .btn-primary:hover { background: #4c51bf; }
    </style>
</head>
<body class="d-flex align-items-center justify-content-center">
<div class="container py-5">
    <div class="hero p-5 text-center col-lg-8 mx-auto">
        <h1 class="display-4 fw-bold text-primary mb-4">
            بانک اسلیپی
        </h1>
        <p class="lead mb-4">${bankIntro}</p>
        <div class="d-flex justify-content-center gap-3 flex-wrap">
            <a href="${pageContext.request.contextPath}/login" class="btn btn-primary btn-lg px-5">
                ورود
            </a>
            <a href="${pageContext.request.contextPath}/signup" class="btn btn-outline-primary btn-lg px-5">
                ثبت‌نام
            </a>
        </div>
    </div>
</div>
</body>
</html>
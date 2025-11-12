<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:include page="common/header.jsp">
    <jsp:param name="title" value="خوش آمدید - سیستم بانکداری آنلاین" />
    <jsp:param name="isWelcome" value="true" />
</jsp:include>

<style>
    body {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        overflow-x: hidden;
    }

    /* Hero Section */
    .hero-section {
        min-height: 100vh;
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 2rem;
        position: relative;
        overflow: hidden;
    }

    .hero-section::before {
        content: '';
        position: absolute;
        width: 500px;
        height: 500px;
        background: rgba(255, 255, 255, 0.1);
        border-radius: 50%;
        top: -250px;
        right: -250px;
        animation: float 6s ease-in-out infinite;
    }

    .hero-section::after {
        content: '';
        position: absolute;
        width: 400px;
        height: 400px;
        background: rgba(255, 255, 255, 0.1);
        border-radius: 50%;
        bottom: -200px;
        left: -200px;
        animation: float 8s ease-in-out infinite reverse;
    }

    @keyframes float {
        0%, 100% { transform: translateY(0px); }
        50% { transform: translateY(20px); }
    }

    .hero-content {
        max-width: 1200px;
        margin: 0 auto;
        position: relative;
        z-index: 1;
    }

    .hero-grid {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 3rem;
        align-items: center;
    }

    .hero-text h1 {
        font-size: 3.5rem;
        font-weight: 800;
        color: white;
        margin-bottom: 1.5rem;
        line-height: 1.2;
        text-shadow: 0 4px 20px rgba(0,0,0,0.2);
    }

    .hero-text p {
        font-size: 1.3rem;
        color: rgba(255, 255, 255, 0.9);
        margin-bottom: 2.5rem;
        line-height: 1.6;
    }

    .hero-buttons {
        display: flex;
        gap: 1.5rem;
        flex-wrap: wrap;
    }

    .hero-btn {
        padding: 1rem 2.5rem;
        border-radius: 50px;
        font-size: 1.1rem;
        font-weight: 600;
        text-decoration: none;
        transition: all 0.3s ease;
        display: inline-flex;
        align-items: center;
        gap: 0.75rem;
        box-shadow: 0 10px 30px rgba(0,0,0,0.2);
    }

    .btn-primary-custom {
        background: white;
        color: var(--primary-color);
    }

    .btn-primary-custom:hover {
        transform: translateY(-5px);
        box-shadow: 0 15px 40px rgba(0,0,0,0.3);
    }

    .btn-secondary-custom {
        background: transparent;
        color: white;
        border: 2px solid white;
    }

    .btn-secondary-custom:hover {
        background: white;
        color: var(--primary-color);
        transform: translateY(-5px);
    }

    .hero-card {
        background: rgba(255, 255, 255, 0.95);
        backdrop-filter: blur(10px);
        border-radius: 30px;
        padding: 3rem;
        box-shadow: 0 20px 60px rgba(0,0,0,0.3);
        animation: slideInRight 0.8s ease;
    }

    @keyframes slideInRight {
        from {
            opacity: 0;
            transform: translateX(50px);
        }
        to {
            opacity: 1;
            transform: translateX(0);
        }
    }

    .stats-grid {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 1.5rem;
        margin-bottom: 2rem;
    }

    .stat-item {
        text-align: center;
        padding: 1.5rem;
        background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
        border-radius: 20px;
        color: white;
    }

    .stat-number {
        font-size: 2.5rem;
        font-weight: 800;
        display: block;
        margin-bottom: 0.5rem;
    }

    .stat-label {
        font-size: 0.9rem;
        opacity: 0.9;
    }

    /* Features Section */
    .features-section {
        padding: 5rem 2rem;
        background: white;
    }

    .section-title {
        text-align: center;
        font-size: 2.5rem;
        font-weight: 800;
        color: var(--dark-color);
        margin-bottom: 1rem;
    }

    .section-subtitle {
        text-align: center;
        font-size: 1.2rem;
        color: #6b7280;
        margin-bottom: 4rem;
    }

    .features-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
        gap: 2rem;
        max-width: 1200px;
        margin: 0 auto;
    }

    .feature-card {
        background: white;
        padding: 2.5rem;
        border-radius: 20px;
        text-align: center;
        transition: all 0.3s ease;
        border: 2px solid #f3f4f6;
        cursor: pointer;
    }

    .feature-card:hover {
        transform: translateY(-10px);
        box-shadow: 0 20px 40px rgba(0,0,0,0.1);
        border-color: var(--primary-color);
    }

    .feature-icon {
        font-size: 3.5rem;
        margin-bottom: 1.5rem;
        background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
    }

    .feature-title {
        font-size: 1.5rem;
        font-weight: 700;
        color: var(--dark-color);
        margin-bottom: 1rem;
    }

    .feature-description {
        color: #6b7280;
        line-height: 1.6;
    }

    /* Services Section */
    .services-section {
        padding: 5rem 2rem;
        background: linear-gradient(135deg, #f9fafb 0%, #f3f4f6 100%);
    }

    .service-card {
        background: white;
        padding: 2.5rem;
        border-radius: 20px;
        margin-bottom: 2rem;
        transition: all 0.3s ease;
        border-left: 5px solid var(--primary-color);
    }

    .service-card:hover {
        transform: translateX(-10px);
        box-shadow: 0 20px 40px rgba(0,0,0,0.1);
    }

    .service-header {
        display: flex;
        align-items: center;
        gap: 1.5rem;
        margin-bottom: 1.5rem;
    }

    .service-icon {
        font-size: 3rem;
        background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
    }

    .service-features {
        list-style: none;
        padding: 0;
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 1rem;
    }

    .service-features li {
        padding: 0.75rem;
        background: #f9fafb;
        border-radius: 10px;
        display: flex;
        align-items: center;
        gap: 0.75rem;
    }

    .service-features li i {
        color: var(--success-color);
    }

    /* Contact Section */
    .contact-section {
        padding: 5rem 2rem;
        background: white;
    }

    .contact-grid {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 3rem;
        max-width: 1200px;
        margin: 0 auto;
    }

    .contact-info {
        padding: 2rem;
    }

    .contact-item {
        display: flex;
        align-items: start;
        gap: 1.5rem;
        padding: 1.5rem;
        margin-bottom: 1.5rem;
        background: #f9fafb;
        border-radius: 15px;
        transition: all 0.3s ease;
    }

    .contact-item:hover {
        background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
        color: white;
        transform: translateX(-5px);
    }

    .contact-item:hover .contact-item-icon {
        background: white;
        color: var(--primary-color);
    }

    .contact-item-icon {
        width: 50px;
        height: 50px;
        background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
        color: white;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 1.5rem;
        flex-shrink: 0;
    }

    .contact-form {
        background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
        padding: 3rem;
        border-radius: 20px;
        box-shadow: 0 20px 60px rgba(0,0,0,0.2);
    }

    .contact-form h3 {
        color: white;
        margin-bottom: 2rem;
        font-size: 2rem;
    }

    .form-group {
        margin-bottom: 1.5rem;
    }

    .form-group label {
        color: white;
        margin-bottom: 0.5rem;
        display: block;
        font-weight: 600;
    }

    .form-group input,
    .form-group textarea {
        width: 100%;
        padding: 1rem;
        border: none;
        border-radius: 10px;
        font-size: 1rem;
        transition: all 0.3s ease;
    }

    .form-group input:focus,
    .form-group textarea:focus {
        outline: none;
        box-shadow: 0 0 0 3px rgba(255,255,255,0.3);
    }

    .submit-btn {
        width: 100%;
        padding: 1rem;
        background: white;
        color: var(--primary-color);
        border: none;
        border-radius: 50px;
        font-size: 1.1rem;
        font-weight: 700;
        cursor: pointer;
        transition: all 0.3s ease;
    }

    .submit-btn:hover {
        transform: translateY(-3px);
        box-shadow: 0 10px 30px rgba(0,0,0,0.2);
    }

    /* FAQ Section */
    .faq-section {
        padding: 5rem 2rem;
        background: #f9fafb;
    }

    .faq-container {
        max-width: 900px;
        margin: 0 auto;
    }

    .faq-item {
        background: white;
        padding: 2rem;
        border-radius: 15px;
        margin-bottom: 1.5rem;
        box-shadow: 0 2px 10px rgba(0,0,0,0.05);
        transition: all 0.3s ease;
    }

    .faq-item:hover {
        box-shadow: 0 10px 30px rgba(0,0,0,0.1);
    }

    .faq-question {
        font-size: 1.2rem;
        font-weight: 700;
        color: var(--dark-color);
        margin-bottom: 1rem;
        display: flex;
        align-items: center;
        gap: 1rem;
    }

    .faq-question i {
        color: var(--primary-color);
    }

    .faq-answer {
        color: #6b7280;
        line-height: 1.6;
    }

    /* About Section */
    .about-section {
        padding: 5rem 2rem;
        background: white;
    }

    .about-content {
        max-width: 1200px;
        margin: 0 auto;
    }

    .mission-vision {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 2rem;
        margin-bottom: 4rem;
    }

    .mission-card, .vision-card {
        padding: 3rem;
        border-radius: 20px;
        text-align: center;
    }

    .mission-card {
        background: linear-gradient(135deg, #10b981, #059669);
        color: white;
    }

    .vision-card {
        background: linear-gradient(135deg, #3b82f6, #2563eb);
        color: white;
    }

    .mission-card h3, .vision-card h3 {
        font-size: 2rem;
        margin-bottom: 1rem;
    }

    .values-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
        gap: 2rem;
    }

    .value-card {
        padding: 2rem;
        background: #f9fafb;
        border-radius: 15px;
        text-align: center;
        transition: all 0.3s ease;
    }

    .value-card:hover {
        background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
        color: white;
        transform: translateY(-5px);
    }

    .value-icon {
        font-size: 3rem;
        margin-bottom: 1rem;
    }

    /* Navbar for Welcome Page */
    .welcome-navbar {
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        padding: 1.5rem 2rem;
        background: rgba(255, 255, 255, 0.1);
        backdrop-filter: blur(10px);
        z-index: 1000;
        transition: all 0.3s ease;
    }

    .welcome-navbar.scrolled {
        background: white;
        box-shadow: 0 2px 10px rgba(0,0,0,0.1);
    }

    .welcome-navbar.scrolled .nav-link {
        color: var(--dark-color);
    }

    .welcome-navbar.scrolled .navbar-brand {
        color: var(--primary-color);
    }

    .navbar-content {
        max-width: 1200px;
        margin: 0 auto;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .navbar-brand-welcome {
        font-size: 1.5rem;
        font-weight: 800;
        color: white;
        text-decoration: none;
        display: flex;
        align-items: center;
        gap: 0.75rem;
    }

    .nav-links {
        display: flex;
        gap: 2rem;
        list-style: none;
        margin: 0;
        padding: 0;
    }

    .nav-link {
        color: white;
        text-decoration: none;
        font-weight: 600;
        transition: all 0.3s ease;
        padding: 0.5rem 1rem;
        border-radius: 10px;
    }

    .nav-link:hover {
        background: rgba(255, 255, 255, 0.2);
    }

    .nav-link.active {
        background: white;
        color: var(--primary-color);
    }

    @media (max-width: 968px) {
        .hero-grid {
            grid-template-columns: 1fr;
        }

        .hero-text h1 {
            font-size: 2.5rem;
        }

        .contact-grid,
        .mission-vision {
            grid-template-columns: 1fr;
        }

        .nav-links {
            display: none;
        }
    }
</style>

<!-- Navbar -->
<nav class="welcome-navbar" id="welcomeNavbar">
    <div class="navbar-content">
        <a href="${pageContext.request.contextPath}/" class="navbar-brand-welcome">
            <i class="fas fa-university"></i>
            بانک آنلاین
        </a>
        <ul class="nav-links">
            <li><a href="${pageContext.request.contextPath}/" class="nav-link ${currentPage == 'home' ? 'active' : ''}">خانه</a></li>
            <li><a href="${pageContext.request.contextPath}/about" class="nav-link ${currentPage == 'about' ? 'active' : ''}">درباره ما</a></li>
            <li><a href="${pageContext.request.contextPath}/services" class="nav-link ${currentPage == 'services' ? 'active' : ''}">خدمات</a></li>
            <li><a href="${pageContext.request.contextPath}/contact" class="nav-link ${currentPage == 'contact' ? 'active' : ''}">تماس</a></li>
        </ul>
    </div>
</nav>

<!-- Content Based on Current Page -->
<c:choose>
    <%-- HOME PAGE --%>
    <c:when test="${currentPage == 'home' || empty currentPage}">
        <!-- Hero Section -->
        <section class="hero-section">
            <div class="hero-content">
                <div class="hero-grid">
                    <div class="hero-text">
                        <h1>بانکداری آنلاین در دستان شما</h1>
                        <p>مدیریت امن و آسان حساب‌های بانکی، تراکنش‌ها و وام‌ها در هر زمان و مکان</p>
                        <div class="hero-buttons">
                            <a href="${pageContext.request.contextPath}/auth/register" class="hero-btn btn-primary-custom">
                                <i class="fas fa-user-plus"></i>
                                ثبت‌نام رایگان
                            </a>
                            <a href="${pageContext.request.contextPath}/auth/login" class="hero-btn btn-secondary-custom">
                                <i class="fas fa-sign-in-alt"></i>
                                ورود به حساب
                            </a>
                        </div>
                    </div>
                    <div class="hero-card">
                        <div class="stats-grid">
                            <div class="stat-item">
                                <span class="stat-number">${totalUsers}</span>
                                <span class="stat-label">کاربر فعال</span>
                            </div>
                            <div class="stat-item">
                                <span class="stat-number">${totalAccounts}</span>
                                <span class="stat-label">حساب بانکی</span>
                            </div>
                            <div class="stat-item">
                                <span class="stat-number">${totalTransactions}</span>
                                <span class="stat-label">تراکنش موفق</span>
                            </div>
                            <div class="stat-item">
                                <span class="stat-number">${satisfactionRate}</span>
                                <span class="stat-label">رضایت مشتریان</span>
                            </div>
                        </div>
                        <div style="text-align: center; margin-top: 2rem;">
                            <h3 style="color: var(--primary-color); margin-bottom: 1rem;">چرا ما را انتخاب کنید؟</h3>
                            <p style="color: #6b7280;">امنیت بالا • سرعت عالی • پشتیبانی 24/7</p>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <!-- Features Section -->
        <section class="features-section">
            <h2 class="section-title">ویژگی‌های برتر</h2>
            <p class="section-subtitle">همه آنچه برای مدیریت امور مالی خود نیاز دارید</p>
            <div class="features-grid">
                <c:forEach items="${features}" var="feature">
                    <div class="feature-card">
                        <div class="feature-icon">${feature.icon}</div>
                        <h3 class="feature-title">${feature.title}</h3>
                        <p class="feature-description">${feature.description}</p>
                    </div>
                </c:forEach>
            </div>
        </section>
    </c:when>

    <%-- ABOUT PAGE --%>
    <c:when test="${currentPage == 'about'}">
        <section class="about-section" style="padding-top: 8rem;">
            <div class="about-content">
                <h2 class="section-title">درباره ما</h2>
                <p class="section-subtitle">سیستم پیشرفته بانکداری دیجیتال</p>

                <div class="mission-vision">
                    <div class="mission-card">
                        <i class="fas fa-bullseye" style="font-size: 3rem; margin-bottom: 1rem;"></i>
                        <h3>ماموریت ما</h3>
                        <p>${mission}</p>
                    </div>
                    <div class="vision-card">
                        <i class="fas fa-eye" style="font-size: 3rem; margin-bottom: 1rem;"></i>
                        <h3>چشم‌انداز ما</h3>
                        <p>${vision}</p>
                    </div>
                </div>

                <h3 class="section-title" style="font-size: 2rem; margin-bottom: 3rem;">ارزش‌های ما</h3>
                <div class="values-grid">
                    <c:forEach items="${values}" var="value">
                        <div class="value-card">
                            <div class="value-icon">${value.icon}</div>
                            <h4>${value.title}</h4>
                            <p>${value.description}</p>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </section>
    </c:when>

    <%-- SERVICES PAGE --%>
    <c:when test="${currentPage == 'services'}">
        <section class="services-section" style="padding-top: 8rem;">
            <h2 class="section-title">خدمات ما</h2>
            <p class="section-subtitle">مجموعه کامل خدمات بانکی دیجیتال</p>
            <div style="max-width: 1200px; margin: 0 auto;">
                <c:forEach items="${services}" var="service">
                    <div class="service-card">
                        <div class="service-header">
                            <div class="service-icon">${service.icon}</div>
                            <div>
                                <h3>${service.title}</h3>
                                <p style="color: #6b7280; margin: 0;">${service.description}</p>
                            </div>
                        </div>
                        <ul class="service-features">
                            <c:forEach items="${service.features}" var="feature">
                                <li>
                                    <i class="fas fa-check-circle"></i>
                                    <span>${feature}</span>
                                </li>
                            </c:forEach>
                        </ul>
                    </div>
                </c:forEach>
            </div>
        </section>
    </c:when>

    <%-- CONTACT PAGE --%>
    <c:when test="${currentPage == 'contact'}">
        <section class="contact-section" style="padding-top: 8rem;">
            <h2 class="section-title">تماس با ما</h2>
            <p class="section-subtitle">ما را با راه‌های زیر می‌توانید پیدا کنید</p>

            <div class="contact-grid">
                <div class="contact-info">
                    <c:forEach items="${contactMethods}" var="method">
                        <div class="contact-item">
                            <div class="contact-item-icon">${method.icon}</div>
                            <div>
                                <h4>${method.title}</h4>
                                <p style="font-size: 1.1rem; font-weight: 600; margin: 0.5rem 0;">${method.value}</p>
                                <p style="color: #6b7280; margin: 0;">${method.description}</p>
                            </div>
                        </div>
                    </c:forEach>
                </div>

                <div class="contact-form">
                    <h3>پیام خود را ارسال کنید</h3>
                    <form action="${pageContext.request.contextPath}/contact" method="post">
                        <div class="form-group">
                            <label>نام و نام خانوادگی *</label>
                            <input type="text" name="name" required>
                        </div>
                        <div class="form-group">
                            <label>ایمیل *</label>
                            <input type="email" name="email" required>
                        </div>
                        <div class="form-group">
                            <label>موضوع</label>
                            <input type="text" name="subject">
                        </div>
                        <div class="form-group">
                            <label>پیام *</label>
                            <textarea name="message" rows="5" required></textarea>
                        </div>
                        <button type="submit" class="submit-btn">
                            <i class="fas fa-paper-plane"></i>
                            ارسال پیام
                        </button>
                    </form>
                </div>
            </div>

            <!-- FAQ Section -->
            <div class="faq-section" style="padding-top: 5rem; background: transparent;">
                <h3 class="section-title" style="font-size: 2rem;">سوالات متداول</h3>
                <div class="faq-container">
                    <c:forEach items="${faqs}" var="faq">
                        <div class="faq-item">
                            <div class="faq-question">
                                <i class="fas fa-question-circle"></i>
                                    ${faq.question}
                            </div>
                            <div class="faq-answer">${faq.answer}</div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </section>
    </c:when>
</c:choose>

<!-- Footer -->
<footer style="background: white; padding: 3rem 2rem; text-align: center; margin-top: 5rem;">
    <div style="max-width: 1200px; margin: 0 auto;">
        <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 2rem;">
            <div>
                <h3 style="color: var(--primary-color); margin-bottom: 0.5rem;">
                    <i class="fas fa-university"></i> بانک آنلاین
                </h3>
                <p style="color: #6b7280;">© 2025 تمامی حقوق محفوظ است</p>
            </div>
            <div style="display: flex; gap: 2rem;">
                <a href="${pageContext.request.contextPath}/" style="color: var(--primary-color); text-decoration: none;">خانه</a>
                <a href="${pageContext.request.contextPath}/about" style="color: var(--primary-color); text-decoration: none;">درباره ما</a>
                <a href="${pageContext.request.contextPath}/services" style="color: var(--primary-color); text-decoration: none;">خدمات</a>
                <a href="${pageContext.request.contextPath}/contact" style="color: var(--primary-color); text-decoration: none;">تماس</a>
                <a href="${github}" target="_blank" style="color: var(--primary-color); text-decoration: none;">
                    <i class="fab fa-github"></i> GitHub
                </a>
            </div>
        </div>
    </div>
</footer>

<script>
    // Navbar scroll effect
    window.addEventListener('scroll', function() {
        const navbar = document.getElementById('welcomeNavbar');
        if (window.scrollY > 50) {
            navbar.classList.add('scrolled');
        } else {
            navbar.classList.remove('scrolled');
        }
    });
</script>

</body>
</html>
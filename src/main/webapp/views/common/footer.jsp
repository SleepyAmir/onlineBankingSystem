<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<style>
    @keyframes fadeInUp {
        from {
            opacity: 0;
            transform: translateY(20px);
        }
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }

    @keyframes pulse {
        0%, 100% {
            transform: scale(1);
        }
        50% {
            transform: scale(1.05);
        }
    }

    /* ✅ اضافه شده: متغیرها */
    :root {
        --sidebar-width: 280px;
        --sidebar-collapsed-width: 80px;
    }

    .modern-footer {
        background: linear-gradient(135deg, #1f2937 0%, #111827 100%);
        color: #e5e7eb;
        padding: 4rem 0 0;
        position: relative;
        overflow: hidden;
        margin-top: 5rem;
        margin-right: var(--sidebar-width, 280px); /* ✅ بدون تغییر */
        transition: margin-right 0.4s cubic-bezier(0.4, 0, 0.2, 1); /* ✅ بدون تغییر */
    }

    /* ✅ اصلاح شده: sidebar-collapsed به جای sidebar-mini */
    body.sidebar-collapsed .modern-footer {
        margin-right: var(--sidebar-collapsed-width, 80px); /* ✅ اصلاح شده */
    }

    /* برای موبایل footer تمام عرض */
    @media (max-width: 768px) {
        .modern-footer {
            margin-right: 0 !important;
        }
    }

    .modern-footer::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        height: 5px;
        background: linear-gradient(90deg,
        var(--primary-color) 0%,
        var(--secondary-color) 50%,
        var(--primary-color) 100%);
        background-size: 200% auto;
        animation: gradient 3s linear infinite;
    }

    @keyframes gradient {
        0% { background-position: 0% 50%; }
        50% { background-position: 100% 50%; }
        100% { background-position: 0% 50%; }
    }

    .footer-content {
        max-width: 1200px;
        margin: 0 auto;
        padding: 0 2rem;
        animation: fadeInUp 0.8s ease-out;
    }

    .footer-grid {
        display: grid;
        grid-template-columns: 2fr 1fr 1fr 1fr;
        gap: 3rem;
        margin-bottom: 3rem;
    }

    .footer-section {
        animation: fadeInUp 0.8s ease-out backwards;
    }

    .footer-section:nth-child(1) { animation-delay: 0.1s; }
    .footer-section:nth-child(2) { animation-delay: 0.2s; }
    .footer-section:nth-child(3) { animation-delay: 0.3s; }
    .footer-section:nth-child(4) { animation-delay: 0.4s; }

    .footer-logo {
        display: flex;
        align-items: center;
        gap: 1rem;
        margin-bottom: 1.5rem;
        font-size: 1.8rem;
        font-weight: 800;
    }

    .footer-logo i {
        background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        font-size: 2.5rem;
        animation: pulse 2s infinite;
    }

    .footer-logo span {
        background: linear-gradient(135deg, #fff, #e5e7eb);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
    }

    .footer-description {
        color: #9ca3af;
        line-height: 1.8;
        margin-bottom: 2rem;
        font-size: 0.95rem;
    }

    .footer-social {
        display: flex;
        gap: 1rem;
    }

    .social-link {
        width: 45px;
        height: 45px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        background: rgba(255, 255, 255, 0.05);
        color: #e5e7eb;
        text-decoration: none;
        transition: all 0.3s ease;
        font-size: 1.2rem;
    }

    .social-link:hover {
        background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
        color: white;
        transform: translateY(-5px);
        box-shadow: 0 10px 25px rgba(102, 126, 234, 0.3);
    }

    .footer-title {
        font-size: 1.1rem;
        font-weight: 700;
        margin-bottom: 1.5rem;
        color: white;
        position: relative;
        padding-bottom: 0.75rem;
    }

    .footer-title::after {
        content: '';
        position: absolute;
        bottom: 0;
        right: 0;
        width: 40px;
        height: 3px;
        background: linear-gradient(90deg, var(--primary-color), var(--secondary-color));
        border-radius: 2px;
    }

    .footer-links {
        list-style: none;
        padding: 0;
        margin: 0;
    }

    .footer-links li {
        margin-bottom: 0.75rem;
    }

    .footer-links a {
        color: #9ca3af;
        text-decoration: none;
        transition: all 0.3s ease;
        display: inline-flex;
        align-items: center;
        gap: 0.5rem;
        font-size: 0.95rem;
    }

    .footer-links a i {
        transition: transform 0.3s ease;
        font-size: 0.8rem;
    }

    .footer-links a:hover {
        color: var(--primary-color);
        padding-right: 0.5rem;
    }

    .footer-links a:hover i {
        transform: translateX(-5px);
    }

    .footer-contact {
        display: flex;
        flex-direction: column;
        gap: 1rem;
    }

    .contact-item {
        display: flex;
        align-items: center;
        gap: 1rem;
        color: #9ca3af;
        font-size: 0.95rem;
    }

    .contact-icon {
        width: 40px;
        height: 40px;
        border-radius: 10px;
        background: rgba(255, 255, 255, 0.05);
        display: flex;
        align-items: center;
        justify-content: center;
        color: var(--primary-color);
        flex-shrink: 0;
    }

    .footer-bottom {
        border-top: 1px solid rgba(255, 255, 255, 0.1);
        padding: 2rem 0;
        margin-top: 3rem;
    }

    .footer-bottom-content {
        max-width: 1200px;
        margin: 0 auto;
        padding: 0 2rem;
        display: flex;
        justify-content: space-between;
        align-items: center;
        flex-wrap: wrap;
        gap: 1rem;
    }

    .copyright {
        color: #9ca3af;
        font-size: 0.9rem;
    }

    .copyright strong {
        color: white;
        font-weight: 600;
    }

    .footer-bottom-links {
        display: flex;
        gap: 2rem;
    }

    .footer-bottom-links a {
        color: #9ca3af;
        text-decoration: none;
        font-size: 0.9rem;
        transition: all 0.3s ease;
    }

    .footer-bottom-links a:hover {
        color: var(--primary-color);
    }

    .back-to-top {
        position: fixed;
        bottom: 2rem;
        left: 2rem;
        width: 50px;
        height: 50px;
        background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
        color: white;
        border: none;
        border-radius: 50%;
        cursor: pointer;
        display: none;
        align-items: center;
        justify-content: center;
        font-size: 1.2rem;
        box-shadow: 0 5px 20px rgba(102, 126, 234, 0.4);
        transition: all 0.3s ease;
        z-index: 1000;
    }

    .back-to-top.show {
        display: flex;
    }

    .back-to-top:hover {
        transform: translateY(-5px);
        box-shadow: 0 10px 30px rgba(102, 126, 234, 0.6);
    }

    /* رسپانسیو */
    @media (max-width: 1024px) {
        .footer-grid {
            grid-template-columns: 1fr 1fr;
            gap: 2rem;
        }
    }

    @media (max-width: 640px) {
        .modern-footer {
            padding: 3rem 0 0;
        }

        .footer-grid {
            grid-template-columns: 1fr;
            gap: 2rem;
        }

        .footer-bottom-content {
            flex-direction: column;
            text-align: center;
        }

        .footer-bottom-links {
            flex-direction: column;
            gap: 1rem;
        }

        .back-to-top {
            bottom: 1rem;
            left: 1rem;
            width: 45px;
            height: 45px;
        }
    }
</style>

<footer class="modern-footer">
    <div class="footer-content">
        <div class="footer-grid">
            <!-- بخش اصلی -->
            <div class="footer-section">
                <div class="footer-logo">
                    <i class="fas fa-university"></i>
                    <span>بانک آنلاین</span>
                </div>
                <p class="footer-description">
                    سیستم بانکداری آنلاین امن و مطمئن برای مدیریت امور مالی شما.
                    با فناوری‌های روز دنیا و امنیت بالا، خدمات بانکی را در هر زمان و مکان تجربه کنید.
                </p>
                <div class="footer-social">
                    <a href="#" class="social-link" title="تلگرام">
                        <i class="fab fa-telegram"></i>
                    </a>
                    <a href="#" class="social-link" title="اینستاگرام">
                        <i class="fab fa-instagram"></i>
                    </a>
                    <a href="#" class="social-link" title="توییتر">
                        <i class="fab fa-twitter"></i>
                    </a>
                    <a href="#" class="social-link" title="لینکدین">
                        <i class="fab fa-linkedin"></i>
                    </a>
                </div>
            </div>

            <!-- دسترسی سریع -->
            <div class="footer-section">
                <h3 class="footer-title">دسترسی سریع</h3>
                <ul class="footer-links">
                    <li>
                        <a href="${pageContext.request.contextPath}/">
                            <i class="fas fa-chevron-left"></i>
                            صفحه اصلی
                        </a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/user-dashboard">
                            <i class="fas fa-chevron-left"></i>
                            داشبورد
                        </a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/accounts/list">
                            <i class="fas fa-chevron-left"></i>
                            حساب‌های من
                        </a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/transactions">
                            <i class="fas fa-chevron-left"></i>
                            انتقال وجه
                        </a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/loans/list">
                            <i class="fas fa-chevron-left"></i>
                            وام‌ها
                        </a>
                    </li>
                </ul>
            </div>

            <!-- خدمات -->
            <div class="footer-section">
                <h3 class="footer-title">خدمات ما</h3>
                <ul class="footer-links">
                    <li>
                        <a href="#">
                            <i class="fas fa-chevron-left"></i>
                            افتتاح حساب
                        </a>
                    </li>
                    <li>
                        <a href="#">
                            <i class="fas fa-chevron-left"></i>
                            صدور کارت
                        </a>
                    </li>
                    <li>
                        <a href="#">
                            <i class="fas fa-chevron-left"></i>
                            دریافت وام
                        </a>
                    </li>
                    <li>
                        <a href="#">
                            <i class="fas fa-chevron-left"></i>
                            مشاوره مالی
                        </a>
                    </li>
                    <li>
                        <a href="#">
                            <i class="fas fa-chevron-left"></i>
                            پشتیبانی 24/7
                        </a>
                    </li>
                </ul>
            </div>

            <!-- تماس با ما -->
            <div class="footer-section">
                <h3 class="footer-title">تماس با ما</h3>
                <div class="footer-contact">
                    <div class="contact-item">
                        <div class="contact-icon">
                            <i class="fas fa-phone"></i>
                        </div>
                        <div>
                            <div>021-12345678</div>
                            <small style="color: #6b7280;">24 ساعته</small>
                        </div>
                    </div>
                    <div class="contact-item">
                        <div class="contact-icon">
                            <i class="fas fa-envelope"></i>
                        </div>
                        <div>
                            <div>support@onlinebank.ir</div>
                            <small style="color: #6b7280;">پاسخگویی سریع</small>
                        </div>
                    </div>
                    <div class="contact-item">
                        <div class="contact-icon">
                            <i class="fas fa-map-marker-alt"></i>
                        </div>
                        <div>
                            <div>تهران، خیابان ولیعصر</div>
                            <small style="color: #6b7280;">دفتر مرکزی</small>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Footer Bottom -->
    <div class="footer-bottom">
        <div class="footer-bottom-content">
            <div class="copyright">
                <i class="fas fa-copyright"></i>
                <strong>2025</strong> تمامی حقوق محفوظ است - بانک آنلاین
            </div>
            <div class="footer-bottom-links">
                <a href="${pageContext.request.contextPath}/terms">قوانین و مقررات</a>
                <a href="${pageContext.request.contextPath}/privacy">حریم خصوصی</a>
                <a href="${pageContext.request.contextPath}/help">راهنما</a>
            </div>
        </div>
    </div>
</footer>

<!-- دکمه بازگشت به بالا -->
<button class="back-to-top" id="backToTop" title="بازگشت به بالا">
    <i class="fas fa-arrow-up"></i>
</button>

<script>
    // دکمه بازگشت به بالا
    const backToTopBtn = document.getElementById('backToTop');

    window.addEventListener('scroll', function() {
        if (window.pageYOffset > 300) {
            backToTopBtn.classList.add('show');
        } else {
            backToTopBtn.classList.remove('show');
        }
    });

    backToTopBtn.addEventListener('click', function() {
        window.scrollTo({
            top: 0,
            behavior: 'smooth'
        });
    });
</script>
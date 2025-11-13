package com.sleepy.onlinebankingsystem.controller.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * صفحه خوش‌آمدگویی و اطلاعات عمومی سیستم
 * شامل: خانه، درباره ما، خدمات، تماس با ما
 */
@Slf4j
@WebServlet({"", "/", "/welcome", "/about", "/services", "/contact"})
public class WelcomeServlet extends HttpServlet {

    @Override
    @Transactional
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getServletPath();

        // بررسی لاگین بودن کاربر
        HttpSession session = req.getSession(false);
        boolean isLoggedIn = (session != null && session.getAttribute("username") != null);

        // اگر لاگین کرده و صفحه اصلی رو می‌خواد، بره داشبورد
        if (isLoggedIn && (path.equals("/") || path.equals("/welcome") || path.isEmpty())) {
            log.info("User already logged in, redirecting to dashboard");
            resp.sendRedirect(req.getContextPath() + "/user-dashboard");
            return;
        }

        // تشخیص صفحه مورد نظر
        switch (path) {
            case "/about":
                showAboutPage(req, resp);
                break;
            case "/services":
                showServicesPage(req, resp);
                break;
            case "/contact":
                showContactPage(req, resp);
                break;
            default: // "/" یا "/welcome"
                showWelcomePage(req, resp);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getServletPath();

        // فقط صفحه تماس POST داره
        if ("/contact".equals(path)) {
            handleContactForm(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }

    /**
     * 🏠 صفحه اصلی (خانه)
     */
    private void showWelcomePage(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        log.info("Welcome page accessed");

        // آمار سیستم
        req.setAttribute("totalUsers", "1000+");
        req.setAttribute("totalAccounts", "2500+");
        req.setAttribute("totalTransactions", "10000+");
        req.setAttribute("satisfactionRate", "98%");

        // ویژگی‌های سیستم
        List<Feature> features = Arrays.asList(
                new Feature("💳", "مدیریت حساب‌ها", "ایجاد و مدیریت حساب‌های جاری و پس‌انداز با امنیت بالا"),
                new Feature("💸", "تراکنش‌های آنلاین", "واریز، برداشت و انتقال وجه 24/7 بدون محدودیت"),
                new Feature("🏦", "وام‌های بانکی", "درخواست و مدیریت وام با نرخ بهره رقابتی"),
                new Feature("💳", "صدور کارت", "صدور کارت دبیت و اعتباری با قابلیت مدیریت آنلاین"),
                new Feature("📊", "گزارش‌های مالی", "مشاهده تاریخچه کامل تراکنش‌ها و آمار مالی"),
                new Feature("🔒", "امنیت پیشرفته", "رمزنگاری قوی و سیستم احراز هویت چندمرحله‌ای")
        );

        req.setAttribute("features", features);
        req.setAttribute("currentPage", "home");

        req.getRequestDispatcher("/views/welcome.jsp").forward(req, resp);
    }

    /**
     * 📖 صفحه درباره ما
     */
    private void showAboutPage(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        log.info("About page accessed");

        // اطلاعات سیستم
        req.setAttribute("systemName", "سیستم بانکداری آنلاین");
        req.setAttribute("version", "1.0.0");
        req.setAttribute("developer", "Amir Hosseini");
        req.setAttribute("github", "https://github.com/SleepyAmir/onlineBankingSystem");
        req.setAttribute("establishedYear", "2025");

        // ماموریت و چشم‌انداز
        req.setAttribute("mission",
                "ارائه خدمات بانکداری آنلاین با بالاترین کیفیت، امنیت و سهولت دسترسی برای همه مردم");
        req.setAttribute("vision",
                "تبدیل شدن به پیشروترین سیستم بانکداری دیجیتال با رویکرد مشتری‌محوری");

        // تیم توسعه
        List<TeamMember> team = Arrays.asList(
                new TeamMember("Amir Hosseini", "Backend Developer", "Java Enterprise Expert"),
                new TeamMember("Development Team", "Full Stack", "UI/UX & Database Design")
        );
        req.setAttribute("team", team);

        // ارزش‌های اصلی
        List<Value> values = Arrays.asList(
                new Value("🔒", "امنیت", "محافظت از اطلاعات مشتریان در اولویت اول"),
                new Value("⚡", "سرعت", "پردازش تراکنش‌ها در کمترین زمان ممکن"),
                new Value("🎯", "دقت", "انجام عملیات با بالاترین دقت و بدون خطا"),
                new Value("🤝", "اعتماد", "ایجاد رابطه بلندمدت مبتنی بر اعتماد متقابل")
        );
        req.setAttribute("values", values);

        req.setAttribute("currentPage", "about");

        req.getRequestDispatcher("/views/welcome.jsp").forward(req, resp);
    }

    /**
     * 💼 صفحه خدمات
     */
    private void showServicesPage(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        log.info("Services page accessed");

        // خدمات اصلی
        List<Service> services = Arrays.asList(
                new Service(
                        "💳",
                        "مدیریت حساب‌های بانکی",
                        "ایجاد، ویرایش و مدیریت حساب‌های جاری و پس‌انداز",
                        Arrays.asList("حساب جاری", "حساب پس‌انداز", "مدیریت موجودی", "تغییر وضعیت حساب")
                ),
                new Service(
                        "💸",
                        "تراکنش‌های مالی",
                        "انجام انواع تراکنش‌های بانکی به صورت آنلاین و امن",
                        Arrays.asList("واریز وجه", "برداشت وجه", "انتقال بین حساب‌ها", "تاریخچه تراکنش‌ها")
                ),
                new Service(
                        "🏦",
                        "وام و اعتبارات",
                        "درخواست، مدیریت و پرداخت اقساط وام‌های بانکی",
                        Arrays.asList("درخواست وام", "محاسبه قسط", "پرداخت آنلاین", "مشاهده وضعیت وام")
                ),
                new Service(
                        "💳",
                        "صدور و مدیریت کارت",
                        "صدور کارت‌های بانکی و مدیریت آنها",
                        Arrays.asList("صدور کارت دبیت", "صدور کارت اعتباری", "مسدودسازی کارت", "فعال‌سازی کارت")
                ),
                new Service(
                        "📊",
                        "گزارش‌گیری و آمار",
                        "دسترسی به گزارش‌های مالی کامل و تحلیل‌های دقیق",
                        Arrays.asList("گزارش تراکنش‌ها", "آمار مالی", "نمودارهای تحلیلی", "صورت‌حساب ماهانه")
                ),
                new Service(
                        "🔔",
                        "اعلان‌ها و هشدارها",
                        "دریافت اعلان‌های آنی برای تمام فعالیت‌های حساب",
                        Arrays.asList("SMS اطلاع‌رسانی", "ایمیل تراکنش‌ها", "هشدار موجودی کم", "اعلان تراکنش‌های مشکوک")
                )
        );

        req.setAttribute("services", services);
        req.setAttribute("currentPage", "services");

        req.getRequestDispatcher("/views/welcome.jsp").forward(req, resp);
    }

    /**
     * 📞 صفحه تماس با ما
     */
    private void showContactPage(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        log.info("Contact page accessed");

        // اطلاعات تماس
        req.setAttribute("email", "support@onlinebanking.com");
        req.setAttribute("phone", "021-12345678");
        req.setAttribute("mobile", "0912-345-6789");
        req.setAttribute("address", "تهران، خیابان ولیعصر، پلاک 123، طبقه 5");
        req.setAttribute("workingHours", "شنبه تا چهارشنبه: 8:00 - 17:00 | پنجشنبه: 8:00 - 13:00");
        req.setAttribute("github", "https://github.com/SleepyAmir/onlineBankingSystem");

        // راه‌های ارتباطی
        List<ContactMethod> contactMethods = Arrays.asList(
                new ContactMethod("📧", "ایمیل", "support@onlinebanking.com", "پاسخ‌گویی در کمتر از 24 ساعت"),
                new ContactMethod("☎️", "تلفن", "021-12345678", "پشتیبانی 24 ساعته"),
                new ContactMethod("📱", "موبایل", "0912-345-6789", "ارتباط مستقیم با پشتیبانی"),
                new ContactMethod("💬", "گیت‌هاب", "github.com/SleepyAmir", "گزارش مشکلات فنی")
        );
        req.setAttribute("contactMethods", contactMethods);

        // سوالات متداول
        List<FAQ> faqs = Arrays.asList(
                new FAQ("چگونه می‌توانم حساب کاربری ایجاد کنم؟",
                        "از صفحه اصلی روی دکمه 'ثبت‌نام' کلیک کرده و فرم را پر کنید."),
                new FAQ("آیا استفاده از سیستم امن است؟",
                        "بله، از رمزنگاری پیشرفته و احراز هویت چندمرحله‌ای استفاده می‌کنیم."),
                new FAQ("چطور می‌توانم رمز عبورم را تغییر دهم؟",
                        "از صفحه پروفایل خود می‌توانید رمز عبور را تغییر دهید."),
                new FAQ("آیا هزینه‌ای برای استفاده از سیستم وجود دارد؟",
                        "خیر، استفاده از سیستم کاملاً رایگان است.")
        );
        req.setAttribute("faqs", faqs);

        req.setAttribute("currentPage", "contact");

        req.getRequestDispatcher("/views/welcome.jsp").forward(req, resp);
    }

    /**
     * 📨 پردازش فرم تماس
     */
    private void handleContactForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String subject = req.getParameter("subject");
        String message = req.getParameter("message");

        // اعتبارسنجی
        if (name == null || name.isBlank() ||
                email == null || email.isBlank() ||
                message == null || message.isBlank()) {

            req.setAttribute("error", "لطفاً همه فیلدهای الزامی را پر کنید");
            showContactPage(req, resp);
            return;
        }

        // Log کردن پیام (در پروژه واقعی باید ذخیره بشه یا ایمیل بشه)
        log.info("Contact form submitted - Name: {}, Email: {}, Subject: {}",
                name, email, subject != null ? subject : "No subject");

        // نمایش پیام موفقیت
        req.setAttribute("success", "پیام شما با موفقیت ارسال شد! به زودی با شما تماس خواهیم گرفت.");
        showContactPage(req, resp);
    }

    // ==================== کلاس‌های کمکی ====================

    /**
     * ویژگی سیستم
     */
    public static class Feature {
        public String icon;
        public String title;
        public String description;

        public Feature(String icon, String title, String description) {
            this.icon = icon;
            this.title = title;
            this.description = description;
        }

        public String getIcon() { return icon; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
    }

    /**
     * عضو تیم
     */
    public static class TeamMember {
        public String name;
        public String position;
        public String expertise;

        public TeamMember(String name, String position, String expertise) {
            this.name = name;
            this.position = position;
            this.expertise = expertise;
        }

        public String getName() { return name; }
        public String getPosition() { return position; }
        public String getExpertise() { return expertise; }
    }

    /**
     * ارزش‌های سازمانی
     */
    public static class Value {
        public String icon;
        public String title;
        public String description;

        public Value(String icon, String title, String description) {
            this.icon = icon;
            this.title = title;
            this.description = description;
        }

        public String getIcon() { return icon; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
    }

    /**
     * خدمات
     */
    public static class Service {
        public String icon;
        public String title;
        public String description;
        public List<String> features;

        public Service(String icon, String title, String description, List<String> features) {
            this.icon = icon;
            this.title = title;
            this.description = description;
            this.features = features;
        }

        public String getIcon() { return icon; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public List<String> getFeatures() { return features; }
    }

    /**
     * روش‌های تماس
     */
    public static class ContactMethod {
        public String icon;
        public String title;
        public String value;
        public String description;

        public ContactMethod(String icon, String title, String value, String description) {
            this.icon = icon;
            this.title = title;
            this.value = value;
            this.description = description;
        }

        public String getIcon() { return icon; }
        public String getTitle() { return title; }
        public String getValue() { return value; }
        public String getDescription() { return description; }
    }

    /**
     * سوالات متداول
     */
    public static class FAQ {
        public String question;
        public String answer;

        public FAQ(String question, String answer) {
            this.question = question;
            this.answer = answer;
        }

        public String getQuestion() { return question; }
        public String getAnswer() { return answer; }
    }
}
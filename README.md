```markdown
🏦 سیستم بانکداری آنلاین - OnlineBankingSystem
سیستم جامع بانکداری آنلاین با Jakarta EE
ویژگی‌ها • نصب • استفاده • API • مشارکت
📑 فهرست

* درباره پروژه

* ویژگی‌ها

* تکنولوژی‌ها

* پیش‌نیازها

* نصب و راه‌اندازی

* پیکربندی

* استفاده

* ساختار پروژه

* مستندات API

* مشارکت

🎯 درباره پروژه
یک سیستم بانکداری آنلاین امن و جامع که با استفاده از Jakarta EE پیاده‌سازی شده است. این پروژه شامل مدیریت کاربران، حساب‌های بانکی، کارت‌ها، وام‌ها و تراکنش‌ها می‌شود. با تمرکز روی امنیت، عملکرد و کاربری آسان، مناسب برای بانکداری دیجیتال است.

* ✅ مدیریت کاربران با نقش‌های مختلف

* ✅ ایجاد و مدیریت حساب‌های بانکی

* ✅ صدور و کنترل کارت‌ها

* ✅ درخواست و مدیریت وام‌ها با محاسبه قسط

* ✅ تراکنش‌های مالی (واریز، برداشت، انتقال)

* ✅ REST API کامل

* ✅ رابط کاربری ساده با JSP

* ✅ پشتیبانی از زبان فارسی

✨ ویژگی‌ها
🔐 احراز هویت و امنیت

* ✓ ثبت‌نام و ورود امن با هشینگ رمز عبور

* ✓ نقش‌ها: Admin, Manager, Customer

* ✓ کنترل دسترسی مبتنی بر نقش

* ✓ حذف نرم (Soft Delete) برای تمام موجودیت‌ها

🏦 مدیریت حساب‌ها

* ✓ ایجاد حساب با موجودی اولیه

* ✓ به‌روزرسانی و تغییر وضعیت حساب

* ✓ جستجو بر اساس کاربر یا شماره حساب

* ✓ حذف حساب با چک موجودی

💳 مدیریت کارت‌ها

* ✓ صدور کارت جدید با CVV و تاریخ انقضا

* ✓ فعال/غیرفعال کردن کارت

* ✓ جستجو بر اساس حساب یا شماره کارت

* ✓ ماسکینگ شماره کارت برای امنیت

💰 مدیریت وام‌ها

* ✓ درخواست وام با محاسبه اتوماتیک قسط ماهانه

* ✓ تأیید/رد وام توسط مدیر

* ✓ پرداخت قسط و تغییر وضعیت وام

* ✓ جستجو بر اساس وضعیت (Pending, Approved, Active)

🔄 مدیریت تراکنش‌ها

* ✓ واریز، برداشت و انتقال وجه

* ✓ انتقال با شماره کارت

* ✓ تولید ID منحصربه‌فرد برای تراکنش

* ✓ تاریخچه تراکنش‌ها با جستجو

🔧 قابلیت‌های فنی

* ✓ REST API با JAX-RS

* ✓ اعتبارسنجی داده‌ها

* ✓ لاگینگ با Logback و SLF4J

* ✓ داده‌های اولیه با DataInitializer

* ✓ پشتیبانی از Oracle/MySQL

🛠 تکنولوژی‌ها
Backend
تکنولوژینسخهکاربردJava11زبان برنامه‌نویسیJakarta EE9.1فریمورک EnterpriseHibernate6.2ORM FrameworkOracle DB11g+ یا MySQLپایگاه دادهMaven3.8+Build ToolLombok1.18کاهش BoilerplateLogback1.5.16لاگینگSLF4J2.0.13لاگینگGson2.10.1JSON ProcessingJUnit5.10.2تستینگMockito5.12.0ماکینگ
Frontend
تکنولوژینسخهکاربردJSP/JSTL3.0Server-side RenderingBootstrap5.3UI Framework (RTL)JavaScriptES6+تعاملات کاربریCSS3-استایل و انیمیشن
Application Server

* ✅ Apache TomEE 9.x (پیشنهادی)

* ✅ WildFly 26+

* ✅ Payara Server 6+

📦 پیش‌نیازها
قبل از شروع، موارد زیر را نصب کنید:
1. Java Development Kit

```
# نصب JDK 11
# بررسی نسخه
java -version
# خروجی: java version "11.x.x"
```

2. Apache Maven

```
# بررسی نسخه Maven
mvn -version
# خروجی: Apache Maven 3.8.x
```

3. پایگاه داده
Oracle Database 11g+ یا MySQL 8.0+

```
-- ایجاد دیتابیس
CREATE DATABASE onlinebankingsystem;
```

4. Application Server
Apache TomEE 9.x (توصیه می‌شود)

```
# دانلود از
https://tomee.apache.org/download-ng.html
```

🚀 نصب و راه‌اندازی
مرحله 1: دریافت پروژه

```
git clone https://github.com/yourusername/onlineBankingSystem.git
cd onlineBankingSystem
```

مرحله 2: راه‌اندازی دیتابیس
برای Oracle:

```
-- اتصال به Oracle به عنوان SYSDBA
sqlplus / as sysdba

-- ایجاد کاربر
CREATE USER sleepy IDENTIFIED BY sleepy123;
GRANT CONNECT, RESOURCE, DBA TO sleepy;
GRANT UNLIMITED TABLESPACE TO sleepy;
EXIT;
```

برای MySQL:

```
-- اتصال به MySQL
mysql -u root -p

-- ایجاد دیتابیس و کاربر
CREATE DATABASE onlinebankingsystem
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

CREATE USER 'sleepy'@'localhost'
IDENTIFIED BY 'sleepy123';

GRANT ALL PRIVILEGES ON onlinebankingsystem.*
TO 'sleepy'@'localhost';

FLUSH PRIVILEGES;
EXIT;
```

مرحله 3: پیکربندی اتصال
فایل src/main/resources/META-INF/persistence.xml را ویرایش کنید:
برای Oracle:

```
<property name="hibernate.dialect" value="org.hibernate.dialect.OracleDialect"/>
```

برای MySQL:

```
<property name="hibernate.dialect" value="org.hibernate.dialect.MySQLDialect"/>
```

فایل src/main/webapp/WEB-INF/resources.xml برای datasource:

```
<Resource id="bankDataSource" type="DataSource">
    JdbcDriver = oracle.jdbc.OracleDriver
    JdbcUrl = jdbc:oracle:thin:@localhost:1521:XE
    UserName = sleepy
    Password = sleepy123
    JtaManaged = true
</Resource>
```

مرحله 4: Build پروژه

```
# پاک‌سازی و Build
mvn clean package

# خروجی:
# [INFO] Building war: .../target/onlineBankingSystem-1.0-SNAPSHOT.war
# [INFO] BUILD SUCCESS
```

مرحله 5: Deploy
با TomEE:

```
# کپی فایل WAR
cp target/onlineBankingSystem-1.0-SNAPSHOT.war $TOMEE_HOME/webapps/

# راه‌اندازی سرور
cd $TOMEE_HOME/bin
./startup.sh    # Linux/Mac
# یا
startup.bat     # Windows
```

مرحله 6: دسترسی به برنامه
مرورگر را باز کرده و به آدرس زیر بروید:

```
http://localhost:80/onlineBankingSystem/
```

🔧 پیکربندی
حساب‌های پیش‌فرض
سیستم با 4 حساب آزمایشی راه‌اندازی می‌شود (از DataInitializer):
نقشنام کاربریرمز عبوردسترسی‌ها👑 Adminadmin123456دسترسی کامل👔 Managermanager123456مدیریت وام‌ها👤 Customer1amir123456عملیات پایه👤 Customer2sara123456عملیات پایه
📖 استفاده
سناریوی کاری استاندارد
1️⃣ ورود به سیستم
به صفحه login.jsp بروید
از حساب‌های آزمایشی استفاده کنید یا ثبت‌نام کنید
پس از ورود، به داشبورد منتقل می‌شوید
2️⃣ ایجاد حساب بانکی

```
داشبورد → حساب‌ها → ایجاد حساب جدید

- کاربر: انتخاب از لیست
- نوع: SAVINGS
- موجودی اولیه: 1000000
- ذخیره
```

3️⃣ صدور کارت

```
کارت‌ها → صدور کارت جدید

- حساب: انتخاب حساب
- نوع: DEBIT
- ذخیره
```

4️⃣ درخواست وام

```
وام‌ها → درخواست وام

- حساب: انتخاب
- مبلغ اصل: 5000000
- نرخ بهره: 12
- مدت: 12 ماه
- ذخیره
```

5️⃣ تأیید وام (به عنوان مدیر)

```
وام‌ها → لیست وام‌ها → تأیید وام
```

6️⃣ انتقال وجه

```
تراکنش‌ها → انتقال وجه

- کارت مبدأ: انتخاب
- کارت مقصد: انتخاب
- مبلغ: 10000
- توضیح: تست
- ذخیره
```

استفاده از REST API
دریافت لیست حساب‌ها

```
curl -X GET http://localhost:80/api/accounts \
  -H "Content-Type: application/json"
```

ایجاد تراکنش انتقال

```
curl -X POST http://localhost:80/api/transactions/transfer \
-H "Content-Type: application/json" \
-d '{
"fromCardNumber": "6037...",
"toCardNumber": "6037...",
"amount": 5000
}'
```

📁 ساختار پروژه

```
onlineBankingSystem/
│
├───.idea
│   └───dataSources
│       └───6ad6ec74-e8f0-4aa9-93e5-214e1125b2dd
│           └───storage_v2
│               └───_src_
│                   └───schema
├───.mvn
│   └───wrapper
├───src
│   ├───main
│   │   ├───java
│   │   │   └───com
│   │   │       └───sleepy
│   │   │           └───onlinebankingsystem
│   │   │               ├───config
│   │   │               ├───controller
│   │   │               │   ├───api
│   │   │               │   └───servlet
│   │   │               ├───exception
│   │   │               ├───filter
│   │   │               ├───model
│   │   │               │   ├───dto
│   │   │               │   │   ├───request
│   │   │               │   │   └───response
│   │   │               │   ├───entity
│   │   │               │   └───enums
│   │   │               ├───repository
│   │   │               ├───security
│   │   │               ├───service
│   │   │               │   └───impl
│   │   │               ├───session
│   │   │               ├───tools
│   │   │               ├───utils
│   │   │               └───validation
│   │   ├───resources
│   │   │   └───META-INF
│   │   └───webapp
│   │       ├───css
│   │       ├───error
│   │       ├───js
│   │       ├───views
│   │       │   ├───accounts
│   │       │   ├───auth
│   │       │   ├───cards
│   │       │   ├───common
│   │       │   ├───dashboard
│   │       │   ├───loans
│   │       │   ├───transactions
│   │       │   └───users
│   │       └───WEB-INF
│   │           └───lib
│   └───test
│       ├───java
│       │   └───com
│       │       └───sleepy
│       │           └───onlinebankingsystem
│       └───resources
└───target
    ├───classes
    │   ├───com
    │   │   └───sleepy
    │   │       └───onlinebankingsystem
    │   │           ├───config
    │   │           ├───controller
    │   │           │   ├───api
    │   │           │   └───servlet
    │   │           ├───exception
    │   │           ├───filter
    │   │           ├───model
    │   │           │   ├───dto
    │   │           │   │   ├───request
    │   │           │   │   └───response
    │   │           │   ├───entity
    │   │           │   └───enums
    │   │           ├───repository
    │   │           ├───security
    │   │           ├───service
    │   │           │   └───impl
    │   │           ├───tools
    │   │           └───utils
    │   └───META-INF
    ├───generated-sources
    │   └───annotations
    ├───generated-test-sources
    │   └───test-annotations
    ├───onlineBankingSystem-1.0-SNAPSHOT
    │   ├───error
    │   ├───jsp
    │   ├───META-INF
    │   ├───templates
    │   ├───views
    │   │   ├───accounts
    │   │   ├───auth
    │   │   ├───cards
    │   │   ├───common
    │   │   ├───dashboard
    │   │   ├───loans
    │   │   ├───transactions
    │   │   └───users
    │   └───WEB-INF
    │       ├───classes
    │       │   ├───com
    │       │   │   └───sleepy
    │       │   │       └───onlinebankingsystem
    │       │   │           ├───config
    │       │   │           ├───controller
    │       │   │           │   ├───api
    │       │   │           │   └───servlet
    │       │   │           ├───exception
    │       │   │           ├───filter
    │       │   │           ├───model
    │       │   │           │   ├───dto
    │       │   │           │   │   ├───request
    │       │   │           │   │   └───response
    │       │   │           │   ├───entity
    │       │   │           │   └───enums
    │       │   │           ├───repository
    │       │   │           ├───security
    │       │   │           ├───service
    │       │   │           │   └───impl
    │       │   │           ├───tools
    │       │   │           └───utils
    │       │   └───META-INF
    │       └───lib
    └───test-classes
```

🌐 مستندات API
Account API
MethodEndpointتوضیحاتGET/api/accountsدریافت تمام حساب‌هاGET/api/accounts/{id}دریافت حساب با IDPOST/api/accountsایجاد حساب جدیدPUT/api/accounts/{id}به‌روزرسانی حسابDELETE/api/accounts/{id}حذف حساب
Card API
MethodEndpointتوضیحاتPOST/api/cardsصدور کارت جدیدGET/api/cardsدریافت تمام کارت‌هاPOST/api/cards/{id}/blockمسدودسازی کارت
Loan API
MethodEndpointتوضیحاتPOST/api/loans/applyدرخواست وامGET/api/loansدریافت تمام وام‌هاPOST/api/loans/{id}/approveتأیید وامPOST/api/loans/{id}/payپرداخت قسط
Transaction API
MethodEndpointتوضیحاتPOST/api/transactions/depositواریزPOST/api/transactions/withdrawalبرداشتPOST/api/transactions/transferانتقال با کارتGET/api/transactionsدریافت تراکنش‌ها
User API
MethodEndpointتوضیحاتPOST/api/users/registerثبت‌نامPOST/api/usersایجاد کاربر (ادمین)GET/api/usersدریافت کاربرانPUT/api/users/{id}به‌روزرسانیDELETE/api/users/{id}حذف
🤝 مشارکت
مشارکت شما در بهبود این پروژه بسیار ارزشمند است!
راهنمای مشارکت

1. Fork کردن پروژه

2. کلون کردن Fork

```
git clone https://github.com/YOUR-USERNAME/onlineBankingSystem.git
cd onlineBankingSystem
```

3. ایجاد Branch جدید

```
git checkout -b feature/new-feature
```

4. Commit و Push تغییرات

```
git add .
git commit -m "✨ Add new feature"
git push origin feature/new-feature
```

5. ایجاد Pull Request

👨‍💻 توسعه‌دهنده
[نام شما]
⬆ بازگشت به بالا
```
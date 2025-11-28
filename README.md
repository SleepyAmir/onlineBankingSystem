<div align="center">

# 🏦 سیستم بانکداری آنلاین | Online Banking System

<img src="https://img.shields.io/badge/Java-11-orange?style=for-the-badge&logo=java" />
<img src="https://img.shields.io/badge/Jakarta%20EE-9.1-blue?style=for-the-badge&logo=oracle" />
<img src="https://img.shields.io/badge/Hibernate-6.2-59666C?style=for-the-badge&logo=hibernate" />
<img src="https://img.shields.io/badge/Oracle-DB-red?style=for-the-badge&logo=oracle" />
<img src="https://img.shields.io/badge/TomEE-9.x-yellow?style=for-the-badge" />

**یک سیستم بانکداری آنلاین جامع و امن با معماری Enterprise**

[ویژگی‌ها](#-ویژگیها) • [نصب](#-نصب-و-راهاندازی) • [مستندات](#-مستندات-api) • [دمو](#-نمایش-پروژه)

---
<img src="/docs/screenshots/dashboard.png" alt="Register Page" width="800"/>

</div>

## 📑 فهرست مطالب

- [معرفی پروژه](#-معرفی-پروژه)
- [ویژگی‌ها](#-ویژگیها)
- [تکنولوژی‌ها](#️-تکنولوژیها)
- [معماری سیستم](#-معماری-سیستم)
- [پیش‌نیازها](#-پیشنیازها)
- [نصب و راه‌اندازی](#-نصب-و-راهاندازی)
- [پیکربندی](#️-پیکربندی)
- [راهنمای استفاده](#-راهنمای-استفاده)
- [API Documentation](#-مستندات-api)
- [ساختار پروژه](#-ساختار-پروژه)
- [امنیت](#-امنیت)
- [تست](#-تست)
- [مشارکت](#-مشارکت)
- [مجوز](#-مجوز)

---

## 🎯 معرفی پروژه

**سیستم بانکداری آنلاین** یک پلتفرم کامل و امن برای مدیریت عملیات بانکی است که با استفاده از تکنولوژی‌های **Jakarta EE** و معماری **Enterprise** طراحی شده است.

### 💡 چرا این پروژه؟

- ✅ **معماری حرفه‌ای**: استفاده از Design Patterns و Best Practices
- ✅ **امنیت بالا**: رمزنگاری BCrypt، JWT Authentication، CSRF Protection
- ✅ **مقیاس‌پذیری**: معماری لایه‌ای قابل توسعه
- ✅ **مستندسازی کامل**: API Documentation و Code Comments
- ✅ **پشتیبانی از Farsi/RTL**: رابط کاربری فارسی کامل

---

## ✨ ویژگی‌ها

### 🔐 مدیریت کاربران و امنیت

| ویژگی | توضیحات |
|------|---------|
| 🔑 **احراز هویت** | ثبت‌نام، ورود، خروج با BCrypt Hashing |
| 👥 **نقش‌های کاربری** | Admin, Manager, Customer با دسترسی‌های متفاوت |
| 🛡️ **امنیت پیشرفته** | JWT Token, Session Management, CSRF Protection |
| 📊 **Audit Trail** | ثبت تمام فعالیت‌ها با Timestamp |

### 💰 مدیریت مالی

<table>
<tr>
<td width="50%">

**🏦 حساب‌های بانکی**
- ایجاد حساب جاری/پس‌انداز
- مدیریت موجودی
- تغییر وضعیت (فعال/مسدود/بسته)
- شماره حساب یکتا 16 رقمی

</td>
<td width="50%">

**💳 کارت‌های بانکی**
- صدور کارت دبیت/اعتباری
- تولید CVV و تاریخ انقضا
- فعال/مسدودسازی کارت
- ماسکینگ شماره کارت

</td>
</tr>
<tr>
<td>

**💸 تراکنش‌های مالی**
- واریز و برداشت وجه
- انتقال بین حساب‌ها
- انتقال با شماره کارت
- تاریخچه کامل تراکنش‌ها

</td>
<td>

**🏦 وام و اعتبارات**
- درخواست وام
- محاسبه قسط ماهانه
- تأیید/رد وام توسط مدیر
- پرداخت اقساط آنلاین

</td>
</tr>
</table>

### 📊 ویژگی‌های پیشرفته

```
✓ داشبورد اختصاصی برای هر نقش (Admin/Manager/Customer)
✓ گزارش‌گیری پیشرفته با فیلترهای زمانی
✓ جستجوی پیشرفته در تمام بخش‌ها
✓ صفحه‌بندی (Pagination) در لیست‌ها
✓ Soft Delete برای تمام موجودیت‌ها
✓ Validation در سمت Client و Server
✓ پشتیبانی از REST API کامل
✓ Responsive Design با Bootstrap 5
```

---

## 🛠️ تکنولوژی‌ها

### Backend Stack

```yaml
Language: Java 11
Framework: Jakarta EE 9.1
ORM: Hibernate 6.2
Database: Oracle 11g+ / MySQL 8.0+
Build Tool: Maven 3.8+
Security: BCrypt, JWT, CSRF Protection
Logging: Logback + SLF4J
```

### Frontend Stack

```yaml
Template Engine: JSP/JSTL 3.0
CSS Framework: Bootstrap 5.3 (RTL)
JavaScript: ES6+ (Vanilla)
Icons: Font Awesome / Custom
```

### Application Server

```yaml
Primary: Apache TomEE 9.x (Jakarta EE 9.1)
Alternative: WildFly 26+, Payara Server 6+
```

### Dependencies Overview

| Category | Library | Version | Purpose |
|----------|---------|---------|---------|
| Core | Jakarta EE API | 9.1.0 | Enterprise APIs |
| Persistence | Hibernate | 6.2 | ORM Framework |
| Security | BCrypt | 0.4 | Password Hashing |
| Auth | JWT (jjwt) | 0.11.5 | Token Authentication |
| JSON | Gson | 2.10.1 | JSON Processing |
| Logging | Logback | 1.5.16 | Application Logging |
| Testing | JUnit 5 | 5.10.2 | Unit Testing |
| Testing | Mockito | 5.12.0 | Mocking Framework |

---

## 🏗 معماری سیستم

### Layered Architecture

```
┌─────────────────────────────────────────────────────┐
│                 Presentation Layer                   │
│  (Servlets, JSP, REST APIs, Filters)                │
├─────────────────────────────────────────────────────┤
│                   Service Layer                      │
│  (Business Logic, Transaction Management)           │
├─────────────────────────────────────────────────────┤
│                 Repository Layer                     │
│  (Data Access, JPA/Hibernate)                       │
├─────────────────────────────────────────────────────┤
│                  Database Layer                      │
│  (Oracle DB / MySQL)                                │
└─────────────────────────────────────────────────────┘
```

### Design Patterns

- **Repository Pattern**: جداسازی لایه دیتابیس
- **Service Pattern**: مدیریت Business Logic
- **DTO Pattern**: انتقال داده بین لایه‌ها
- **Builder Pattern**: ساخت اشیاء پیچیده
- **Singleton Pattern**: مدیریت Session و Configuration

---

## 📋 پیش‌نیازها

### نرم‌افزارهای مورد نیاز

```bash
# 1. Java Development Kit
☑ JDK 11 or higher
  Download: https://adoptium.net/

# 2. Apache Maven
☑ Maven 3.8+
  Download: https://maven.apache.org/download.cgi

# 3. Database (یکی از موارد زیر)
☑ Oracle Database 11g+ OR MySQL 8.0+

# 4. Application Server
☑ Apache TomEE 9.x (Recommended)
  Download: https://tomee.apache.org/download-ng.html
```

### بررسی نصب

```bash
# Java
java -version
# Expected: java version "11.x.x"

# Maven
mvn -version
# Expected: Apache Maven 3.8.x

# Oracle (optional)
sqlplus / as sysdba
# OR MySQL
mysql --version
```

---

## 🚀 نصب و راه‌اندازی

### مرحله 1️⃣: دریافت پروژه

```bash
# Clone the repository
git clone https://github.com/yourusername/onlineBankingSystem.git
cd onlineBankingSystem
```

### مرحله 2️⃣: راه‌اندازی دیتابیس

<details>
<summary><b>🔸 Oracle Database</b></summary>

```sql
-- 1. اتصال به Oracle
sqlplus / as sysdba

-- 2. ایجاد کاربر
CREATE USER sleepy IDENTIFIED BY sleepy123;
GRANT CONNECT, RESOURCE, DBA TO sleepy;
GRANT UNLIMITED TABLESPACE TO sleepy;

-- 3. بررسی اتصال
CONNECT sleepy/sleepy123;
SELECT USER FROM DUAL;
-- Expected: SLEEPY

EXIT;
```

</details>

<details>
<summary><b>🔸 MySQL Database</b></summary>

```sql
-- 1. اتصال به MySQL
mysql -u root -p

-- 2. ایجاد دیتابیس
CREATE DATABASE onlinebankingsystem
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- 3. ایجاد کاربر
CREATE USER 'sleepy'@'localhost' 
IDENTIFIED BY 'sleepy123';

GRANT ALL PRIVILEGES ON onlinebankingsystem.* 
TO 'sleepy'@'localhost';

FLUSH PRIVILEGES;

-- 4. بررسی
USE onlinebankingsystem;
SELECT DATABASE();
-- Expected: onlinebankingsystem

EXIT;
```

</details>

### مرحله 3️⃣: پیکربندی دیتابیس

**برای Oracle:**

`src/main/resources/META-INF/persistence.xml`:

```xml
<property name="hibernate.dialect" 
          value="org.hibernate.dialect.OracleDialect"/>
```

`src/main/resources/tomee-resources.xml`:

```xml
<Resource id="jdbc/JtaDataSource" type="DataSource">
    jdbcDriver = oracle.jdbc.driver.OracleDriver
    jdbcUrl = jdbc:oracle:thin:@localhost:1521:xe
    username = javaee
    password = java123
    jtaManaged = true
</Resource>
```

**برای MySQL:**

`persistence.xml`:

```xml
<property name="hibernate.dialect" 
          value="org.hibernate.dialect.MySQLDialect"/>
```

`tomee-resources.xml`:

```xml
<Resource id="jdbc/JtaDataSource" type="DataSource">
    jdbcDriver = com.mysql.cj.jdbc.Driver
    jdbcUrl = jdbc:mysql://localhost:3306/onlinebankingsystem
    username = javaee
    password = java123
    jtaManaged = true
</Resource>
```

### مرحله 4️⃣: Build پروژه

```bash
# Clean & Package
mvn clean package

# Output:
# [INFO] Building war: target/onlineBankingSystem-1.0-SNAPSHOT.war
# [INFO] BUILD SUCCESS
# [INFO] Total time: 45.123 s
```

### مرحله 5️⃣: Deploy روی TomEE

```bash
# 1. کپی WAR file
cp target/onlineBankingSystem-1.0-SNAPSHOT.war $TOMEE_HOME/webapps/

# 2. راه‌اندازی سرور
cd $TOMEE_HOME/bin

# Linux/Mac:
./startup.sh

# Windows:
startup.bat

# 3. مشاهده لاگ‌ها
tail -f ../logs/catalina.out
```

### مرحله 6️⃣: دسترسی به برنامه

```
🌐 URL: http://localhost:80/welcome

📧 حساب‌های پیش‌فرض:
```
### مرحله 6️⃣: دسترسی به برنامه
```
🌐 URL: http://localhost:80/welcome

📧 حساب‌های پیش‌فرض:
```

| نقش | نام کاربری | رمز عبور | شماره کارت | دسترسی‌ها |
|-----|------------|----------|-------------|-----------|
| 👑 **Admin** | `admin`    | `123456` | - | دسترسی کامل به سیستم |
| 👤 **Customer** | `amir`     | `123456` | `6037276007625393` | عملیات بانکی پایه |
| 👤 **Customer** | `sara`     | `123456` | `6037523713761494` | عملیات بانکی پایه |

---
---

## ⚙️ پیکربندی

### تنظیمات امنیتی

**BCrypt Rounds** (تعداد دور هشینگ):

```java
// PasswordUtil.java
private static final int BCRYPT_ROUNDS = 12;
```

**JWT Expiration** (مدت اعتبار توکن):

```java
// JwtUtil.java
private static final long EXPIRATION_MINUTES = 15;
```

**Session Timeout**:

```java
// LoginServlet.java
session.setMaxInactiveInterval(15 * 60); // 15 دقیقه
```

### تنظیمات Hibernate

```xml
<!-- persistence.xml -->
<property name="hibernate.hbm2ddl.auto" value="update"/>
<property name="hibernate.show_sql" value="true"/>
<property name="hibernate.format_sql" value="true"/>
```

⚠️ **نکته مهم**: برای محیط Production، `hbm2ddl.auto` را روی `none` قرار دهید.

---

## 📖 راهنمای استفاده

### سناریو 1️⃣: ورود به سیستم

```
1. مراجعه به http://localhost:80/welcome/
2. کلیک روی "ورود به سیستم"
3. وارد کردن نام کاربری و رمز عبور
4. انتقال به داشبورد بر اساس نقش
```

### سناریو 2️⃣: ایجاد حساب بانکی (Admin/Manager)

```
داشبورد → حساب‌ها → ایجاد حساب جدید

📝 اطلاعات مورد نیاز:
  • کاربر: انتخاب از لیست
  • نوع حساب: جاری/پس‌انداز
  • موجودی اولیه: مبلغ (اختیاری)
  
✅ پس از ذخیره: شماره حساب یکتا تولید می‌شود
```

### سناریو 3️⃣: صدور کارت

```
کارت‌ها → صدور کارت جدید

📝 اطلاعات:
  • حساب بانکی: انتخاب حساب فعال
  • نوع کارت: دبیت/اعتباری
  
✅ سیستم خودکار تولید می‌کند:
  - شماره کارت 16 رقمی
  - CVV 3 رقمی
  - تاریخ انقضا (3 سال آینده)
```

### سناریو 4️⃣: انتقال وجه

```
تراکنش‌ها → انتقال وجه

📝 مراحل:
  1. انتخاب کارت مبدأ
  2. وارد کردن شماره کارت/حساب مقصد
  3. مبلغ انتقال
  4. توضیحات (اختیاری)
  
✅ اعتبارسنجی خودکار:
  - بررسی موجودی کافی
  - بررسی وضعیت حساب‌ها
  - تولید کد پیگیری
```

### سناریو 5️⃣: درخواست وام

```
وام‌ها → درخواست وام جدید

📝 اطلاعات:
  • حساب بانکی
  • مبلغ اصل وام
  • نرخ بهره سالانه
  • مدت زمان (ماه)
  
💡 محاسبه خودکار:
  - قسط ماهانه با فرمول ریاضی
  - کل مبلغ قابل پرداخت
  
👔 تأیید مدیر: وام در وضعیت Pending قرار می‌گیرد
```

---

## 🌐 مستندات API

### Base URL

```
http://localhost:80/api
```

### Authentication

```http
POST /api/users/register
Content-Type: application/json

{
  "username": "newuser",
  "password": "password123",
  "firstName": "نام",
  "lastName": "نام خانوادگی",
  "phone": "09121234567",
  "nationalCode": "1234567890"
}
```

### 💳 Account Management

<details>
<summary><b>دریافت تمام حساب‌ها</b></summary>

```http
GET /api/accounts?page=0&size=10
```

**Response:**
```json
{
  "success": true,
  "message": "Success",
  "data": [
    {
      "accountNumber": "1234567890123456",
      "type": "SAVINGS",
      "balance": 10000000,
      "status": "ACTIVE"
    }
  ]
}
```

</details>

<details>
<summary><b>ایجاد حساب جدید</b></summary>

```http
POST /api/accounts
Content-Type: application/json

{
  "userId": 1,
  "type": "SAVINGS",
  "initialBalance": 1000000
}
```

</details>

### 💸 Transaction Management

<details>
<summary><b>واریز وجه</b></summary>

```http
POST /api/transactions/deposit
Content-Type: application/json

{
  "toAccountNumber": "1234567890123456",
  "amount": 500000,
  "description": "واریز نقدی"
}
```

</details>

<details>
<summary><b>انتقال وجه</b></summary>

```http
POST /api/transactions/transfer
Content-Type: application/json

{
  "fromCardNumber": "6037787250994758",
  "toCardNumber": "6037671525818325",
  "amount": 200000,
  "description": "انتقال به دوست"
}
```

</details>

### 🏦 Loan Management

<details>
<summary><b>درخواست وام</b></summary>

```http
POST /api/loans/apply
Content-Type: application/json

{
  "accountNumber": "1234567890123456",
  "principal": 50000000,
  "annualInterestRate": 18.0,
  "durationMonths": 24
}
```

</details>

<details>
<summary><b>تأیید وام (Manager/Admin)</b></summary>

```http
POST /api/loans/{id}/approve
```

</details>

### 📄 کامل API Docs

برای مشاهده مستندات کامل API:

```
📚 Postman Collection: ./docs/API_Collection.json

```

---

## 📁 ساختار پروژه

```
onlineBankingSystem/
│
├── 📂 src/main/
│   ├── 📂 java/com/sleepy/onlinebankingsystem/
│   │   ├── 📂 config/              # پیکربندی و Initializer
│   │   │   ├── DataInitializer.java
│   │   │   └── LoggingInitializer.java
│   │   │
│   │   ├── 📂 controller/
│   │   │   ├── 📂 api/             # REST API Controllers
│   │   │   │   ├── AccountApi.java
│   │   │   │   ├── TransactionApi.java
│   │   │   │   ├── LoanApi.java
│   │   │   │   ├── CardApi.java
│   │   │   │   └── UserApi.java
│   │   │   │
│   │   │   └── 📂 servlet/         # Web Servlets
│   │   │       ├── LoginServlet.java
│   │   │       ├── RegisterServlet.java
│   │   │       ├── AccountServlet.java
│   │   │       └── TransactionServlet.java
│   │   │
│   │   ├── 📂 model/
│   │   │   ├── 📂 entity/          # JPA Entities
│   │   │   │   ├── User.java
│   │   │   │   ├── Account.java
│   │   │   │   ├── Card.java
│   │   │   │   ├── Transaction.java
│   │   │   │   ├── Loan.java
│   │   │   │   └── Base.java (Soft Delete)
│   │   │   │
│   │   │   ├── 📂 dto/             # Data Transfer Objects
│   │   │   │   ├── 📂 request/
│   │   │   │   └── 📂 response/
│   │   │   │
│   │   │   └── 📂 enums/           # Enumerations
│   │   │       ├── UserRole.java
│   │   │       ├── AccountStatus.java
│   │   │       └── TransactionType.java
│   │   │
│   │   ├── 📂 repository/          # Data Access Layer
│   │   │   ├── BaseRepository.java
│   │   │   ├── UserRepository.java
│   │   │   ├── AccountRepository.java
│   │   │   └── ...
│   │   │
│   │   ├── 📂 service/             # Business Logic
│   │   │   ├── 📂 impl/
│   │   │   ├── UserService.java
│   │   │   ├── AccountService.java
│   │   │   └── ...
│   │   │
│   │   ├── 📂 security/            # Security Layer
│   │   │   ├── JwtUtil.java
│   │   │   ├── SessionManager.java
│   │   │   └── PasswordUtil.java
│   │   │
│   │   ├── 📂 filter/              # Servlet Filters
│   │   │   ├── AuthenticationFilter.java
│   │   │   ├── AuthorizationFilter.java
│   │   │   └── CsrfFilter.java
│   │   │
│   │   ├── 📂 exception/           # Custom Exceptions
│   │   └── 📂 utils/               # Utility Classes
│   │
│   ├── 📂 resources/
│   │   ├── 📂 META-INF/
│   │   │   ├── persistence.xml     # JPA Configuration
│   │   │   └── beans.xml           # CDI Configuration
│   │   │
│   │   ├── logback.xml             # Logging Config
│   │   └── tomee-resources.xml     # DataSource Config
│   │
│   └── 📂 webapp/
│       ├── 📂 WEB-INF/
│       │   ├── web.xml
│       │   └── resources.xml
│       │
│       ├── 📂 views/               # JSP Pages
│       │   ├── 📂 auth/
│       │   ├── 📂 accounts/
│       │   ├── 📂 transactions/
│       │   ├── 📂 loans/
│       │   ├── 📂 cards/
│       │   └── 📂 dashboard/
│       │
│       ├── 📂 css/                 # Stylesheets
│       ├── 📂 js/                  # JavaScript
│       └── 📂 images/              # Assets
│
├── 📄 pom.xml                      # Maven Configuration
├── 📄 README.md                    # This file
└── 📄 .gitignore
```

---

## 🔒 امنیت

### لایه‌های امنیتی

```
1️⃣ Authentication Layer
   ├─ BCrypt Password Hashing (12 rounds)
   ├─ JWT Token Management
   └─ Session Management با Timeout

2️⃣ Authorization Layer
   ├─ Role-Based Access Control (RBAC)
   ├─ Fine-Grained Permissions
   └─ Resource-Level Authorization

3️⃣ Data Protection
   ├─ SQL Injection Prevention (Prepared Statements)
   ├─ XSS Protection (Input Validation)
   ├─ CSRF Token Validation
   └─ Secure Password Storage

4️⃣ Network Security
   ├─ HTTPS Ready
   ├─ Secure Headers
   └─ CORS Configuration
```

### Best Practices پیاده‌سازی شده

✅ **Input Validation**: اعتبارسنجی تمام ورودی‌ها در Client و Server  
✅ **Output Encoding**: Escape کردن خروجی‌ها  
✅ **Parameterized Queries**: استفاده از JPA/Hibernate  
✅ **Soft Delete**: حذف منطقی به جای حذف فیزیکی  
✅ **Audit Logging**: ثبت تمام تغییرات با Timestamp  
✅ **Session Management**: Timeout خودکار و Invalidation  
✅ **Error Handling**: پیام‌های خطای امن (بدون افشای اطلاعات)

---

## 🧪 تست

### Unit Testing

```bash
# اجرای تست‌ها
mvn test

# تست با Coverage Report
mvn clean test jacoco:report
```

### Integration Testing

```bash
mvn verify -Pintegration-tests
```

### Manual Testing Checklist

- [ ] ثبت‌نام کاربر جدید
- [ ] ورود با نقش‌های مختلف
- [ ] ایجاد حساب بانکی
- [ ] صدور کارت
- [ ] انتقال وجه
- [ ] درخواست وام
- [ ] تأیید وام (Manager)
- [ ] پرداخت قسط
- [ ] مشاهده تاریخچه تراکنش‌ها
- [ ] تست امنیتی (XSS, CSRF, SQL Injection)

---

## 📊 نمایش پروژه

### صفحه خوش‌آمدگویی

<img src="/docs/screenshots/Welcome.png" alt="Welcome Page" width="800"/>

**ویژگی‌ها:**
- طراحی مدرن و Gradient Background
- نمایش آمار سیستم (کاربران، حساب‌ها، تراکنش‌ها)
- دکمه‌های CTA جذاب
- RTL Support کامل

### صفحه ورود

<img src="/docs/screenshots/Login.png" alt="Login Page" width="800"/>

**امکانات:**
- فرم ورود با Validation
- پیام‌های خطای واضح
- Remember Me


### صفحه ثبت نام
<img src="/docs/screenshots/SignUp.png" alt="Register Page" width="800"/>

**امکانات:**
- فرم ثبت نام با Validation
- پیام‌های خطای واضح
- Remember Me

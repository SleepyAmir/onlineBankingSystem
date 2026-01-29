<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/views/common/header.jsp">
    <jsp:param name="title" value="درخواست وام" />
</jsp:include>

<jsp:include page="/views/common/navbar.jsp" />
<jsp:include page="/views/common/sidebar.jsp" />

<div class="content-wrapper">
    <div class="container-fluid px-4 py-4">
        <h1 class="display-5 fw-bold">درخواست وام جدید</h1>
        <p class="lead text-muted">فرم ثبت درخواست وام</p>

        <!-- پیام خطا -->
        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show">
                <i class="fas fa-exclamation-triangle me-2"></i>${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <!-- کارت راهنما -->
        <div class="card border-info mb-4">
            <div class="card-header bg-info text-white">
                <h5 class="mb-0">
                    <i class="fas fa-info-circle me-2"></i>محدودیت‌های وام
                </h5>
            </div>
            <div class="card-body">
                <div class="row">
                    <div class="col-md-4">
                        <h6 class="text-primary">
                            <i class="fas fa-money-bill-wave me-2"></i>مبلغ وام
                        </h6>
                        <ul class="list-unstyled">
                            <li><strong>حداقل:</strong> 1,000,000 ریال (1 میلیون)</li>
                            <li><strong>حداکثر:</strong> 1,000,000,000 ریال (1 میلیارد)</li>
                        </ul>
                    </div>
                    <div class="col-md-4">
                        <h6 class="text-primary">
                            <i class="fas fa-percent me-2"></i>نرخ بهره
                        </h6>
                        <ul class="list-unstyled">
                            <li><strong>حداقل:</strong> 5%</li>
                            <li><strong>حداکثر:</strong> 30%</li>
                        </ul>
                    </div>
                    <div class="col-md-4">
                        <h6 class="text-primary">
                            <i class="fas fa-calendar-alt me-2"></i>مدت بازپرداخت
                        </h6>
                        <ul class="list-unstyled">
                            <li><strong>حداقل:</strong> 6 ماه</li>
                            <li><strong>حداکثر:</strong> 60 ماه (5 سال)</li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>

        <!-- فرم درخواست -->
        <div class="card shadow-lg border-0 rounded-3">
            <div class="card-header bg-primary text-white">
                <h5 class="mb-0">
                    <i class="fas fa-file-invoice-dollar me-2"></i>فرم درخواست
                </h5>
            </div>
            <div class="card-body p-4">
                <form action="${pageContext.request.contextPath}/loans/apply" method="post" id="loanForm">

                    <!-- انتخاب حساب -->
                    <div class="mb-4">
                        <label for="accountId" class="form-label fw-bold">
                            <i class="fas fa-university me-2"></i>حساب
                            <span class="text-danger">*</span>
                        </label>
                        <select class="form-select form-select-lg" id="accountId" name="accountId" required>
                            <option value="">انتخاب حساب</option>
                            <c:forEach items="${accounts}" var="account">
                                <option value="${account.id}">
                                    <c:out value="${account.accountNumber}" /> -
                                    موجودی: <c:out value="${account.balance}" /> ریال
                                </option>
                            </c:forEach>
                        </select>
                        <div class="form-text">
                            <i class="fas fa-info-circle me-1"></i>
                            حسابی را که می‌خواهید وام به آن واریز شود انتخاب کنید
                        </div>
                    </div>

                    <!-- مبلغ وام -->
                    <div class="mb-4">
                        <label for="principal" class="form-label fw-bold">
                            <i class="fas fa-money-bill-wave me-2"></i>مبلغ وام (ریال)
                            <span class="text-danger">*</span>
                        </label>
                        <input type="number"
                               class="form-control form-control-lg"
                               id="principal"
                               name="principal"
                               required
                               min="1000000"
                               max="1000000000"
                               step="100000"
                               placeholder="مثال: 10000000">
                        <div class="form-text">
                            <i class="fas fa-arrow-down text-success me-1"></i>
                            <strong>حداقل:</strong> 1,000,000 ریال
                            <i class="fas fa-arrow-up text-danger me-1 ms-3"></i>
                            <strong>حداکثر:</strong> 1,000,000,000 ریال
                        </div>
                        <div id="principalDisplay" class="mt-2 text-primary fw-bold"></div>
                    </div>

                    <!-- نرخ بهره -->
                    <div class="mb-4">
                        <label for="interestRate" class="form-label fw-bold">
                            <i class="fas fa-percent me-2"></i>نرخ بهره سالانه (%)
                            <span class="text-danger">*</span>
                        </label>
                        <div class="input-group">
                            <input type="number"
                                   class="form-control form-control-lg"
                                   id="interestRate"
                                   name="interestRate"
                                   required
                                   min="5"
                                   max="30"
                                   step="0.1"
                                   value="15"
                                   placeholder="مثال: 15">
                            <span class="input-group-text">%</span>
                        </div>
                        <div class="form-text">
                            <i class="fas fa-arrow-down text-success me-1"></i>
                            <strong>حداقل:</strong> 5%
                            <i class="fas fa-arrow-up text-danger me-1 ms-3"></i>
                            <strong>حداکثر:</strong> 30%
                        </div>
                        <input type="range"
                               class="form-range mt-2"
                               min="5"
                               max="30"
                               step="0.5"
                               value="15"
                               id="interestRateRange">
                        <div class="d-flex justify-content-between small text-muted">
                            <span>5%</span>
                            <span>15%</span>
                            <span>30%</span>
                        </div>
                    </div>

                    <!-- مدت وام -->
                    <div class="mb-4">
                        <label for="duration" class="form-label fw-bold">
                            <i class="fas fa-calendar-alt me-2"></i>مدت وام (ماه)
                            <span class="text-danger">*</span>
                        </label>
                        <input type="number"
                               class="form-control form-control-lg"
                               id="duration"
                               name="duration"
                               required
                               min="6"
                               max="60"
                               step="1"
                               value="12"
                               placeholder="مثال: 12">
                        <div class="form-text">
                            <i class="fas fa-arrow-down text-success me-1"></i>
                            <strong>حداقل:</strong> 6 ماه
                            <i class="fas fa-arrow-up text-danger me-1 ms-3"></i>
                            <strong>حداکثر:</strong> 60 ماه (5 سال)
                        </div>
                        <input type="range"
                               class="form-range mt-2"
                               min="6"
                               max="60"
                               step="6"
                               value="12"
                               id="durationRange">
                        <div class="d-flex justify-content-between small text-muted">
                            <span>6 ماه</span>
                            <span>30 ماه</span>
                            <span>60 ماه</span>
                        </div>
                    </div>

                    <!-- کارت محاسبه تخمینی -->
                    <div class="card bg-light border-0 mb-4" id="estimateCard" style="display: none;">
                        <div class="card-body">
                            <h6 class="text-primary mb-3">
                                <i class="fas fa-calculator me-2"></i>محاسبه تخمینی قسط ماهانه
                            </h6>
                            <div class="row text-center">
                                <div class="col-md-4">
                                    <div class="p-3 bg-white rounded">
                                        <small class="text-muted d-block mb-2">قسط ماهانه (تقریبی)</small>
                                        <h4 class="text-primary mb-0" id="monthlyPaymentEstimate">-</h4>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="p-3 bg-white rounded">
                                        <small class="text-muted d-block mb-2">کل بازپرداخت</small>
                                        <h5 class="text-warning mb-0" id="totalPaymentEstimate">-</h5>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="p-3 bg-white rounded">
                                        <small class="text-muted d-block mb-2">کل سود</small>
                                        <h5 class="text-danger mb-0" id="totalInterestEstimate">-</h5>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- دکمه‌های عملیات -->
                    <div class="d-flex gap-2">
                        <button type="submit" class="btn btn-primary btn-lg flex-fill">
                            <i class="fas fa-hand-holding-usd me-2"></i>ثبت درخواست
                        </button>
                        <a href="${pageContext.request.contextPath}/loans/list"
                           class="btn btn-secondary btn-lg flex-fill">
                            <i class="fas fa-times me-2"></i>لغو
                        </a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- اسکریپت‌های JavaScript -->
<script>
    // همگام‌سازی اسلایدر نرخ بهره با ورودی
    const interestRateInput = document.getElementById('interestRate');
    const interestRateRange = document.getElementById('interestRateRange');

    interestRateRange.addEventListener('input', function() {
        interestRateInput.value = this.value;
        calculateEstimate();
    });

    interestRateInput.addEventListener('input', function() {
        if (this.value >= 5 && this.value <= 30) {
            interestRateRange.value = this.value;
        }
        calculateEstimate();
    });

    // همگام‌سازی اسلایدر مدت وام با ورودی
    const durationInput = document.getElementById('duration');
    const durationRange = document.getElementById('durationRange');

    durationRange.addEventListener('input', function() {
        durationInput.value = this.value;
        calculateEstimate();
    });

    durationInput.addEventListener('input', function() {
        if (this.value >= 6 && this.value <= 60) {
            durationRange.value = this.value;
        }
        calculateEstimate();
    });

    // نمایش فرمت عدد مبلغ وام
    const principalInput = document.getElementById('principal');
    const principalDisplay = document.getElementById('principalDisplay');

    principalInput.addEventListener('input', function() {
        const value = parseInt(this.value);
        if (!isNaN(value) && value > 0) {
            principalDisplay.textContent = value.toLocaleString('fa-IR') + ' ریال';
            calculateEstimate();
        } else {
            principalDisplay.textContent = '';
        }
    });

    // محاسبه تخمینی قسط ماهانه
    function calculateEstimate() {
        const principal = parseFloat(principalInput.value);
        const rate = parseFloat(interestRateInput.value);
        const months = parseInt(durationInput.value);

        if (isNaN(principal) || isNaN(rate) || isNaN(months) ||
            principal < 1000000 || rate < 5 || months < 6) {
            document.getElementById('estimateCard').style.display = 'none';
            return;
        }

        // محاسبه قسط ماهانه با فرمول استاندارد
        const monthlyRate = (rate / 12) / 100;
        const monthlyPayment = monthlyRate === 0
            ? principal / months
            : principal * (monthlyRate * Math.pow(1 + monthlyRate, months)) /
            (Math.pow(1 + monthlyRate, months) - 1);

        const totalPayment = monthlyPayment * months;
        const totalInterest = totalPayment - principal;

        // نمایش نتایج
        document.getElementById('monthlyPaymentEstimate').textContent =
            Math.round(monthlyPayment).toLocaleString('fa-IR') + ' ریال';
        document.getElementById('totalPaymentEstimate').textContent =
            Math.round(totalPayment).toLocaleString('fa-IR') + ' ریال';
        document.getElementById('totalInterestEstimate').textContent =
            Math.round(totalInterest).toLocaleString('fa-IR') + ' ریال';

        document.getElementById('estimateCard').style.display = 'block';
    }

    // اعتبارسنجی فرم قبل از ارسال
    document.getElementById('loanForm').addEventListener('submit', function(e) {
        const principal = parseFloat(principalInput.value);
        const rate = parseFloat(interestRateInput.value);
        const months = parseInt(durationInput.value);

        let errors = [];

        // بررسی مبلغ وام
        if (principal < 1000000) {
            errors.push('مبلغ وام نمی‌تواند کمتر از 1,000,000 ریال باشد');
        }
        if (principal > 1000000000) {
            errors.push('مبلغ وام نمی‌تواند بیشتر از 1,000,000,000 ریال باشد');
        }

        // بررسی نرخ بهره
        if (rate < 5 || rate > 30) {
            errors.push('نرخ بهره باید بین 5 تا 30 درصد باشد');
        }

        // بررسی مدت وام
        if (months < 6 || months > 60) {
            errors.push('مدت وام باید بین 6 تا 60 ماه باشد');
        }

        // نمایش خطاها
        if (errors.length > 0) {
            e.preventDefault();
            alert('❌ خطاهای اعتبارسنجی:\n\n' + errors.join('\n'));
            return false;
        }

        // تأیید نهایی
        const confirmMsg = 'آیا از ثبت درخواست وام با مشخصات زیر اطمینان دارید؟\n\n' +
            '💰 مبلغ وام: ' + principal.toLocaleString('fa-IR') + ' ریال\n' +
            '📊 نرخ بهره: ' + rate + '%\n' +
            '📅 مدت: ' + months + ' ماه\n' +
            '💳 قسط ماهانه (تقریبی): ' +
            document.getElementById('monthlyPaymentEstimate').textContent;

        if (!confirm(confirmMsg)) {
            e.preventDefault();
            return false;
        }

        return true;
    });

    // محاسبه اولیه در صورت وجود مقادیر پیش‌فرض
    calculateEstimate();
</script>

<jsp:include page="/views/common/footer.jsp" />

<%-- ✅ payment.jsp - نسخه نهایی با Service جدید --%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/views/common/header.jsp">
    <jsp:param name="title" value="پرداخت قسط وام" />
</jsp:include>

<jsp:include page="/views/common/navbar.jsp" />
<jsp:include page="/views/common/sidebar.jsp" />

<div class="content-wrapper">
    <div class="container-fluid px-4 py-4">
        <h1 class="display-5 fw-bold">پرداخت قسط وام</h1>
        <p class="lead text-muted">شماره وام: <c:out value="${loan.loanNumber}" /></p>

        <!-- پیام خطا -->
        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show">
                <i class="fas fa-exclamation-triangle me-2"></i>${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <div class="row mb-4">
            <!-- کارت اطلاعات وام -->
            <div class="col-md-6">
                <div class="card border-primary h-100 shadow-sm">
                    <div class="card-header bg-primary text-white">
                        <h5 class="mb-0">
                            <i class="fas fa-file-invoice-dollar me-2"></i>اطلاعات وام
                        </h5>
                    </div>
                    <div class="card-body">
                        <table class="table table-borderless mb-3">
                            <tr>
                                <th width="50%">مبلغ اصل وام:</th>
                                <td class="text-end">
                                    <fmt:formatNumber value="${loan.principal}" type="number" groupingUsed="true"/> ریال
                                </td>
                            </tr>
                            <tr>
                                <th>مبلغ باقیمانده:</th>
                                <td class="text-end text-danger fw-bold fs-5">
                                    <fmt:formatNumber value="${remainingBalance}" type="number" groupingUsed="true"/> ریال
                                </td>
                            </tr>
                            <tr>
                                <th>مبلغ پرداخت شده:</th>
                                <td class="text-end text-success fw-bold">
                                    <fmt:formatNumber value="${paidAmount}" type="number" groupingUsed="true"/> ریال
                                </td>
                            </tr>
                            <tr>
                                <th>قسط ماهانه:</th>
                                <td class="text-end">
                                    <span class="badge bg-info fs-6">
                                        <fmt:formatNumber value="${loan.monthlyPayment}" type="number" groupingUsed="true"/> ریال
                                    </span>
                                </td>
                            </tr>
                            <tr>
                                <th>اقساط پرداخت شده:</th>
                                <td class="text-end">${paidInstallments} قسط</td>
                            </tr>
                            <tr>
                                <th>اقساط باقیمانده:</th>
                                <td class="text-end">${remainingInstallments} قسط</td>
                            </tr>
                            <tr>
                                <th>مدت کل:</th>
                                <td class="text-end">${loan.durationMonths} ماه</td>
                            </tr>
                            <tr>
                                <th>نرخ بهره:</th>
                                <td class="text-end">${loan.annualInterestRate}٪</td>
                            </tr>
                            <tr>
                                <th>وضعیت:</th>
                                <td class="text-end">
                                    <c:choose>
                                        <c:when test="${loan.status == 'ACTIVE'}">
                                            <span class="badge bg-success">فعال</span>
                                        </c:when>
                                        <c:when test="${loan.status == 'APPROVED'}">
                                            <span class="badge bg-info">تأیید شده</span>
                                        </c:when>
                                        <c:when test="${loan.status == 'PAID'}">
                                            <span class="badge bg-secondary">تسویه شده</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-warning">${loan.status}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </table>

                        <!-- Progress Bar پیشرفت پرداخت -->
                        <div class="mt-3">
                            <div class="d-flex justify-content-between align-items-center mb-2">
                                <label class="form-label mb-0 fw-bold">پیشرفت پرداخت</label>
                                <span class="badge bg-primary">${paymentProgress}%</span>
                            </div>
                            <div class="progress" style="height: 30px;">
                                <div class="progress-bar bg-gradient progress-bar-striped progress-bar-animated"
                                     role="progressbar"
                                     style="width: ${paymentProgress}%"
                                     aria-valuenow="${paymentProgress}"
                                     aria-valuemin="0"
                                     aria-valuemax="100">
                                    <c:if test="${paymentProgress > 15}">
                                        ${paymentProgress}%
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- کارت اطلاعات حساب -->
            <div class="col-md-6">
                <div class="card border-success h-100 shadow-sm">
                    <div class="card-header bg-success text-white">
                        <h5 class="mb-0">
                            <i class="fas fa-wallet me-2"></i>اطلاعات حساب پرداخت
                        </h5>
                    </div>
                    <div class="card-body">
                        <table class="table table-borderless mb-0">
                            <tr>
                                <th width="50%">شماره حساب:</th>
                                <td class="text-end">
                                    <code class="fs-6"><c:out value="${account.accountNumber}" /></code>
                                </td>
                            </tr>
                            <tr>
                                <th>نوع حساب:</th>
                                <td class="text-end">
                                    <c:choose>
                                        <c:when test="${account.type == 'SAVINGS'}">
                                            <span class="badge bg-primary">پس‌انداز</span>
                                        </c:when>
                                        <c:when test="${account.type == 'CHECKING'}">
                                            <span class="badge bg-info">جاری</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-secondary">${account.type}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                            <tr>
                                <th>موجودی فعلی:</th>
                                <td class="text-end text-success fw-bold fs-4">
                                    <fmt:formatNumber value="${account.balance}" type="number" groupingUsed="true"/> ریال
                                </td>
                            </tr>
                        </table>

                        <!-- هشدار موجودی کم -->
                        <c:if test="${account.balance < loan.monthlyPayment}">
                            <div class="alert alert-warning mt-3 mb-0">
                                <i class="fas fa-exclamation-triangle me-2"></i>
                                <strong>توجه:</strong> موجودی حساب شما کمتر از قسط ماهانه است!
                            </div>
                        </c:if>

                        <!-- اطلاعات مفید -->
                        <div class="mt-4 p-3 bg-light rounded">
                            <h6 class="text-muted mb-3">
                                <i class="fas fa-info-circle me-2"></i>اطلاعات تکمیلی
                            </h6>
                            <div class="row g-2">
                                <div class="col-12">
                                    <small class="text-muted">کل مبلغ بازپرداخت:</small>
                                    <div class="fw-bold">
                                        <fmt:formatNumber value="${loan.totalRepayment}" type="number" groupingUsed="true"/> ریال
                                    </div>
                                </div>
                                <div class="col-12">
                                    <small class="text-muted">کل سود:</small>
                                    <div class="fw-bold text-warning">
                                        <fmt:formatNumber value="${loan.totalInterest}" type="number" groupingUsed="true"/> ریال
                                    </div>
                                </div>
                                <div class="col-12">
                                    <small class="text-muted">تاریخ شروع:</small>
                                    <div class="fw-bold">${loan.formattedStartDate}</div>
                                </div>
                                <div class="col-12">
                                    <small class="text-muted">تاریخ پایان:</small>
                                    <div class="fw-bold text-info">${loan.formattedEndDate}</div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- فرم پرداخت -->
        <div class="card shadow-lg border-0">
            <div class="card-header bg-gradient bg-primary text-white">
                <h5 class="mb-0">
                    <i class="fas fa-money-check-alt me-2"></i>پرداخت قسط وام
                </h5>
            </div>
            <div class="card-body p-4">
                <form action="${pageContext.request.contextPath}/loans/payment"
                      method="post"
                      id="paymentForm">

                    <input type="hidden" name="loanId" value="${loan.id}">

                    <!-- فیلد مبلغ پرداخت -->
                    <div class="mb-4">
                        <label for="paymentAmount" class="form-label fw-bold">
                            <i class="fas fa-dollar-sign me-2"></i>مبلغ پرداخت (ریال)
                            <span class="text-danger">*</span>
                        </label>
                        <input type="number"
                               class="form-control form-control-lg"
                               id="paymentAmount"
                               name="paymentAmount"
                               required
                               min="${loan.monthlyPayment}"
                               max="${remainingBalance}"
                               value="${loan.monthlyPayment}"
                               step="1000"
                               placeholder="مبلغ دلخواه را وارد کنید">

                        <div class="form-text mt-2">
                            <div class="d-flex justify-content-between">
                                <span>
                                    <i class="fas fa-arrow-down text-danger"></i>
                                    حداقل: <strong><fmt:formatNumber value="${loan.monthlyPayment}" type="number" groupingUsed="true"/></strong> ریال
                                </span>
                                <span>
                                    <i class="fas fa-arrow-up text-success"></i>
                                    حداکثر: <strong><fmt:formatNumber value="${remainingBalance}" type="number" groupingUsed="true"/></strong> ریال
                                </span>
                            </div>
                        </div>
                    </div>

                    <!-- دکمه‌های پیشنهادی -->
                    <div class="mb-4">
                        <label class="form-label fw-bold">
                            <i class="fas fa-hand-pointer me-2"></i>انتخاب سریع مبلغ:
                        </label>
                        <div class="btn-group w-100" role="group">
                            <button type="button"
                                    class="btn btn-outline-primary quick-amount"
                                    data-amount="${loan.monthlyPayment}">
                                یک قسط
                                <br>
                                <small><fmt:formatNumber value="${loan.monthlyPayment}" type="number" groupingUsed="true"/></small>
                            </button>
                            <c:if test="${remainingBalance >= loan.monthlyPayment * 3}">
                                <button type="button"
                                        class="btn btn-outline-primary quick-amount"
                                        data-amount="${loan.monthlyPayment * 3}">
                                    سه قسط
                                    <br>
                                    <small><fmt:formatNumber value="${loan.monthlyPayment * 3}" type="number" groupingUsed="true"/></small>
                                </button>
                            </c:if>
                            <c:if test="${remainingBalance > loan.monthlyPayment}">
                                <button type="button"
                                        class="btn btn-outline-success quick-amount"
                                        data-amount="${remainingBalance}">
                                    تسویه کامل
                                    <br>
                                    <small><fmt:formatNumber value="${remainingBalance}" type="number" groupingUsed="true"/></small>
                                </button>
                            </c:if>
                        </div>
                    </div>

                    <!-- راهنما -->
                    <div class="alert alert-info">
                        <h6 class="alert-heading">
                            <i class="fas fa-lightbulb me-2"></i>نکات مهم:
                        </h6>
                        <ul class="mb-0">
                            <li>مبلغ پرداخت از موجودی حساب شما کسر می‌شود</li>
                            <li>می‌توانید بیشتر از قسط ماهانه پرداخت کنید تا زودتر وام را تسویه کنید</li>
                            <li>در صورت تسویه کامل وام، وضعیت به "تسویه شده" تغییر خواهد کرد</li>
                            <li>پس از هر پرداخت، موجودی باقیمانده وام به‌روز می‌شود</li>
                        </ul>
                    </div>

                    <!-- دکمه‌های عملیات -->
                    <div class="d-flex gap-2">
                        <button type="submit"
                                class="btn btn-primary btn-lg flex-fill"
                        ${account.balance < loan.monthlyPayment ? 'disabled' : ''}
                                id="submitBtn">
                            <i class="fas fa-check-circle me-2"></i>
                            پرداخت قسط
                        </button>
                        <a href="${pageContext.request.contextPath}/loans/detail?id=${loan.id}"
                           class="btn btn-secondary btn-lg flex-fill">
                            <i class="fas fa-times me-2"></i>
                            لغو و بازگشت
                        </a>
                    </div>
                </form>
            </div>
        </div>

        <!-- کارت خلاصه مالی -->
        <div class="card mt-4 border-0 bg-light shadow-sm">
            <div class="card-body">
                <h6 class="text-center text-muted mb-3">
                    <i class="fas fa-chart-line me-2"></i>خلاصه وضعیت مالی وام
                </h6>
                <div class="row text-center g-3">
                    <div class="col-md-3">
                        <div class="p-3 bg-white rounded">
                            <h6 class="text-muted small mb-2">کل مبلغ بازپرداخت</h6>
                            <h5 class="text-primary mb-0">
                                <fmt:formatNumber value="${loan.totalRepayment}" type="number" groupingUsed="true"/>
                                <small class="text-muted">ریال</small>
                            </h5>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="p-3 bg-white rounded">
                            <h6 class="text-muted small mb-2">کل سود</h6>
                            <h5 class="text-warning mb-0">
                                <fmt:formatNumber value="${loan.totalInterest}" type="number" groupingUsed="true"/>
                                <small class="text-muted">ریال</small>
                            </h5>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="p-3 bg-white rounded">
                            <h6 class="text-muted small mb-2">پرداخت شده</h6>
                            <h5 class="text-success mb-0">
                                <fmt:formatNumber value="${paidAmount}" type="number" groupingUsed="true"/>
                                <small class="text-muted">ریال</small>
                            </h5>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="p-3 bg-white rounded">
                            <h6 class="text-muted small mb-2">باقیمانده</h6>
                            <h5 class="text-danger mb-0">
                                <fmt:formatNumber value="${remainingBalance}" type="number" groupingUsed="true"/>
                                <small class="text-muted">ریال</small>
                            </h5>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- اسکریپت‌های JavaScript -->
<script>
    // دکمه‌های انتخاب سریع مبلغ
    document.querySelectorAll('.quick-amount').forEach(button => {
        button.addEventListener('click', function() {
            const amount = this.getAttribute('data-amount');
            document.getElementById('paymentAmount').value = amount;

            // هایلایت کردن دکمه انتخاب شده
            document.querySelectorAll('.quick-amount').forEach(btn => {
                btn.classList.remove('active');
            });
            this.classList.add('active');
        });
    });

    // اعتبارسنجی فرم قبل از ارسال
    document.getElementById('paymentForm').addEventListener('submit', function(e) {
        const amount = parseFloat(document.getElementById('paymentAmount').value);
        const accountBalance = ${account.balance};
        const monthlyPayment = ${loan.monthlyPayment};
        const remainingBalance = ${remainingBalance};

        // بررسی موجودی کافی
        if (amount > accountBalance) {
            e.preventDefault();
            alert('❌ موجودی حساب شما کافی نیست!\n\n' +
                'موجودی فعلی: ' + accountBalance.toLocaleString('fa-IR') + ' ریال\n' +
                'مبلغ درخواستی: ' + amount.toLocaleString('fa-IR') + ' ریال');
            return false;
        }

        // بررسی حداقل مبلغ
        if (amount < monthlyPayment) {
            e.preventDefault();
            alert('❌ مبلغ پرداخت نمی‌تواند کمتر از قسط ماهانه باشد!\n\n' +
                'حداقل مبلغ: ' + monthlyPayment.toLocaleString('fa-IR') + ' ریال');
            return false;
        }

        // بررسی حداکثر مبلغ
        if (amount > remainingBalance) {
            e.preventDefault();
            alert('❌ مبلغ پرداخت نمی‌تواند بیشتر از باقیمانده وام باشد!\n\n' +
                'حداکثر مبلغ: ' + remainingBalance.toLocaleString('fa-IR') + ' ریال');
            return false;
        }

        // تأیید نهایی
        const confirmMsg = amount === remainingBalance
            ? '✅ آیا از تسویه کامل وام با مبلغ ' + amount.toLocaleString('fa-IR') + ' ریال اطمینان دارید؟\n\n' +
            '🎉 پس از این پرداخت، وام شما به طور کامل تسویه خواهد شد!'
            : '✅ آیا از پرداخت مبلغ ' + amount.toLocaleString('fa-IR') + ' ریال اطمینان دارید؟\n\n' +
            'باقیمانده پس از پرداخت: ' + (remainingBalance - amount).toLocaleString('fa-IR') + ' ریال';

        if (!confirm(confirmMsg)) {
            e.preventDefault();
            return false;
        }

        // غیرفعال کردن دکمه برای جلوگیری از ارسال مکرر
        document.getElementById('submitBtn').disabled = true;
        document.getElementById('submitBtn').innerHTML =
            '<span class="spinner-border spinner-border-sm me-2"></span>در حال پردازش...';

        return true;
    });

    // فرمت کردن عدد هنگام تایپ
    document.getElementById('paymentAmount').addEventListener('input', function(e) {
        // حذف کاراکترهای غیر عددی
        let value = this.value.replace(/[^0-9]/g, '');
        this.value = value;
    });
</script>

<jsp:include page="/views/common/footer.jsp" />
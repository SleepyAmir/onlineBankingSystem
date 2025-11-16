<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/views/common/header.jsp">
    <jsp:param name="title" value="تراکنش بانکی" />
</jsp:include>

<jsp:include page="/views/common/navbar.jsp" />
<jsp:include page="/views/common/sidebar.jsp" />

<div class="content-wrapper">
    <div class="container-fluid px-4 py-4">
        <h1 class="display-5 fw-bold">تراکنش بانکی</h1>
        <p class="lead text-muted">واریز، برداشت یا انتقال وجه</p>

        <!-- پیام خطا -->
        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show">
                <i class="fas fa-exclamation-triangle me-2"></i>${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <!-- فرم اصلی -->
        <div class="card shadow-lg border-0 rounded-3">
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/transactions" method="post" id="transactionForm">

                    <!-- نوع عملیات -->
                    <div class="mb-4">
                        <label class="form-label fw-bold">نوع عملیات</label>
                        <div class="btn-group w-100" role="group">
                            <input type="radio" class="btn-check" name="action" id="deposit" value="deposit" ${param.action == 'deposit' ? 'checked' : ''}>
                            <label class="btn btn-outline-success" for="deposit">
                                <i class="fas fa-plus-circle"></i> واریز
                            </label>

                            <input type="radio" class="btn-check" name="action" id="withdrawal" value="withdrawal" ${param.action == 'withdrawal' ? 'checked' : ''}>
                            <label class="btn btn-outline-warning" for="withdrawal">
                                <i class="fas fa-minus-circle"></i> برداشت
                            </label>

                            <input type="radio" class="btn-check" name="action" id="transfer" value="transfer" ${param.action == 'transfer' ? 'checked' : ''}>
                            <label class="btn btn-outline-primary" for="transfer">
                                <i class="fas fa-exchange-alt"></i> انتقال (کارت به کارت)
                            </label>
                        </div>
                    </div>

                    <!-- حساب مبدأ (برداشت) -->
                    <div class="mb-3" id="fromAccountSection" style="display: ${param.action == 'deposit' ? 'none' : (param.action == 'transfer' ? 'none' : 'block')};">
                        <label for="fromAccountId" class="form-label">
                            <i class="fas fa-wallet me-2"></i>حساب مبدأ
                        </label>
                        <select class="form-select" id="fromAccountId" name="fromAccountId">
                            <option value="">انتخاب حساب</option>
                            <c:forEach items="${accounts}" var="acc">
                                <option value="${acc.id}" ${acc.id == param.fromAccountId ? 'selected' : ''}>
                                        ${acc.accountNumber} - ${acc.type} - موجودی: <c:out value="${acc.balance}"/> ریال
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <!-- حساب مقصد (واریز) -->
                    <div class="mb-3" id="toAccountSection" style="display: ${param.action == 'deposit' ? 'block' : 'none'};">
                        <label for="toAccountId" class="form-label">
                            <i class="fas fa-wallet me-2"></i>حساب مقصد
                        </label>
                        <select class="form-select" id="toAccountId" name="toAccountId">
                            <option value="">انتخاب حساب</option>
                            <c:forEach items="${accounts}" var="acc">
                                <option value="${acc.id}" ${acc.id == param.toAccountId ? 'selected' : ''}>
                                        ${acc.accountNumber} - ${acc.type} - موجودی: <c:out value="${acc.balance}"/> ریال
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <!-- کارت مبدأ (انتقال) -->
                    <div class="mb-3" id="fromCardSection" style="display: ${param.action == 'transfer' ? 'block' : 'none'};">
                        <label for="fromCardId" class="form-label">
                            <i class="fas fa-credit-card me-2"></i>کارت مبدأ (کارت شما)
                        </label>
                        <select class="form-select" id="fromCardId" name="fromCardId">
                            <option value="">انتخاب کارت</option>
                            <c:forEach items="${activeCards}" var="card">
                                <option value="${card.id}" ${card.id == param.fromCardId ? 'selected' : ''}>
                                    **** **** **** ${card.cardNumber.substring(12)} - ${card.account.type} - موجودی: <c:out value="${card.account.balance}"/> ریال
                                </option>
                            </c:forEach>
                        </select>
                        <c:if test="${empty activeCards}">
                            <small class="text-danger">
                                <i class="fas fa-exclamation-circle"></i>
                                شما کارت فعالی ندارید. لطفاً ابتدا از منوی کارت‌ها یک کارت صادر کنید.
                            </small>
                        </c:if>
                    </div>

                    <!-- شماره کارت مقصد (انتقال) -->
                    <div class="mb-3" id="toCardNumberSection" style="display: ${param.action == 'transfer' ? 'block' : 'none'};">
                        <label for="toCardNumber" class="form-label">
                            <i class="fas fa-credit-card me-2"></i>شماره کارت مقصد (16 رقم)
                        </label>
                        <input type="text"
                               class="form-control"
                               id="toCardNumber"
                               name="toCardNumber"
                               placeholder="1234 5678 9012 3456"
                               pattern="\d{16}"
                               maxlength="16"
                               value="${param.toCardNumber}">
                        <small class="text-muted">
                            <i class="fas fa-info-circle"></i>
                            شماره کارت 16 رقمی مقصد را وارد کنید (بدون فاصله)
                        </small>
                    </div>

                    <!-- مبلغ -->
                    <div class="mb-3">
                        <label for="amount" class="form-label">
                            <i class="fas fa-money-bill-wave me-2"></i>مبلغ (ریال)
                        </label>
                        <input type="number"
                               class="form-control"
                               id="amount"
                               name="amount"
                               required
                               min="1000"
                               step="1000"
                               placeholder="مثال: 50000"
                               value="${param.amount}">
                        <small class="text-muted">حداقل مبلغ: 1,000 ریال</small>
                    </div>

                    <!-- توضیحات -->
                    <div class="mb-3">
                        <label for="description" class="form-label">
                            <i class="fas fa-comment me-2"></i>توضیحات (اختیاری)
                        </label>
                        <textarea class="form-control"
                                  id="description"
                                  name="description"
                                  rows="2"
                                  placeholder="مثال: خرید لوازم منزل">${param.description}</textarea>
                    </div>

                    <!-- راهنما -->
                    <div class="alert alert-info" id="transferHelp" style="display: ${param.action == 'transfer' ? 'block' : 'none'};">
                        <h6><i class="fas fa-lightbulb me-2"></i>نکات مهم انتقال کارت به کارت:</h6>
                        <ul class="mb-0">
                            <li>شماره کارت باید دقیقاً 16 رقم باشد</li>
                            <li>کارت مقصد باید معتبر و فعال باشد</li>
                            <li>موجودی کافی در حساب متصل به کارت مبدأ داشته باشید</li>
                            <li>انتقال آنی انجام می‌شود</li>
                        </ul>
                    </div>

                    <!-- دکمه‌ها -->
                    <div class="d-flex gap-2 mt-4">
                        <button type="submit" class="btn btn-primary btn-lg flex-fill">
                            <i class="fas fa-check-circle me-2"></i>انجام تراکنش
                        </button>
                        <a href="${pageContext.request.contextPath}/user-dashboard"
                           class="btn btn-secondary btn-lg flex-fill">
                            <i class="fas fa-times me-2"></i>لغو
                        </a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<script>
    // نمایش/مخفی کردن فیلدها بر اساس نوع عملیات
    document.querySelectorAll('input[name="action"]').forEach(radio => {
        radio.addEventListener('change', function() {
            const action = this.value;

            // مخفی کردن همه
            document.getElementById('fromAccountSection').style.display = 'none';
            document.getElementById('toAccountSection').style.display = 'none';
            document.getElementById('fromCardSection').style.display = 'none';
            document.getElementById('toCardNumberSection').style.display = 'none';
            document.getElementById('transferHelp').style.display = 'none';

            // نمایش بر اساس انتخاب
            if (action === 'deposit') {
                document.getElementById('toAccountSection').style.display = 'block';
                document.getElementById('toAccountId').required = true;
                document.getElementById('fromAccountId').required = false;
                document.getElementById('fromCardId').required = false;
                document.getElementById('toCardNumber').required = false;
            } else if (action === 'withdrawal') {
                document.getElementById('fromAccountSection').style.display = 'block';
                document.getElementById('fromAccountId').required = true;
                document.getElementById('toAccountId').required = false;
                document.getElementById('fromCardId').required = false;
                document.getElementById('toCardNumber').required = false;
            } else if (action === 'transfer') {
                document.getElementById('fromCardSection').style.display = 'block';
                document.getElementById('toCardNumberSection').style.display = 'block';
                document.getElementById('transferHelp').style.display = 'block';
                document.getElementById('fromCardId').required = true;
                document.getElementById('toCardNumber').required = true;
                document.getElementById('fromAccountId').required = false;
                document.getElementById('toAccountId').required = false;
            }
        });
    });

    // فرمت‌دهی خودکار شماره کارت
    const cardInput = document.getElementById('toCardNumber');
    cardInput.addEventListener('input', function(e) {
        // فقط اعداد
        this.value = this.value.replace(/\D/g, '');

        // محدود به 16 رقم
        if (this.value.length > 16) {
            this.value = this.value.substring(0, 16);
        }
    });

    // اعتبارسنجی فرم قبل از ارسال
    document.getElementById('transactionForm').addEventListener('submit', function(e) {
        const action = document.querySelector('input[name="action"]:checked')?.value;

        if (!action) {
            e.preventDefault();
            alert('لطفاً نوع عملیات را انتخاب کنید');
            return;
        }

        if (action === 'transfer') {
            const cardNumber = document.getElementById('toCardNumber').value;
            if (cardNumber.length !== 16) {
                e.preventDefault();
                alert('شماره کارت باید دقیقاً 16 رقم باشد');
                return;
            }

            const fromCardId = document.getElementById('fromCardId').value;
            if (!fromCardId) {
                e.preventDefault();
                alert('لطفاً کارت مبدأ را انتخاب کنید');
                return;
            }
        }
    });

    // اجرای اولیه
    const checked = document.querySelector('input[name="action"]:checked');
    if (checked) {
        checked.dispatchEvent(new Event('change'));
    }
</script>

<jsp:include page="/views/common/footer.jsp" />
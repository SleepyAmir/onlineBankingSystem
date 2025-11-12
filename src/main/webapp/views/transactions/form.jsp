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
                    ${error}
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
                            <label class="btn btn-outline-success" for="deposit">واریز</label>

                            <input type="radio" class="btn-check" name="action" id="withdrawal" value="withdrawal" ${param.action == 'withdrawal' ? 'checked' : ''}>
                            <label class="btn btn-outline-warning" for="withdrawal">برداشت</label>

                            <input type="radio" class="btn-check" name="action" id="transfer" value="transfer" ${param.action == 'transfer' ? 'checked' : ''}>
                            <label class="btn btn-outline-primary" for="transfer">انتقال</label>
                        </div>
                    </div>

                    <!-- حساب مبدأ (برداشت و انتقال) -->
                    <div class="mb-3" id="fromAccountSection" style="display: ${param.action == 'deposit' ? 'none' : 'block'};">
                        <label for="fromAccountId" class="form-label">حساب مبدأ</label>
                        <select class="form-select" id="fromAccountId" name="fromAccountId" ${param.action == 'deposit' ? '' : 'required'}>
                            <option value="">انتخاب حساب</option>
                            <c:forEach items="${accounts}" var="acc">
                                <option value="${acc.id}" ${acc.id == param.fromAccountId ? 'selected' : ''}>
                                        ${acc.accountNumber} - موجودی: ${acc.balance}
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <!-- حساب مقصد (واریز) -->
                    <div class="mb-3" id="toAccountSection" style="display: ${param.action == 'deposit' ? 'block' : 'none'};">
                        <label for="toAccountId" class="form-label">حساب مقصد</label>
                        <select class="form-select" id="toAccountId" name="toAccountId" ${param.action == 'deposit' ? 'required' : ''}>
                            <option value="">انتخاب حساب</option>
                            <c:forEach items="${accounts}" var="acc">
                                <option value="${acc.id}" ${acc.id == param.toAccountId ? 'selected' : ''}>
                                        ${acc.accountNumber} - موجودی: ${acc.balance}
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <!-- شماره حساب مقصد (انتقال) -->
                    <div class="mb-3" id="toAccountNumberSection" style="display: ${param.action == 'transfer' ? 'block' : 'none'};">
                        <label for="toAccountNumber" class="form-label">شماره حساب مقصد</label>
                        <input type="text" class="form-control" id="toAccountNumber" name="toAccountNumber"
                               placeholder="مثال: 1234567890123456" pattern="[0-9]{16}"
                        ${param.action == 'transfer' ? 'required' : ''} value="${param.toAccountNumber}">
                    </div>

                    <!-- مبلغ -->
                    <div class="mb-3">
                        <label for="amount" class="form-label">مبلغ (ریال)</label>
                        <input type="number" class="form-control" id="amount" name="amount"
                               required min="1000" value="${param.amount}">
                    </div>

                    <!-- توضیحات -->
                    <div class="mb-3">
                        <label for="description" class="form-label">توضیحات (اختیاری)</label>
                        <textarea class="form-control" id="description" name="description" rows="2">${param.description}</textarea>
                    </div>

                    <!-- دکمه‌ها -->
                    <div class="d-flex gap-2">
                        <button type="submit" class="btn btn-primary flex-fill">
                            انجام تراکنش
                        </button>
                        <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-secondary flex-fill">
                            لغو
                        </a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<script>
    document.querySelectorAll('input[name="action"]').forEach(radio => {
        radio.addEventListener('change', function() {
            const action = this.value;

            document.getElementById('fromAccountSection').style.display = (action === 'deposit') ? 'none' : 'block';
            document.getElementById('toAccountSection').style.display = (action === 'deposit') ? 'block' : 'none';
            document.getElementById('toAccountNumberSection').style.display = (action === 'transfer') ? 'block' : 'none';

            document.getElementById('fromAccountId').required = (action !== 'deposit');
            document.getElementById('toAccountId').required = (action === 'deposit');
            document.getElementById('toAccountNumber').required = (action === 'transfer');
        });
    });

    // اجرای اولیه
    const checked = document.querySelector('input[name="action"]:checked');
    if (checked) checked.dispatchEvent(new Event('change'));
</script>

<jsp:include page="/views/common/footer.jsp" />
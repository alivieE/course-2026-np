<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</head>
<body class="d-flex flex-column min-vh-100">

<#-- Спливаюче повідомлення (toast) з flash-атрибутів контролера -->
<#if toastMessage??>
<div class="toast-container position-fixed top-0 end-0 p-3" style="z-index: 1080;">
    <div id="flashToast" class="toast align-items-center text-bg-${(toastType)!'primary'} border-0 shadow"
         role="alert" aria-live="assertive" aria-atomic="true"
         data-bs-autohide="true" data-bs-delay="4000">
        <div class="d-flex">
            <div class="toast-body fw-bold">
                <#if (toastType)! == "success">✅
                <#elseif (toastType)! == "info">ℹ️
                <#elseif (toastType)! == "danger">⚠️
                </#if>
                ${toastMessage}
            </div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto"
                    data-bs-dismiss="toast" aria-label="Закрити"></button>
        </div>
    </div>
</div>
<script>
    document.addEventListener('DOMContentLoaded', function () {
        const el = document.getElementById('flashToast');
        if (el) new bootstrap.Toast(el).show();
    });
</script>
</#if>

<nav class="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm">
    <div class="container">
        <a class="navbar-brand fw-bold" href="/">✈️ SkyAirlines</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav me-auto">
                <li class="nav-item"><a class="nav-link active" href="/">Головна</a></li>
                <li class="nav-item"><a class="nav-link" href="/flights">Розклад рейсів</a></li>
                <#if currentUser?? && currentUser.role?string == "ADMIN">
                    <li class="nav-item">
                        <a class="nav-link text-warning fw-bold" href="/admin/flights">
                            🛠 Адмінпанель
                        </a>
                    </li>
                </#if>
            </ul>
            <ul class="navbar-nav">
                <#if currentUser??>
                    <li class="nav-item">
                        <span class="navbar-text text-white me-3">
                            👤 ${currentUser.firstName} ${currentUser.lastName}
                            <#if currentUser.role?string == "ADMIN">
                                <span class="badge bg-warning text-dark">ADMIN</span>
                            </#if>
                        </span>
                    </li>
                    <li class="nav-item">
                        <a class="btn btn-outline-light btn-sm" href="/logout">Вийти</a>
                    </li>
                <#else>
                    <li class="nav-item me-2">
                        <a class="btn btn-outline-light btn-sm" href="/login">Увійти</a>
                    </li>
                    <li class="nav-item">
                        <a class="btn btn-light btn-sm text-primary fw-bold" href="/register">Реєстрація</a>
                    </li>
                </#if>
            </ul>
        </div>
    </div>
</nav>

<div class="container mt-4">
    <div class="p-5 mb-4 bg-light rounded-3 shadow-sm">
        <div class="container-fluid py-5">
            <h1 class="display-5 fw-bold text-primary">${welcomeMessage}</h1>
            <#if currentUser??>
                <p class="col-md-8 fs-5 text-success fw-bold">
                    Вітаємо, ${currentUser.firstName}! Ви успішно увійшли в систему.
                </p>
            </#if>
            <p class="col-md-8 fs-4 text-secondary">
                Швидкі, безпечні та зручні перельоти до найпопулярніших міст Європи та світу.
            </p>
            <a href="/flights" class="btn btn-primary btn-lg mt-3 shadow">Знайти рейс ➔</a>
        </div>
    </div>

    <div class="row text-center mt-5 mb-5">
        <div class="col-md-4 mb-3">
            <div class="card h-100 border-0 shadow-sm">
                <div class="card-body">
                    <h2 class="card-title">📱</h2>
                    <h4 class="fw-bold">Онлайн-реєстрація</h4>
                    <p class="text-muted">Проходьте реєстрацію за 24 години до вильоту просто у смартфоні.</p>
                </div>
            </div>
        </div>
        <div class="col-md-4 mb-3">
            <div class="card h-100 border-0 shadow-sm">
                <div class="card-body">
                    <h2 class="card-title">💼</h2>
                    <h4 class="fw-bold">Гнучкі тарифи</h4>
                    <p class="text-muted">Обирайте тариф, який підходить саме вам.</p>
                </div>
            </div>
        </div>
        <div class="col-md-4 mb-3">
            <div class="card h-100 border-0 shadow-sm">
                <div class="card-body">
                    <h2 class="card-title">🎧</h2>
                    <h4 class="fw-bold">Підтримка 24/7</h4>
                    <p class="text-muted">Наші спеціалісти завжди на зв'язку.</p>
                </div>
            </div>
        </div>
    </div>
</div>

<footer class="mt-auto bg-dark text-white text-center py-3">
    <p class="mb-0">© 2026 SkyAirlines. Всі права захищено.</p>
</footer>
</body>
</html>

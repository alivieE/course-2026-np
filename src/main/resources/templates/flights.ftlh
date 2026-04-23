<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</head>
<body class="bg-light d-flex flex-column min-vh-100">

<#-- Спливаюче повідомлення (toast) з flash-атрибутів -->
<#if toastMessage??>
<div class="toast-container position-fixed top-0 end-0 p-3" style="z-index: 1080;">
    <div id="flashToast" class="toast align-items-center text-bg-${(toastType)!'primary'} border-0 shadow"
         role="alert" data-bs-autohide="true" data-bs-delay="4000">
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
                <li class="nav-item"><a class="nav-link" href="/">Головна</a></li>
                <li class="nav-item"><a class="nav-link active" href="/flights">Розклад рейсів</a></li>
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

<div class="container mt-5 mb-5">
    <div class="d-flex justify-content-between align-items-center border-bottom pb-3 mb-4">
        <h2 class="fw-bold">Доступні напрямки</h2>
        <span class="badge bg-secondary fs-6">Знайдено: <#if flightsCount??>${flightsCount}<#else>${flights?size}</#if> рейсів</span>
    </div>

    <#if !currentUser??>
        <div class="alert alert-info shadow-sm" role="alert">
            💡 <b>Підказка:</b> для оформлення квитка необхідно
            <a href="/login" class="alert-link">увійти в систему</a>
            або <a href="/register" class="alert-link">зареєструватися</a>.
        </div>
    </#if>

    <#if flights?has_content>
        <div class="row">
            <#list flights as flight>
                <div class="col-md-4 mb-4">
                    <div class="card h-100 shadow-sm <#if flight_index == 0>border-primary</#if>">
                        <div class="card-header <#if flight_index == 0>bg-primary<#else>bg-secondary</#if> text-white d-flex justify-content-between">
                            <span>Рейс: <b>${flight.flightNumber}</b></span>
                            <span>✈️</span>
                        </div>
                        <div class="card-body">
                            <h4 class="card-title fw-bold text-center mb-3">
                                ${flight.departureCity} ➔ ${flight.arrivalCity}
                            </h4>
                            <ul class="list-unstyled">
                                <li>📅 <b>Дата:</b> ${flight.departureTime[8..9]}.${flight.departureTime[5..6]}.${flight.departureTime[0..3]}</li>
                                <li>🕒 <b>Виліт:</b> ${flight.departureTime[11..15]}</li>
                                <li>🛬 <b>Приліт:</b> ${flight.arrivalTime[11..15]}</li>
                                <li>⏱ <b>Тривалість:</b> ${flight.duration} хв</li>
                                <li>
                                    📌 <b>Статус:</b>
                                    <#if flight.status?string == "PLANNED">
                                        <span class="badge bg-success">Заплановано</span>
                                    <#elseif flight.status?string == "CANCELLED">
                                        <span class="badge bg-danger">Скасовано</span>
                                    <#elseif flight.status?string == "IN_PROGRESS">
                                        <span class="badge bg-info text-dark">У польоті</span>
                                    <#elseif flight.status?string == "COMPLETED">
                                        <span class="badge bg-secondary">Завершено</span>
                                    <#else>
                                        <span class="badge bg-warning text-dark">${flight.status}</span>
                                    </#if>
                                </li>
                            </ul>
                        </div>
                        <div class="card-footer bg-white border-0 text-center pb-3">
                            <#if flight.status?string == "CANCELLED" || flight.status?string == "COMPLETED">
                                <button class="btn btn-outline-secondary w-100 fw-bold" disabled>Недоступно</button>
                            <#else>
                                <a href="/booking" class="btn btn-outline-primary w-100 fw-bold">Вибрати квиток</a>
                            </#if>
                        </div>
                    </div>
                </div>
            </#list>
        </div>
    <#else>
        <div class="alert alert-warning text-center shadow-sm" role="alert">
            <h4 class="alert-heading fw-bold">Рейсів не знайдено</h4>
            <p class="mb-0">На жаль, на цей момент доступних рейсів немає. Спробуйте пізніше.</p>
        </div>
    </#if>
</div>

<footer class="bg-dark text-white text-center py-3 mt-auto">
    <p class="mb-0">© 2026 SkyAirlines. Всі права захищено.</p>
</footer>
</body>
</html>

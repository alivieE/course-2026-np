<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</head>
<body class="d-flex flex-column min-vh-100 bg-light">

<#-- Спливаюче повідомлення (toast) -->
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

<nav class="navbar navbar-expand-lg navbar-dark bg-dark shadow-sm">
    <div class="container">
        <a class="navbar-brand fw-bold" href="/">✈️ SkyAirlines
            <span class="badge bg-warning text-dark ms-2">АДМІНПАНЕЛЬ</span>
        </a>
        <div class="collapse navbar-collapse">
            <ul class="navbar-nav me-auto">
                <li class="nav-item"><a class="nav-link" href="/">Головна</a></li>
                <li class="nav-item"><a class="nav-link" href="/flights">Розклад (клієнт)</a></li>
                <li class="nav-item"><a class="nav-link active text-warning fw-bold" href="/admin/flights">Керування рейсами</a></li>
            </ul>
            <ul class="navbar-nav">
                <li class="nav-item">
                    <span class="navbar-text text-white me-3">
                        👤 ${currentUser.firstName} ${currentUser.lastName}
                        <span class="badge bg-warning text-dark">ADMIN</span>
                    </span>
                </li>
                <li class="nav-item">
                    <a class="btn btn-outline-light btn-sm" href="/logout">Вийти</a>
                </li>
            </ul>
        </div>
    </div>
</nav>

<div class="container mt-4 mb-5">
    <div class="d-flex justify-content-between align-items-center mb-4 pb-3 border-bottom">
        <div>
            <h1 class="fw-bold mb-1">🛠 Керування рейсами</h1>
            <p class="text-muted mb-0">Створюйте, редагуйте та видаляйте рейси авіакомпанії.</p>
        </div>
        <a href="/admin/flights/new" class="btn btn-primary btn-lg shadow-sm">
            + Новий рейс
        </a>
    </div>

    <#if flights?has_content>
        <div class="card shadow-sm border-0">
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="table-dark">
                        <tr>
                            <th>#</th>
                            <th>Номер</th>
                            <th>Маршрут</th>
                            <th>Виліт</th>
                            <th>Приліт</th>
                            <th>Статус</th>
                            <th class="text-end">Дії</th>
                        </tr>
                    </thead>
                    <tbody>
                        <#list flights as flight>
                        <tr>
                            <td>${flight.id}</td>
                            <td><b>${flight.flightNumber}</b></td>
                            <td>${flight.departureCity} → ${flight.arrivalCity}</td>
                            <td>${flight.departureTime[8..9]}.${flight.departureTime[5..6]}.${flight.departureTime[0..3]} ${flight.departureTime[11..15]}</td>
                            <td>${flight.arrivalTime[8..9]}.${flight.arrivalTime[5..6]}.${flight.arrivalTime[0..3]} ${flight.arrivalTime[11..15]}</td>
                            <td>
                                <#if flight.status?string == "PLANNED">
                                    <span class="badge bg-success">Заплановано</span>
                                <#elseif flight.status?string == "IN_PROGRESS">
                                    <span class="badge bg-info text-dark">У польоті</span>
                                <#elseif flight.status?string == "COMPLETED">
                                    <span class="badge bg-secondary">Завершено</span>
                                <#elseif flight.status?string == "CANCELLED">
                                    <span class="badge bg-danger">Скасовано</span>
                                </#if>
                            </td>
                            <td class="text-end">
                                <a href="/admin/flights/${flight.id}/edit"
                                   class="btn btn-sm btn-outline-primary">✏️ Редагувати</a>
                                <form action="/admin/flights/${flight.id}/delete" method="post"
                                      class="d-inline"
                                      onsubmit="return confirm('Видалити рейс ${flight.flightNumber}?');">
                                    <button type="submit" class="btn btn-sm btn-outline-danger">🗑 Видалити</button>
                                </form>
                            </td>
                        </tr>
                        </#list>
                    </tbody>
                </table>
            </div>
        </div>
        <p class="text-muted mt-3">Усього рейсів: <b>${flights?size}</b></p>
    <#else>
        <div class="alert alert-warning text-center shadow-sm">
            <h4 class="alert-heading fw-bold">Рейсів поки немає</h4>
            <p>Натисніть «+ Новий рейс», щоб створити перший запис.</p>
        </div>
    </#if>
</div>

<footer class="mt-auto bg-dark text-white text-center py-3">
    <p class="mb-0">© 2026 SkyAirlines. Адміністративна панель.</p>
</footer>
</body>
</html>

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

<#if toastMessage??>
<div class="toast-container position-fixed top-0 end-0 p-3" style="z-index: 1080;">
    <div id="flashToast" class="toast align-items-center text-bg-${(toastType)!'primary'} border-0 shadow"
         role="alert" data-bs-autohide="true" data-bs-delay="4000">
        <div class="d-flex">
            <div class="toast-body fw-bold">
                <#if (toastType)! == "success">✅<#elseif (toastType)! == "info">ℹ️<#elseif (toastType)! == "danger">⚠️</#if>
                ${toastMessage}
            </div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
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
                <li class="nav-item"><a class="nav-link" href="/admin/flights">Рейси</a></li>
                <li class="nav-item"><a class="nav-link" href="/admin/aircrafts">Літаки</a></li>
                <li class="nav-item"><a class="nav-link" href="/admin/passengers">Пасажири</a></li>
                <li class="nav-item"><a class="nav-link active text-warning fw-bold" href="/admin/tickets">Квитки</a></li>
            </ul>
            <ul class="navbar-nav">
                <li class="nav-item">
                    <span class="navbar-text text-white me-3">
                        👤 ${currentUser.firstName} ${currentUser.lastName}
                        <span class="badge bg-warning text-dark">ADMIN</span>
                    </span>
                </li>
                <li class="nav-item"><a class="btn btn-outline-light btn-sm" href="/logout">Вийти</a></li>
            </ul>
        </div>
    </div>
</nav>

<div class="container mt-4 mb-5">
    <div class="d-flex justify-content-between align-items-center mb-4 pb-3 border-bottom">
        <div>
            <h1 class="fw-bold mb-1">🎫 Керування квитками</h1>
            <p class="text-muted mb-0">Створюйте, редагуйте та видаляйте квитки на рейси.</p>
        </div>
        <a href="/admin/tickets/new" class="btn btn-primary btn-lg shadow-sm">+ Новий квиток</a>
    </div>

    <#if tickets?has_content>
        <div class="card shadow-sm border-0">
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="table-dark">
                        <tr>
                            <th>#</th>
                            <th>Номер</th>
                            <th>Пасажир</th>
                            <th>Рейс</th>
                            <th>Місце</th>
                            <th>Клас</th>
                            <th>Ціна</th>
                            <th>Статус</th>
                            <th class="text-end">Дії</th>
                        </tr>
                    </thead>
                    <tbody>
                        <#list tickets as t>
                        <tr>
                            <td>${t.id}</td>
                            <td><b>${t.ticketNumber}</b></td>
                            <td>${t.passenger.lastName} ${t.passenger.firstName}</td>
                            <td>${t.flight.flightNumber}<br><small class="text-muted">${t.flight.departureCity} → ${t.flight.arrivalCity}</small></td>
                            <td>${t.seatNumber}</td>
                            <td>
                                <#if t.serviceClass?string == "ECONOMY"><span class="badge bg-secondary">Економ</span>
                                <#elseif t.serviceClass?string == "BUSINESS"><span class="badge bg-info text-dark">Бізнес</span>
                                <#elseif t.serviceClass?string == "FIRST"><span class="badge bg-warning text-dark">Перший</span></#if>
                            </td>
                            <td>${t.price} ₴</td>
                            <td>
                                <#if t.status?string == "RESERVED"><span class="badge bg-warning text-dark">Заброньовано</span>
                                <#elseif t.status?string == "CONFIRMED"><span class="badge bg-success">Підтверджено</span>
                                <#elseif t.status?string == "CANCELLED"><span class="badge bg-danger">Скасовано</span>
                                <#elseif t.status?string == "USED"><span class="badge bg-secondary">Використано</span></#if>
                            </td>
                            <td class="text-end">
                                <a href="/admin/tickets/${t.id}/edit" class="btn btn-sm btn-outline-primary">✏️</a>
                                <form action="/admin/tickets/${t.id}/delete" method="post" class="d-inline"
                                      onsubmit="return confirm('Видалити квиток ${t.ticketNumber}?');">
                                    <button type="submit" class="btn btn-sm btn-outline-danger">🗑</button>
                                </form>
                            </td>
                        </tr>
                        </#list>
                    </tbody>
                </table>
            </div>
        </div>
        <p class="text-muted mt-3">Усього квитків: <b>${tickets?size}</b></p>
    <#else>
        <div class="alert alert-warning text-center shadow-sm">
            <h4 class="alert-heading fw-bold">Квитків поки немає</h4>
            <p>Натисніть «+ Новий квиток», щоб оформити перший.</p>
        </div>
    </#if>
</div>

<footer class="mt-auto bg-dark text-white text-center py-3">
    <p class="mb-0">© 2026 SkyAirlines. Адміністративна панель.</p>
</footer>
</body>
</html>

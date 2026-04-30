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
            <button type="button" class="btn-close btn-close-white me-2 m-auto"
                    data-bs-dismiss="toast"></button>
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
                <li class="nav-item"><a class="nav-link active text-warning fw-bold" href="/admin/aircrafts">Літаки</a></li>
                <li class="nav-item"><a class="nav-link" href="/admin/passengers">Пасажири</a></li>
                <li class="nav-item"><a class="nav-link" href="/admin/tickets">Квитки</a></li>
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
            <h1 class="fw-bold mb-1">🛩 Керування літаками</h1>
            <p class="text-muted mb-0">Створюйте, редагуйте та видаляйте літаки авіакомпанії.</p>
        </div>
        <a href="/admin/aircrafts/new" class="btn btn-primary btn-lg shadow-sm">+ Новий літак</a>
    </div>

    <#if aircrafts?has_content>
        <div class="card shadow-sm border-0">
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="table-dark">
                        <tr>
                            <th>#</th>
                            <th>Реєстр. номер</th>
                            <th>Модель</th>
                            <th>Виробник</th>
                            <th>Рік</th>
                            <th>Місць</th>
                            <th>Статус</th>
                            <th class="text-end">Дії</th>
                        </tr>
                    </thead>
                    <tbody>
                        <#list aircrafts as a>
                        <tr>
                            <td>${a.id}</td>
                            <td><b>${a.registrationNumber}</b></td>
                            <td>${a.model}</td>
                            <td>${a.manufacturer}</td>
                            <td>${(a.yearOfManufacture)!'—'}</td>
                            <td>${a.totalSeats}</td>
                            <td>
                                <#if a.status?string == "ACTIVE">
                                    <span class="badge bg-success">Активний</span>
                                <#elseif a.status?string == "MAINTENANCE">
                                    <span class="badge bg-warning text-dark">Обслуговування</span>
                                <#elseif a.status?string == "OUT_OF_SERVICE">
                                    <span class="badge bg-secondary">Не в експлуатації</span>
                                <#elseif a.status?string == "RETIRED">
                                    <span class="badge bg-danger">Списано</span>
                                </#if>
                            </td>
                            <td class="text-end">
                                <a href="/admin/aircrafts/${a.id}/edit" class="btn btn-sm btn-outline-primary">✏️</a>
                                <form action="/admin/aircrafts/${a.id}/delete" method="post" class="d-inline"
                                      onsubmit="return confirm('Видалити літак ${a.registrationNumber}?');">
                                    <button type="submit" class="btn btn-sm btn-outline-danger">🗑</button>
                                </form>
                            </td>
                        </tr>
                        </#list>
                    </tbody>
                </table>
            </div>
        </div>
        <p class="text-muted mt-3">Усього літаків: <b>${aircrafts?size}</b></p>
    <#else>
        <div class="alert alert-warning text-center shadow-sm">
            <h4 class="alert-heading fw-bold">Літаків поки немає</h4>
            <p>Натисніть «+ Новий літак», щоб створити перший запис.</p>
        </div>
    </#if>
</div>

<footer class="mt-auto bg-dark text-white text-center py-3">
    <p class="mb-0">© 2026 SkyAirlines. Адміністративна панель.</p>
</footer>
</body>
</html>

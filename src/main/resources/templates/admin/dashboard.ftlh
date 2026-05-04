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

<nav class="navbar navbar-expand-lg navbar-dark bg-dark shadow-sm">
    <div class="container">
        <a class="navbar-brand fw-bold" href="/">✈️ SkyAirlines
            <span class="badge bg-warning text-dark ms-2">АДМІНПАНЕЛЬ</span>
        </a>
        <div class="collapse navbar-collapse">
            <ul class="navbar-nav me-auto">
                <li class="nav-item"><a class="nav-link" href="/">Головна</a></li>
                <li class="nav-item"><a class="nav-link active text-warning fw-bold" href="/admin">Огляд</a></li>
                <li class="nav-item"><a class="nav-link" href="/admin/flights">Рейси</a></li>
                <li class="nav-item"><a class="nav-link" href="/admin/aircrafts">Літаки</a></li>
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
                <li class="nav-item"><a class="btn btn-outline-light btn-sm" href="/logout">Вийти</a></li>
            </ul>
        </div>
    </div>
</nav>

<div class="container mt-4 mb-5">
    <div class="mb-4 pb-3 border-bottom">
        <h1 class="fw-bold mb-1">📊 Огляд адміністративної панелі</h1>
        <p class="text-muted mb-0">Швидкий доступ до основних розділів керування авіакомпанією.</p>
    </div>

    <div class="row g-4">
        <div class="col-md-6 col-lg-3">
            <a href="/admin/flights" class="text-decoration-none">
                <div class="card shadow-sm border-0 h-100 text-center">
                    <div class="card-body">
                        <div class="display-4 mb-2">🛫</div>
                        <h5 class="card-title fw-bold text-dark">Рейси</h5>
                        <p class="display-6 text-primary mb-0">${flightCount}</p>
                        <small class="text-muted">записів</small>
                    </div>
                </div>
            </a>
        </div>

        <div class="col-md-6 col-lg-3">
            <a href="/admin/aircrafts" class="text-decoration-none">
                <div class="card shadow-sm border-0 h-100 text-center">
                    <div class="card-body">
                        <div class="display-4 mb-2">🛩</div>
                        <h5 class="card-title fw-bold text-dark">Літаки</h5>
                        <p class="display-6 text-primary mb-0">${aircraftCount}</p>
                        <small class="text-muted">
                            <#if (maintenanceCount > 0)>
                                <span class="badge bg-warning text-dark">🔧 ${maintenanceCount} потребує ТО</span>
                            <#else>
                                записів
                            </#if>
                        </small>
                    </div>
                </div>
            </a>
        </div>

        <div class="col-md-6 col-lg-3">
            <a href="/admin/passengers" class="text-decoration-none">
                <div class="card shadow-sm border-0 h-100 text-center">
                    <div class="card-body">
                        <div class="display-4 mb-2">👥</div>
                        <h5 class="card-title fw-bold text-dark">Пасажири</h5>
                        <p class="display-6 text-primary mb-0">${passengerCount}</p>
                        <small class="text-muted">записів</small>
                    </div>
                </div>
            </a>
        </div>

        <div class="col-md-6 col-lg-3">
            <a href="/admin/tickets" class="text-decoration-none">
                <div class="card shadow-sm border-0 h-100 text-center">
                    <div class="card-body">
                        <div class="display-4 mb-2">🎫</div>
                        <h5 class="card-title fw-bold text-dark">Квитки</h5>
                        <p class="display-6 text-primary mb-0">${ticketCount}</p>
                        <small class="text-muted">записів</small>
                    </div>
                </div>
            </a>
        </div>
    </div>

    <div class="mt-5">
        <h3 class="fw-bold mb-3">⚡ Швидкі дії</h3>
        <div class="d-flex gap-2 flex-wrap">
            <a href="/admin/flights/new" class="btn btn-outline-primary">+ Новий рейс</a>
            <a href="/admin/aircrafts/new" class="btn btn-outline-primary">+ Новий літак</a>
            <a href="/admin/passengers/new" class="btn btn-outline-primary">+ Новий пасажир</a>
            <a href="/admin/tickets/new" class="btn btn-outline-primary">+ Новий квиток</a>
            <a href="/admin/aircrafts/maintenance-needed" class="btn btn-outline-warning">🔧 Літаки на ТО</a>
        </div>
    </div>
</div>

<footer class="mt-auto bg-dark text-white text-center py-3">
    <p class="mb-0">© 2026 SkyAirlines. Адміністративна панель.</p>
</footer>
</body>
</html>

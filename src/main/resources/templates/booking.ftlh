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

<nav class="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm">
    <div class="container">
        <a class="navbar-brand fw-bold" href="/">✈️ SkyAirlines</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav me-auto">
                <li class="nav-item"><a class="nav-link" href="/">Головна</a></li>
                <li class="nav-item"><a class="nav-link" href="/flights">Розклад рейсів</a></li>
                <li class="nav-item"><a class="nav-link active" href="/booking">Оформлення квитка</a></li>
                <li class="nav-item"><a class="nav-link" href="/my-tickets">Мої квитки</a></li>
            </ul>
            <ul class="navbar-nav">
                <li class="nav-item">
                    <span class="navbar-text text-white me-3">
                        👤 ${currentUser.firstName} ${currentUser.lastName}
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
    <div class="row justify-content-center mb-4">
        <div class="col-lg-10">
            <div class="alert alert-success mb-0">
                ✅ Ви увійшли як <b>${currentUser.firstName} ${currentUser.lastName}</b>
                (${currentUser.email}). Профіль пасажира: <b>${passenger.lastName} ${passenger.firstName}</b>
            </div>
        </div>
    </div>

    <div class="row justify-content-center">
        <div class="col-lg-10">
            <div class="card shadow-sm border-0">
                <div class="card-header bg-white border-bottom py-3">
                    <h1 class="h3 fw-bold mb-0">🎫 Оформлення нового квитка</h1>
                </div>
                <div class="card-body p-4">
                    <form action="/booking" method="post">

                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label for="ticketNumber" class="form-label fw-bold">Номер квитка</label>
                                <input type="text" class="form-control" id="ticketNumber" name="ticketNumber"
                                       required pattern="TK-[0-9]{7}" placeholder="TK-1234567">
                                <div class="form-text">Формат: TK-XXXXXXX</div>
                            </div>
                            <div class="col-md-6">
                                <label for="seatNumber" class="form-label fw-bold">Місце</label>
                                <input type="text" class="form-control" id="seatNumber" name="seatNumber"
                                       required pattern="[0-9]{1,3}[A-F]" placeholder="12A">
                            </div>
                        </div>

                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label for="serviceClass" class="form-label fw-bold">Клас обслуговування</label>
                                <select class="form-select" id="serviceClass" name="serviceClass" required>
                                    <#list classes as c>
                                        <option value="${c}">
                                            <#if c?string == "ECONOMY">Економ
                                            <#elseif c?string == "BUSINESS">Бізнес
                                            <#elseif c?string == "FIRST">Перший</#if>
                                        </option>
                                    </#list>
                                </select>
                            </div>
                            <div class="col-md-6">
                                <label for="price" class="form-label fw-bold">Ціна (грн)</label>
                                <input type="number" class="form-control" id="price" name="price"
                                       required step="0.01" min="0.01" placeholder="2500.00">
                            </div>
                        </div>

                        <div class="row mb-3">
                            <div class="col-md-12">
                                <label for="flightId" class="form-label fw-bold">Рейс</label>
                                <select class="form-select" id="flightId" name="flightId" required>
                                    <option value="">— оберіть рейс —</option>
                                    <#list flights as f>
                                        <option value="${f.id}" <#if (selectedFlightId)?? && selectedFlightId == f.id>selected</#if>>
                                            ${f.flightNumber} (${f.departureCity} → ${f.arrivalCity})
                                        </option>
                                    </#list>
                                </select>
                            </div>
                        </div>

                        <hr class="my-4">
                        <div class="d-flex justify-content-between">
                            <a href="/flights" class="btn btn-outline-secondary">← До розкладу</a>
                            <button type="submit" class="btn btn-primary btn-lg">Підтвердити бронювання ✔️</button>
                        </div>
                    </form>
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

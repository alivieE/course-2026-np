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
                <li class="nav-item"><a class="btn btn-outline-light btn-sm" href="/logout">Вийти</a></li>
            </ul>
        </div>
    </div>
</nav>

<div class="container mt-4 mb-5">
    <div class="row justify-content-center">
        <div class="col-lg-8">
            <div class="card shadow-sm border-0">
                <div class="card-header bg-white border-bottom py-3">
                    <h1 class="h3 fw-bold mb-0">
                        <#if isNew>➕ Створення літака<#else>✏️ Редагування літака #${aircraft.id}</#if>
                    </h1>
                </div>
                <div class="card-body p-4">
                    <form action="<#if isNew>/admin/aircrafts<#else>/admin/aircrafts/${aircraft.id}</#if>" method="post">

                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label for="registrationNumber" class="form-label fw-bold">Реєстраційний номер</label>
                                <input type="text" class="form-control" id="registrationNumber" name="registrationNumber"
                                       value="${(aircraft.registrationNumber)!''}"
                                       required pattern="[A-Z]{2}-[0-9A-Z]{4,6}" placeholder="UR-12345">
                                <div class="form-text">Формат: XX-#####</div>
                            </div>
                            <div class="col-md-6">
                                <label for="status" class="form-label fw-bold">Статус</label>
                                <select class="form-select" id="status" name="status" required>
                                    <#list statuses as s>
                                        <option value="${s}" <#if (aircraft.status)?? && aircraft.status == s>selected</#if>>
                                            <#if s?string == "ACTIVE">Активний
                                            <#elseif s?string == "MAINTENANCE">На обслуговуванні
                                            <#elseif s?string == "OUT_OF_SERVICE">Не в експлуатації
                                            <#elseif s?string == "RETIRED">Списано</#if>
                                        </option>
                                    </#list>
                                </select>
                            </div>
                        </div>

                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label for="model" class="form-label fw-bold">Модель</label>
                                <input type="text" class="form-control" id="model" name="model"
                                       value="${(aircraft.model)!''}" required placeholder="Boeing 737">
                            </div>
                            <div class="col-md-6">
                                <label for="manufacturer" class="form-label fw-bold">Виробник</label>
                                <input type="text" class="form-control" id="manufacturer" name="manufacturer"
                                       value="${(aircraft.manufacturer)!''}" required placeholder="Boeing">
                            </div>
                        </div>

                        <div class="row mb-3">
                            <div class="col-md-4">
                                <label for="yearOfManufacture" class="form-label fw-bold">Рік випуску</label>
                                <input type="number" class="form-control" id="yearOfManufacture" name="yearOfManufacture"
                                       value="${(aircraft.yearOfManufacture)!''}" min="1950" max="2030">
                            </div>
                            <div class="col-md-4">
                                <label for="totalSeats" class="form-label fw-bold">Кількість місць</label>
                                <input type="number" class="form-control" id="totalSeats" name="totalSeats"
                                       value="${(aircraft.totalSeats)!''}" min="1" required>
                            </div>
                            <div class="col-md-4">
                                <label for="maxRangeKm" class="form-label fw-bold">Дальність (км)</label>
                                <input type="number" class="form-control" id="maxRangeKm" name="maxRangeKm"
                                       value="${(aircraft.maxRangeKm)!''}" min="0">
                            </div>
                        </div>

                        <hr class="my-4">
                        <div class="d-flex justify-content-between">
                            <a href="/admin/aircrafts" class="btn btn-outline-secondary">← Скасувати</a>
                            <button type="submit" class="btn btn-primary btn-lg">
                                <#if isNew>💾 Створити літак<#else>💾 Зберегти зміни</#if>
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<footer class="mt-auto bg-dark text-white text-center py-3">
    <p class="mb-0">© 2026 SkyAirlines. Адміністративна панель.</p>
</footer>
</body>
</html>

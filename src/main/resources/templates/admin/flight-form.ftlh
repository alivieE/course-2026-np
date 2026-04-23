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
    <div class="row justify-content-center">
        <div class="col-lg-8">
            <div class="card shadow-sm border-0">
                <div class="card-header bg-white border-bottom py-3">
                    <h1 class="h3 fw-bold mb-0">
                        <#if isNew>
                            ➕ Створення рейсу
                        <#else>
                            ✏️ Редагування рейсу #${flight.id}
                        </#if>
                    </h1>
                </div>
                <div class="card-body p-4">
                    <#-- POST на /admin/flights для створення або /admin/flights/{id} для оновлення -->
                    <form action="<#if isNew>/admin/flights<#else>/admin/flights/${flight.id}</#if>"
                          method="post">

                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label for="flightNumber" class="form-label fw-bold">Номер рейсу</label>
                                <input type="text" class="form-control" id="flightNumber" name="flightNumber"
                                       value="${(flight.flightNumber)!''}"
                                       required placeholder="SA-101" pattern="[A-Z]{2}-[0-9]+">
                                <div class="form-text">Формат: XX-### (наприклад SA-101)</div>
                            </div>
                            <div class="col-md-6">
                                <label for="status" class="form-label fw-bold">Статус</label>
                                <select class="form-select" id="status" name="status" required>
                                    <#list statuses as s>
                                        <option value="${s}"
                                            <#if (flight.status)?? && flight.status == s>selected</#if>>
                                            <#if s?string == "PLANNED">Заплановано
                                            <#elseif s?string == "IN_PROGRESS">У польоті
                                            <#elseif s?string == "COMPLETED">Завершено
                                            <#elseif s?string == "CANCELLED">Скасовано
                                            </#if>
                                        </option>
                                    </#list>
                                </select>
                            </div>
                        </div>

                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label for="departureCity" class="form-label fw-bold">Місто вильоту</label>
                                <input type="text" class="form-control" id="departureCity" name="departureCity"
                                       value="${(flight.departureCity)!''}" required placeholder="Київ">
                            </div>
                            <div class="col-md-6">
                                <label for="arrivalCity" class="form-label fw-bold">Місто прибуття</label>
                                <input type="text" class="form-control" id="arrivalCity" name="arrivalCity"
                                       value="${(flight.arrivalCity)!''}" required placeholder="Париж">
                            </div>
                        </div>

                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label for="departureTime" class="form-label fw-bold">Час вильоту</label>
                                <input type="datetime-local" class="form-control"
                                       id="departureTime" name="departureTime"
                                       value="<#if (flight.departureTime)??>${flight.departureTime[0..15]}</#if>"
                                       required>
                            </div>
                            <div class="col-md-6">
                                <label for="arrivalTime" class="form-label fw-bold">Час прибуття</label>
                                <input type="datetime-local" class="form-control"
                                       id="arrivalTime" name="arrivalTime"
                                       value="<#if (flight.arrivalTime)??>${flight.arrivalTime[0..15]}</#if>"
                                       required>
                            </div>
                        </div>

                        <hr class="my-4">

                        <div class="d-flex justify-content-between">
                            <a href="/admin/flights" class="btn btn-outline-secondary">← Скасувати</a>
                            <button type="submit" class="btn btn-primary btn-lg">
                                <#if isNew>💾 Створити рейс<#else>💾 Зберегти зміни</#if>
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

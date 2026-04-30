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
    <div class="row justify-content-center">
        <div class="col-lg-8">
            <div class="card shadow-sm border-0">
                <div class="card-header bg-white border-bottom py-3">
                    <h1 class="h3 fw-bold mb-0">
                        <#if isNew>➕ Створення квитка<#else>✏️ Редагування квитка #${ticket.id}</#if>
                    </h1>
                </div>
                <div class="card-body p-4">
                    <form action="<#if isNew>/admin/tickets<#else>/admin/tickets/${ticket.id}</#if>" method="post">

                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label for="ticketNumber" class="form-label fw-bold">Номер квитка</label>
                                <input type="text" class="form-control" id="ticketNumber" name="ticketNumber"
                                       value="${(ticket.ticketNumber)!''}" required pattern="TK-[0-9]{7}" placeholder="TK-1234567">
                                <div class="form-text">Формат: TK-XXXXXXX</div>
                            </div>
                            <div class="col-md-6">
                                <label for="seatNumber" class="form-label fw-bold">Місце</label>
                                <input type="text" class="form-control" id="seatNumber" name="seatNumber"
                                       value="${(ticket.seatNumber)!''}" required pattern="[0-9]{1,3}[A-F]" placeholder="12A">
                            </div>
                        </div>

                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label for="serviceClass" class="form-label fw-bold">Клас обслуговування</label>
                                <select class="form-select" id="serviceClass" name="serviceClass" required>
                                    <#list classes as c>
                                        <option value="${c}" <#if (ticket.serviceClass)?? && ticket.serviceClass == c>selected</#if>>
                                            <#if c?string == "ECONOMY">Економ<#elseif c?string == "BUSINESS">Бізнес<#elseif c?string == "FIRST">Перший</#if>
                                        </option>
                                    </#list>
                                </select>
                            </div>
                            <div class="col-md-6">
                                <label for="status" class="form-label fw-bold">Статус</label>
                                <select class="form-select" id="status" name="status" required>
                                    <#list statuses as s>
                                        <option value="${s}" <#if (ticket.status)?? && ticket.status == s>selected</#if>>
                                            <#if s?string == "RESERVED">Заброньовано
                                            <#elseif s?string == "CONFIRMED">Підтверджено
                                            <#elseif s?string == "CANCELLED">Скасовано
                                            <#elseif s?string == "USED">Використано</#if>
                                        </option>
                                    </#list>
                                </select>
                            </div>
                        </div>

                        <div class="row mb-3">
                            <div class="col-md-12">
                                <label for="price" class="form-label fw-bold">Ціна (грн)</label>
                                <input type="number" class="form-control" id="price" name="price"
                                       value="${(ticket.price)!''}" required step="0.01" min="0.01" placeholder="2500.00">
                            </div>
                        </div>

                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label for="passengerId" class="form-label fw-bold">Пасажир</label>
                                <select class="form-select" id="passengerId" name="passengerId" required>
                                    <option value="">— оберіть пасажира —</option>
                                    <#list passengers as p>
                                        <option value="${p.id}" <#if (ticket.passenger)?? && ticket.passenger.id == p.id>selected</#if>>
                                            ${p.lastName} ${p.firstName} (${p.passportNumber})
                                        </option>
                                    </#list>
                                </select>
                            </div>
                            <div class="col-md-6">
                                <label for="flightId" class="form-label fw-bold">Рейс</label>
                                <select class="form-select" id="flightId" name="flightId" required>
                                    <option value="">— оберіть рейс —</option>
                                    <#list flights as f>
                                        <option value="${f.id}" <#if (ticket.flight)?? && ticket.flight.id == f.id>selected</#if>>
                                            ${f.flightNumber} (${f.departureCity} → ${f.arrivalCity})
                                        </option>
                                    </#list>
                                </select>
                            </div>
                        </div>

                        <hr class="my-4">
                        <div class="d-flex justify-content-between">
                            <a href="/admin/tickets" class="btn btn-outline-secondary">← Скасувати</a>
                            <button type="submit" class="btn btn-primary btn-lg">
                                <#if isNew>💾 Створити квиток<#else>💾 Зберегти зміни</#if>
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

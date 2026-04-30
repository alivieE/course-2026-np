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
                <li class="nav-item"><a class="nav-link active text-warning fw-bold" href="/admin/passengers">Пасажири</a></li>
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
                        <#if isNew>➕ Створення пасажира<#else>✏️ Редагування пасажира #${passenger.id}</#if>
                    </h1>
                </div>
                <div class="card-body p-4">
                    <form action="<#if isNew>/admin/passengers<#else>/admin/passengers/${passenger.id}</#if>" method="post">

                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label for="firstName" class="form-label fw-bold">Ім'я</label>
                                <input type="text" class="form-control" id="firstName" name="firstName"
                                       value="${(passenger.firstName)!''}" required minlength="2" maxlength="50">
                            </div>
                            <div class="col-md-6">
                                <label for="lastName" class="form-label fw-bold">Прізвище</label>
                                <input type="text" class="form-control" id="lastName" name="lastName"
                                       value="${(passenger.lastName)!''}" required minlength="2" maxlength="50">
                            </div>
                        </div>

                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label for="passportNumber" class="form-label fw-bold">Серія та номер паспорта</label>
                                <input type="text" class="form-control" id="passportNumber" name="passportNumber"
                                       value="${(passenger.passportNumber)!''}" required pattern="[A-Z0-9]+"
                                       minlength="6" maxlength="20" placeholder="AB123456">
                                <div class="form-text">Тільки великі літери та цифри</div>
                            </div>
                            <div class="col-md-6">
                                <label for="email" class="form-label fw-bold">Email</label>
                                <input type="email" class="form-control" id="email" name="email"
                                       value="${(passenger.email)!''}" required placeholder="passenger@example.com">
                            </div>
                        </div>

                        <div class="row mb-3">
                            <div class="col-md-4">
                                <label for="phone" class="form-label fw-bold">Телефон</label>
                                <input type="tel" class="form-control" id="phone" name="phone"
                                       value="${(passenger.phone)!''}" pattern="\+?[0-9]{10,15}" placeholder="+380501234567">
                            </div>
                            <div class="col-md-4">
                                <label for="dateOfBirth" class="form-label fw-bold">Дата народження</label>
                                <input type="date" class="form-control" id="dateOfBirth" name="dateOfBirth"
                                       value="${(passenger.dateOfBirth?string)!''}">
                            </div>
                            <div class="col-md-4">
                                <label for="gender" class="form-label fw-bold">Стать</label>
                                <select class="form-select" id="gender" name="gender">
                                    <option value="">— не вказано —</option>
                                    <#list genders as g>
                                        <option value="${g}" <#if (passenger.gender)?? && passenger.gender == g>selected</#if>>
                                            <#if g?string == "MALE">Чоловіча<#elseif g?string == "FEMALE">Жіноча<#else>Інше</#if>
                                        </option>
                                    </#list>
                                </select>
                            </div>
                        </div>

                        <hr class="my-4">
                        <div class="d-flex justify-content-between">
                            <a href="/admin/passengers" class="btn btn-outline-secondary">← Скасувати</a>
                            <button type="submit" class="btn btn-primary btn-lg">
                                <#if isNew>💾 Створити пасажира<#else>💾 Зберегти зміни</#if>
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

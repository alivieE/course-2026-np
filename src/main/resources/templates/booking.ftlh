<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="d-flex flex-column min-vh-100">
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
            </ul>
        </div>
    </div>
</nav>

<div class="container mt-5 mb-5">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card shadow-sm border-0">
                <div class="card-body p-5">
                    <h1 class="card-title text-primary fw-bold mb-4">🎫 Оформлення квитка</h1>
                    <p class="text-muted mb-4">
                        Заповніть форму нижче, щоб забронювати місце на обраний рейс.
                    </p>

                    <form>
                        <div class="mb-3">
                            <label for="fullName" class="form-label fw-bold">Прізвище та ім'я</label>
                            <input type="text" class="form-control" id="fullName" placeholder="Іван Петров">
                        </div>

                        <div class="mb-3">
                            <label for="email" class="form-label fw-bold">Електронна пошта</label>
                            <input type="email" class="form-control" id="email" placeholder="ivan@example.com">
                        </div>

                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label for="flightNumber" class="form-label fw-bold">Номер рейсу</label>
                                <input type="text" class="form-control" id="flightNumber" placeholder="SA-101">
                            </div>
                            <div class="col-md-6">
                                <label for="passengers" class="form-label fw-bold">Кількість пасажирів</label>
                                <select class="form-select" id="passengers">
                                    <option>1</option>
                                    <option>2</option>
                                    <option>3</option>
                                    <option>4</option>
                                </select>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Клас обслуговування</label>
                            <div class="form-check">
                                <input class="form-check-input" type="radio" name="class" id="economy" checked>
                                <label class="form-check-label" for="economy">Економ</label>
                            </div>
                            <div class="form-check">
                                <input class="form-check-input" type="radio" name="class" id="business">
                                <label class="form-check-label" for="business">Бізнес</label>
                            </div>
                        </div>

                        <div class="d-grid gap-2 mt-4">
                            <button type="submit" class="btn btn-primary btn-lg">Підтвердити бронювання ✔️</button>
                            <a href="/flights" class="btn btn-outline-secondary">← Повернутися до розкладу</a>
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

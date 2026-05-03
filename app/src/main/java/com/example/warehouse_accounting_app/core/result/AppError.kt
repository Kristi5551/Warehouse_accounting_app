package com.example.warehouse_accounting_app.core.result

/**
 * Доменная классификация ошибок — без зависимостей от сетевого слоя.
 * Используется в [AppResult.Error.appError] для передачи причины ошибки
 * в слои domain и presentation без утечки транспортных деталей.
 */
sealed class AppError(open val message: String) {
    /** Сервер вернул 401 или 403 — сессия недействительна или доступ запрещён. */
    data class Unauthorized(override val message: String) : AppError(message)

    /** Сеть недоступна, таймаут, хост не найден. */
    data class Network(override val message: String) : AppError(message)

    /** Сервер вернул 5xx. */
    data class Server(override val message: String) : AppError(message)

    /** Прочие ошибки (парсинг, неожиданные исключения и т.д.). */
    data class Unknown(override val message: String) : AppError(message)
}

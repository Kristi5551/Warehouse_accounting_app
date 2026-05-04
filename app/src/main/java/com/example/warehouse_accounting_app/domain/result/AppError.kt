package com.example.warehouse_accounting_app.domain.result

/**
 * Доменная классификация ошибок — без зависимостей от сетевого слоя.
 * Используется в [AppResult.Error.appError] для передачи причины ошибки
 * в слои domain и presentation без утечки транспортных деталей.
 */
sealed class AppError(open val message: String) {
    /**
     * 401 или иная ситуация, когда с точки зрения приложения нужен новый вход.
     * Для [AuthRepository.getCurrentUser] предпочтительнее [SessionExpired], если токен уже очищен.
     */
    data class Unauthorized(override val message: String) : AppError(message)

    /**
     * Недействительный/просроченный токен либо учётная запись больше не может использоваться (типично 401 или 403 на /me).
     * Репозиторий при этом уже может очистить токен.
     */
    data class SessionExpired(override val message: String) : AppError(message)

    /**
     * 403 без признаков «сессия мёртва»: токен не очищается автоматически (например нестандартный ответ /me).
     */
    data class Forbidden(override val message: String) : AppError(message)

    /** Сеть недоступна, таймаут, хост не найден. */
    data class Network(override val message: String) : AppError(message)

    /** Сервер вернул 5xx. */
    data class Server(override val message: String) : AppError(message)

    /** Прочие ошибки (парсинг, неожиданные исключения и т.д.). */
    data class Unknown(override val message: String) : AppError(message)
}

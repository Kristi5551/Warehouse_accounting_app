package com.example.warehouse_accounting_app.domain.usecase.auth

import com.example.warehouse_accounting_app.domain.model.User

/**
 * Результат проверки текущего состояния сессии (Splash-экран).
 *
 * - [Authenticated]   — /api/auth/me успешно подтвердил пользователя.
 * - [Unauthenticated] — токена нет, или сервер вернул 401/403.
 * - [NetworkError]    — сервер недоступен, нет сети, таймаут.
 * - [UnknownError]    — прочая ошибка (парсинг, неожиданное исключение).
 */
sealed interface AuthCheckResult {
    data class Authenticated(val user: User) : AuthCheckResult
    data object Unauthenticated : AuthCheckResult
    data class NetworkError(val message: String) : AuthCheckResult
    data class UnknownError(val message: String) : AuthCheckResult
}

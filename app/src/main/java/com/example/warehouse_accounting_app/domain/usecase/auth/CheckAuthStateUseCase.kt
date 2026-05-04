package com.example.warehouse_accounting_app.domain.usecase.auth

import com.example.warehouse_accounting_app.domain.result.AppError
import com.example.warehouse_accounting_app.domain.result.AppResult
import com.example.warehouse_accounting_app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.first

/**
 * Проверяет, авторизован ли пользователь при старте приложения.
 *
 * Алгоритм:
 * 1. Если токена нет — [AuthCheckResult.Unauthenticated].
 * 2. Если токен есть — вызывает /api/auth/me.
 * 3. Успех — [AuthCheckResult.Authenticated].
 * 4. 401 на /me или 403 «аккаунт недоступен» — токен очищен в репозитории, [Unauthenticated].
 * 5. Иной 403 на /me — токен не трогаем, [UnknownError] (редко).
 * 6. Сетевая ошибка — [NetworkError] (токен сохранён).
 * 7. 5xx и прочее — [UnknownError] (токен сохранён).
 *
 * Зависимости: только domain-репозиторий + domain.result — без core.network.
 */
class CheckAuthStateUseCase(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(): AuthCheckResult {
        val token = repository.observeToken().first()
        if (token.isNullOrBlank()) return AuthCheckResult.Unauthenticated

        return when (val result = repository.getCurrentUser()) {
            is AppResult.Success -> AuthCheckResult.Authenticated(result.data)
            is AppResult.Error -> when (result.appError) {
                is AppError.SessionExpired, is AppError.Unauthorized -> {
                    repository.logout()
                    AuthCheckResult.Unauthenticated
                }
                is AppError.Forbidden -> AuthCheckResult.UnknownError(result.message)
                is AppError.Network -> AuthCheckResult.NetworkError(result.message)
                is AppError.Server -> AuthCheckResult.UnknownError(result.message)
                is AppError.Unknown, null -> AuthCheckResult.UnknownError(result.message)
            }
        }
    }
}

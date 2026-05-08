package com.example.warehouse_accounting_app.domain.usecase.auth

import com.example.warehouse_accounting_app.domain.result.AppError
import com.example.warehouse_accounting_app.domain.result.AppResult
import com.example.warehouse_accounting_app.domain.repository.AuthRepository

class CheckAuthStateUseCase(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(): AuthCheckResult {
        val token = repository.getTokenOnce()
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
                is AppError.Validation, is AppError.NotFound, is AppError.Conflict, is AppError.Unknown ->
                    AuthCheckResult.UnknownError(result.message)
            }
        }
    }
}

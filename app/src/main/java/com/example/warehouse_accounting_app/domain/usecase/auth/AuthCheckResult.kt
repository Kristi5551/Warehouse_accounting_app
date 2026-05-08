package com.example.warehouse_accounting_app.domain.usecase.auth

import com.example.warehouse_accounting_app.domain.model.User

sealed interface AuthCheckResult {
    data class Authenticated(val user: User) : AuthCheckResult
    data object Unauthenticated : AuthCheckResult
    data class NetworkError(val message: String) : AuthCheckResult
    data class UnknownError(val message: String) : AuthCheckResult
}

package com.example.warehouse_accounting_app.domain.usecase.auth

import com.example.warehouse_accounting_app.core.result.AppResult
import com.example.warehouse_accounting_app.domain.model.User
import com.example.warehouse_accounting_app.domain.repository.AuthRepository

class LoginUseCase(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(email: String, password: String): AppResult<User> {
        if (email.isBlank()) return AppResult.Error("Введите email")
        if (password.isBlank()) return AppResult.Error("Введите пароль")
        return repository.login(email.trim(), password)
    }
}

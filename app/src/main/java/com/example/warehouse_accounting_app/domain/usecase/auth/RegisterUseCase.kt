package com.example.warehouse_accounting_app.domain.usecase.auth

import com.example.warehouse_accounting_app.core.result.AppResult
import com.example.warehouse_accounting_app.domain.model.User
import com.example.warehouse_accounting_app.domain.model.UserRole
import com.example.warehouse_accounting_app.domain.repository.AuthRepository

class RegisterUseCase(
    private val repository: AuthRepository,
) {
    private val emailRegex = Regex("^[A-Za-z0-9+._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    suspend operator fun invoke(
        fullName: String,
        email: String,
        password: String,
        repeatPassword: String,
        requestedRole: UserRole,
    ): AppResult<User> {
        if (fullName.isBlank()) return AppResult.Error("Введите ФИО")
        if (email.isBlank()) return AppResult.Error("Введите email")
        val trimmedEmail = email.trim()
        if (!trimmedEmail.matches(emailRegex)) {
            return AppResult.Error("Некорректный формат email")
        }
        if (password.length < 6) {
            return AppResult.Error("Пароль должен содержать минимум 6 символов")
        }
        if (password != repeatPassword) {
            return AppResult.Error("Пароли не совпадают")
        }
        if (requestedRole != UserRole.STOREKEEPER && requestedRole != UserRole.MANAGER) {
            return AppResult.Error("Регистрация с ролью администратора недоступна")
        }
        return repository.register(
            fullName = fullName.trim(),
            email = trimmedEmail.lowercase(),
            password = password,
            requestedRole = requestedRole,
        )
    }
}

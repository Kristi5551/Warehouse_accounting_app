package com.example.warehouse_accounting_app.domain.usecase.auth

import com.example.warehouse_accounting_app.domain.result.AppResult
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
        if (fullName.isBlank()) return AppResult.validation("Введите ФИО")
        if (email.isBlank()) return AppResult.validation("Введите email")
        val trimmedEmail = email.trim()
        if (!trimmedEmail.matches(emailRegex)) {
            return AppResult.validation("Некорректный формат email")
        }
        if (password.length < 6) {
            return AppResult.validation("Пароль должен содержать минимум 6 символов")
        }
        if (password != repeatPassword) {
            return AppResult.validation("Пароли не совпадают")
        }
        if (requestedRole != UserRole.STOREKEEPER && requestedRole != UserRole.MANAGER) {
            return AppResult.validation("Регистрация с ролью администратора недоступна")
        }
        return repository.register(
            fullName = fullName.trim(),
            email = trimmedEmail.lowercase(),
            password = password,
            requestedRole = requestedRole,
        )
    }
}

package com.example.warehouse_accounting_app.domain.usecase.auth

import com.example.warehouse_accounting_app.domain.model.UserRole
import com.example.warehouse_accounting_app.domain.repository.AuthRepository

class RegisterUseCase(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(fullName: String, email: String, password: String, requestedRole: UserRole) =
        repository.register(fullName, email, password, requestedRole)
}

package com.example.warehouse_accounting_app.domain.usecase.auth

import com.example.warehouse_accounting_app.domain.repository.AuthRepository

class LoginUseCase(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(email: String, password: String) = repository.login(email, password)
}

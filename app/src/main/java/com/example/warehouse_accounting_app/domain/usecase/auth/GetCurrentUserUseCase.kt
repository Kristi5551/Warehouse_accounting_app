package com.example.warehouse_accounting_app.domain.usecase.auth

import com.example.warehouse_accounting_app.core.result.AppResult
import com.example.warehouse_accounting_app.domain.model.User
import com.example.warehouse_accounting_app.domain.repository.AuthRepository

class GetCurrentUserUseCase(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(): AppResult<User> = repository.getCurrentUser()
}

package com.example.warehouse_accounting_app.domain.usecase.auth

import com.example.warehouse_accounting_app.core.result.AppResult
import com.example.warehouse_accounting_app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.first

class CheckAuthStateUseCase(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(): Boolean {
        val token = repository.observeToken().first()
        if (token.isNullOrBlank()) return false
        return when (val r = repository.getCurrentUser()) {
            is AppResult.Success -> true
            is AppResult.Error -> {
                repository.logout()
                false
            }
        }
    }
}

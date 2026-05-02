package com.example.warehouse_accounting_app.domain.usecase.auth

import com.example.warehouse_accounting_app.core.network.ApiException
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
                val t = r.throwable
                val unauthorized = t is ApiException && (t.statusCode == 401 || t.statusCode == 403)
                if (unauthorized) {
                    repository.logout()
                    false
                } else {
                    // Сеть, таймаут, 5xx — токен в DataStore не трогаем (getCurrentUser уже чистит только 401/403).
                    true
                }
            }
        }
    }
}

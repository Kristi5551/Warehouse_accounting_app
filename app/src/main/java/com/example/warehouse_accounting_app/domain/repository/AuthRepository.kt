package com.example.warehouse_accounting_app.domain.repository

import com.example.warehouse_accounting_app.domain.result.AppResult
import com.example.warehouse_accounting_app.domain.model.User
import com.example.warehouse_accounting_app.domain.model.UserRole
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): AppResult<User>
    suspend fun register(
        fullName: String,
        email: String,
        password: String,
        requestedRole: UserRole,
    ): AppResult<User>

    suspend fun getCurrentUser(): AppResult<User>
    suspend fun logout()
    fun observeToken(): Flow<String?>

    /** Однократное чтение токена без подписки на Flow. */
    suspend fun getTokenOnce(): String?
}

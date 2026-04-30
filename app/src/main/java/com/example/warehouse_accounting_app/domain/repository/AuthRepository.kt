package com.example.warehouse_accounting_app.domain.repository

import com.example.warehouse_accounting_app.domain.model.User
import com.example.warehouse_accounting_app.domain.model.UserRole

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Pair<String, User>>
    suspend fun register(fullName: String, email: String, password: String, requestedRole: UserRole): Result<Pair<String, User>>
    suspend fun logout()
    suspend fun getCurrentUser(): Result<User>
}

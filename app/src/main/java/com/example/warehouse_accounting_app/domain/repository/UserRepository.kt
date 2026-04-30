package com.example.warehouse_accounting_app.domain.repository

import com.example.warehouse_accounting_app.domain.model.User
import com.example.warehouse_accounting_app.domain.model.UserRole

interface UserRepository {
    suspend fun getUsers(): Result<List<User>>
    suspend fun getPendingUsers(): Result<List<User>>
    suspend fun approveUser(id: Long): Result<Unit>
    suspend fun blockUser(id: Long): Result<Unit>
    suspend fun unblockUser(id: Long): Result<Unit>
    suspend fun changeRole(id: Long, role: UserRole): Result<Unit>
}

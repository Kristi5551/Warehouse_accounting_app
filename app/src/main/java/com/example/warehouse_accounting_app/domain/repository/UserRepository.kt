package com.example.warehouse_accounting_app.domain.repository

import com.example.warehouse_accounting_app.core.result.AppResult
import com.example.warehouse_accounting_app.domain.model.User
import com.example.warehouse_accounting_app.domain.model.UserPick
import com.example.warehouse_accounting_app.domain.model.UserRole

interface UserRepository {
    suspend fun getUsers(): AppResult<List<User>>
    suspend fun getPendingUsers(): AppResult<List<User>>
    suspend fun approveUser(id: Long): AppResult<User>
    suspend fun blockUser(id: Long): AppResult<User>
    suspend fun unblockUser(id: Long): AppResult<User>
    suspend fun changeUserRole(id: Long, role: UserRole): AppResult<User>
    suspend fun createAdmin(fullName: String, email: String, password: String): AppResult<User>

    suspend fun getUsersForOperationFilters(): AppResult<List<UserPick>>
}

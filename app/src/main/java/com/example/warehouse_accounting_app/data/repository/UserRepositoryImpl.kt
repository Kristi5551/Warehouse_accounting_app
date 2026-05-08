package com.example.warehouse_accounting_app.data.repository

import com.example.warehouse_accounting_app.core.datastore.AuthDataStore
import com.example.warehouse_accounting_app.core.network.ApiException
import com.example.warehouse_accounting_app.core.network.logApiException
import com.example.warehouse_accounting_app.core.network.logNetworkFailure
import com.example.warehouse_accounting_app.data.mapper.apiFailure
import com.example.warehouse_accounting_app.data.mapper.networkFailure
import com.example.warehouse_accounting_app.data.mapper.unknownFailure
import com.example.warehouse_accounting_app.data.mapper.toDomain
import com.example.warehouse_accounting_app.data.mapper.toUserPick
import com.example.warehouse_accounting_app.data.remote.api.UserApi
import com.example.warehouse_accounting_app.data.remote.dto.request.CreateAdminUserRequestDto
import com.example.warehouse_accounting_app.domain.model.User
import com.example.warehouse_accounting_app.domain.model.UserPick
import com.example.warehouse_accounting_app.domain.model.UserRole
import com.example.warehouse_accounting_app.domain.repository.UserRepository
import com.example.warehouse_accounting_app.domain.result.AppResult
import java.io.IOException

class UserRepositoryImpl(
    private val api: UserApi,
    private val authDataStore: AuthDataStore,
) : UserRepository {

    private suspend fun handleUnauthorized(e: ApiException) {
        if (e.statusCode == 401) {
            authDataStore.clearToken()
            api.clearCachedAuthTokens()
        }
    }

    override suspend fun getUsers(): AppResult<List<User>> =
        try {
            AppResult.Success(api.getUsers().map { it.toDomain() })
        } catch (e: ApiException) {
            logApiException(e, "GET /api/users")
            handleUnauthorized(e)
            apiFailure(e)
        } catch (e: IOException) {
            logNetworkFailure(e, "GET /api/users")
            networkFailure(e, "Не удалось загрузить пользователей")
        } catch (e: Exception) {
            logNetworkFailure(e, "GET /api/users")
            unknownFailure(e, "Не удалось загрузить пользователей")
        }

    override suspend fun getPendingUsers(): AppResult<List<User>> =
        try {
            AppResult.Success(api.getPendingUsers().map { it.toDomain() })
        } catch (e: ApiException) {
            logApiException(e, "GET /api/users/pending")
            handleUnauthorized(e)
            apiFailure(e)
        } catch (e: IOException) {
            logNetworkFailure(e, "GET /api/users/pending")
            networkFailure(e, "Не удалось загрузить пользователей")
        } catch (e: Exception) {
            logNetworkFailure(e, "GET /api/users/pending")
            unknownFailure(e, "Не удалось загрузить пользователей")
        }

    override suspend fun approveUser(id: Long): AppResult<User> =
        try {
            AppResult.Success(api.approveUser(id).toDomain())
        } catch (e: ApiException) {
            logApiException(e, "PATCH /api/users/$id/approve")
            handleUnauthorized(e)
            apiFailure(e)
        } catch (e: IOException) {
            logNetworkFailure(e, "PATCH /api/users/$id/approve")
            networkFailure(e, "Не удалось подтвердить пользователя")
        } catch (e: Exception) {
            logNetworkFailure(e, "PATCH /api/users/$id/approve")
            unknownFailure(e, "Не удалось подтвердить пользователя")
        }

    override suspend fun blockUser(id: Long): AppResult<User> =
        try {
            AppResult.Success(api.blockUser(id).toDomain())
        } catch (e: ApiException) {
            logApiException(e, "PATCH /api/users/$id/block")
            handleUnauthorized(e)
            apiFailure(e)
        } catch (e: IOException) {
            logNetworkFailure(e, "PATCH /api/users/$id/block")
            networkFailure(e, "Не удалось заблокировать пользователя")
        } catch (e: Exception) {
            logNetworkFailure(e, "PATCH /api/users/$id/block")
            unknownFailure(e, "Не удалось заблокировать пользователя")
        }

    override suspend fun unblockUser(id: Long): AppResult<User> =
        try {
            AppResult.Success(api.unblockUser(id).toDomain())
        } catch (e: ApiException) {
            logApiException(e, "PATCH /api/users/$id/unblock")
            handleUnauthorized(e)
            apiFailure(e)
        } catch (e: IOException) {
            logNetworkFailure(e, "PATCH /api/users/$id/unblock")
            networkFailure(e, "Не удалось разблокировать пользователя")
        } catch (e: Exception) {
            logNetworkFailure(e, "PATCH /api/users/$id/unblock")
            unknownFailure(e, "Не удалось разблокировать пользователя")
        }

    override suspend fun changeUserRole(id: Long, role: UserRole): AppResult<User> =
        try {
            AppResult.Success(api.changeUserRole(id, role.name).toDomain())
        } catch (e: ApiException) {
            logApiException(e, "PATCH /api/users/$id/role")
            handleUnauthorized(e)
            apiFailure(e)
        } catch (e: IOException) {
            logNetworkFailure(e, "PATCH /api/users/$id/role")
            networkFailure(e, "Не удалось изменить роль")
        } catch (e: Exception) {
            logNetworkFailure(e, "PATCH /api/users/$id/role")
            unknownFailure(e, "Не удалось изменить роль")
        }

    override suspend fun createAdmin(fullName: String, email: String, password: String): AppResult<User> =
        try {
            AppResult.Success(api.createAdmin(CreateAdminUserRequestDto(fullName, email, password)).toDomain())
        } catch (e: ApiException) {
            logApiException(e, "POST /api/users/admin")
            handleUnauthorized(e)
            apiFailure(e)
        } catch (e: IOException) {
            logNetworkFailure(e, "POST /api/users/admin")
            networkFailure(e, "Не удалось создать администратора")
        } catch (e: Exception) {
            logNetworkFailure(e, "POST /api/users/admin")
            unknownFailure(e, "Не удалось создать администратора")
        }

    override suspend fun getUsersForOperationFilters(): AppResult<List<UserPick>> =
        try {
            AppResult.Success(api.getUsersForOperationFilters().map { it.toUserPick() })
        } catch (e: ApiException) {
            logApiException(e, "GET /api/users/for-operation-filters")
            handleUnauthorized(e)
            apiFailure(e)
        } catch (e: IOException) {
            logNetworkFailure(e, "GET /api/users/for-operation-filters")
            networkFailure(e, "Не удалось загрузить список пользователей")
        } catch (e: Exception) {
            logNetworkFailure(e, "GET /api/users/for-operation-filters")
            unknownFailure(e, "Не удалось загрузить список пользователей")
        }
}

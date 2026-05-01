package com.example.warehouse_accounting_app.data.repository

import com.example.warehouse_accounting_app.core.datastore.AuthDataStore
import com.example.warehouse_accounting_app.core.network.ApiException
import com.example.warehouse_accounting_app.core.network.connectivityMessage
import com.example.warehouse_accounting_app.core.network.logApiException
import com.example.warehouse_accounting_app.core.network.logNetworkFailure
import com.example.warehouse_accounting_app.core.result.AppResult
import com.example.warehouse_accounting_app.data.mapper.toDomain
import com.example.warehouse_accounting_app.data.remote.api.UserApi
import com.example.warehouse_accounting_app.domain.model.User
import com.example.warehouse_accounting_app.domain.model.UserRole
import com.example.warehouse_accounting_app.domain.repository.UserRepository
import java.io.IOException

class UserRepositoryImpl(
    private val api: UserApi,
    private val authDataStore: AuthDataStore,
) : UserRepository {

    private suspend fun handleUnauthorized(e: ApiException) {
        if (e.statusCode == 401) {
            authDataStore.clearToken()
        }
    }

    override suspend fun getUsers(): AppResult<List<User>> =
        try {
            AppResult.Success(api.getUsers().map { it.toDomain() })
        } catch (e: ApiException) {
            logApiException(e, "GET /api/users")
            handleUnauthorized(e)
            AppResult.Error(e.message ?: "Не удалось загрузить пользователей", e)
        } catch (e: IOException) {
            logNetworkFailure(e, "GET /api/users")
            AppResult.Error(connectivityMessage(e), e)
        } catch (e: Exception) {
            logNetworkFailure(e, "GET /api/users")
            AppResult.Error("Не удалось загрузить пользователей", e)
        }

    override suspend fun getPendingUsers(): AppResult<List<User>> =
        try {
            AppResult.Success(api.getPendingUsers().map { it.toDomain() })
        } catch (e: ApiException) {
            logApiException(e, "GET /api/users/pending")
            handleUnauthorized(e)
            AppResult.Error(e.message ?: "Не удалось загрузить пользователей", e)
        } catch (e: IOException) {
            logNetworkFailure(e, "GET /api/users/pending")
            AppResult.Error(connectivityMessage(e), e)
        } catch (e: Exception) {
            logNetworkFailure(e, "GET /api/users/pending")
            AppResult.Error("Не удалось загрузить пользователей", e)
        }

    override suspend fun approveUser(id: Long): AppResult<User> =
        try {
            AppResult.Success(api.approveUser(id).toDomain())
        } catch (e: ApiException) {
            logApiException(e, "PATCH /api/users/$id/approve")
            handleUnauthorized(e)
            AppResult.Error(e.message ?: "Не удалось подтвердить пользователя", e)
        } catch (e: IOException) {
            logNetworkFailure(e, "PATCH /api/users/$id/approve")
            AppResult.Error(connectivityMessage(e), e)
        } catch (e: Exception) {
            logNetworkFailure(e, "PATCH /api/users/$id/approve")
            AppResult.Error("Не удалось подтвердить пользователя", e)
        }

    override suspend fun blockUser(id: Long): AppResult<User> =
        try {
            AppResult.Success(api.blockUser(id).toDomain())
        } catch (e: ApiException) {
            logApiException(e, "PATCH /api/users/$id/block")
            handleUnauthorized(e)
            AppResult.Error(e.message ?: "Не удалось заблокировать пользователя", e)
        } catch (e: IOException) {
            logNetworkFailure(e, "PATCH /api/users/$id/block")
            AppResult.Error(connectivityMessage(e), e)
        } catch (e: Exception) {
            logNetworkFailure(e, "PATCH /api/users/$id/block")
            AppResult.Error("Не удалось заблокировать пользователя", e)
        }

    override suspend fun unblockUser(id: Long): AppResult<User> =
        try {
            AppResult.Success(api.unblockUser(id).toDomain())
        } catch (e: ApiException) {
            logApiException(e, "PATCH /api/users/$id/unblock")
            handleUnauthorized(e)
            AppResult.Error(e.message ?: "Не удалось разблокировать пользователя", e)
        } catch (e: IOException) {
            logNetworkFailure(e, "PATCH /api/users/$id/unblock")
            AppResult.Error(connectivityMessage(e), e)
        } catch (e: Exception) {
            logNetworkFailure(e, "PATCH /api/users/$id/unblock")
            AppResult.Error("Не удалось разблокировать пользователя", e)
        }

    override suspend fun changeUserRole(id: Long, role: UserRole): AppResult<User> =
        try {
            AppResult.Success(api.changeUserRole(id, role.name).toDomain())
        } catch (e: ApiException) {
            logApiException(e, "PATCH /api/users/$id/role")
            handleUnauthorized(e)
            AppResult.Error(e.message ?: "Не удалось изменить роль", e)
        } catch (e: IOException) {
            logNetworkFailure(e, "PATCH /api/users/$id/role")
            AppResult.Error(connectivityMessage(e), e)
        } catch (e: Exception) {
            logNetworkFailure(e, "PATCH /api/users/$id/role")
            AppResult.Error("Не удалось изменить роль", e)
        }
}

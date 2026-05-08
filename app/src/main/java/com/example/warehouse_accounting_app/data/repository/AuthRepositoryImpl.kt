package com.example.warehouse_accounting_app.data.repository

import com.example.warehouse_accounting_app.core.datastore.AuthDataStore
import com.example.warehouse_accounting_app.core.network.ApiException
import com.example.warehouse_accounting_app.core.network.logApiException
import com.example.warehouse_accounting_app.core.network.logNetworkFailure
import com.example.warehouse_accounting_app.core.util.AppLogger
import com.example.warehouse_accounting_app.data.mapper.apiFailure
import com.example.warehouse_accounting_app.data.mapper.networkFailure
import com.example.warehouse_accounting_app.data.mapper.unknownFailure
import com.example.warehouse_accounting_app.data.remote.api.AuthApi
import com.example.warehouse_accounting_app.data.remote.dto.request.LoginRequestDto
import com.example.warehouse_accounting_app.data.remote.dto.request.RegisterRequestDto
import com.example.warehouse_accounting_app.data.mapper.toDomain
import com.example.warehouse_accounting_app.domain.model.User
import com.example.warehouse_accounting_app.domain.model.UserRole
import com.example.warehouse_accounting_app.domain.repository.AuthRepository
import com.example.warehouse_accounting_app.domain.result.AppError
import com.example.warehouse_accounting_app.domain.result.AppResult
import kotlinx.coroutines.flow.Flow
import java.io.IOException

private const val AUTH_LOG_TAG = "WarehouseAuth"

class AuthRepositoryImpl(
    private val api: AuthApi,
    private val authDataStore: AuthDataStore,
    private val logger: AppLogger,
) : AuthRepository {
    override suspend fun login(email: String, password: String): AppResult<User> =
        try {
            val response = api.login(LoginRequestDto(email = email, password = password))
            val received = response.token.isNotBlank()
            logger.d(AUTH_LOG_TAG, "login token received = $received")
            if (!received) {
                val msg = "Сервер не вернул токен"
                AppResult.Error(msg, AppError.Unknown(msg))
            } else {
                authDataStore.saveToken(response.token)
                api.clearCachedAuthTokens()
                val saved = !authDataStore.getTokenOnce().isNullOrBlank()
                logger.d(AUTH_LOG_TAG, "token saved = $saved")
                AppResult.Success(response.user.toDomain())
            }
        } catch (e: ApiException) {
            logApiException(e, "POST /api/auth/login")
            apiFailure(e)
        } catch (e: IOException) {
            logNetworkFailure(e, "POST /api/auth/login")
            networkFailure(e)
        } catch (e: Exception) {
            logNetworkFailure(e, "POST /api/auth/login")
            unknownFailure(e)
        }

    override suspend fun register(
        fullName: String,
        email: String,
        password: String,
        requestedRole: UserRole,
    ): AppResult<User> =
        try {
            val user =
                api.register(
                    RegisterRequestDto(
                        fullName = fullName,
                        email = email,
                        password = password,
                        requestedRole = requestedRole.name,
                    ),
                ).toDomain()
            AppResult.Success(user)
        } catch (e: ApiException) {
            logApiException(e, "POST /api/auth/register")
            apiFailure(e)
        } catch (e: IOException) {
            logNetworkFailure(e, "POST /api/auth/register")
            networkFailure(e)
        } catch (e: Exception) {
            logNetworkFailure(e, "POST /api/auth/register")
            unknownFailure(e)
        }

    override suspend fun getCurrentUser(): AppResult<User> =
        try {
            val available = !authDataStore.getTokenOnce().isNullOrBlank()
            logger.d(AUTH_LOG_TAG, "token available for request = $available")
            AppResult.Success(api.me().toDomain())
        } catch (e: ApiException) {
            logApiException(e, "GET /api/auth/me")
            val msg = e.message?.trim().orEmpty().ifEmpty { "Не удалось получить данные пользователя" }
            when (e.statusCode) {
                401 -> {
                    authDataStore.clearToken()
                    api.clearCachedAuthTokens()
                    val text = msg.ifBlank { "Сессия истекла. Войдите снова." }
                    AppResult.Error(text, AppError.SessionExpired(text))
                }
                403 -> {
                    if (msg.meansAccountNoLongerActiveForSession()) {
                        authDataStore.clearToken()
                        api.clearCachedAuthTokens()
                        val text = msg.ifBlank { "Сессия истекла." }
                        AppResult.Error(text, AppError.SessionExpired(text))
                    } else {
                        AppResult.Error(msg, AppError.Forbidden(msg))
                    }
                }
                else -> apiFailure(e, msg)
            }
        } catch (e: IOException) {
            logNetworkFailure(e, "GET /api/auth/me")
            networkFailure(e)
        } catch (e: Exception) {
            logNetworkFailure(e, "GET /api/auth/me")
            unknownFailure(e)
        }

    override suspend fun logout() {
        authDataStore.clearToken()
        api.clearCachedAuthTokens()
    }

    override fun observeToken(): Flow<String?> = authDataStore.observeToken()

    override suspend fun getTokenOnce(): String? = authDataStore.getTokenOnce()
}

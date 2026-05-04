package com.example.warehouse_accounting_app.data.repository

import android.util.Log
import com.example.warehouse_accounting_app.BuildConfig
import com.example.warehouse_accounting_app.core.datastore.AuthDataStore
import com.example.warehouse_accounting_app.core.network.ApiException
import com.example.warehouse_accounting_app.core.network.connectivityMessage
import com.example.warehouse_accounting_app.core.network.logApiException
import com.example.warehouse_accounting_app.core.network.logNetworkFailure
import com.example.warehouse_accounting_app.domain.result.AppError
import com.example.warehouse_accounting_app.domain.result.AppResult
import com.example.warehouse_accounting_app.data.mapper.toDomain
import com.example.warehouse_accounting_app.data.remote.api.AuthApi
import com.example.warehouse_accounting_app.data.remote.dto.request.LoginRequestDto
import com.example.warehouse_accounting_app.data.remote.dto.request.RegisterRequestDto
import com.example.warehouse_accounting_app.domain.model.User
import com.example.warehouse_accounting_app.domain.model.UserRole
import com.example.warehouse_accounting_app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import java.io.IOException

private const val AUTH_LOG_TAG = "WarehouseAuth"

class AuthRepositoryImpl(
    private val api: AuthApi,
    private val authDataStore: AuthDataStore,
) : AuthRepository {
    override suspend fun login(email: String, password: String): AppResult<User> =
        try {
            val response = api.login(LoginRequestDto(email = email, password = password))
            val received = response.token.isNotBlank()
            if (BuildConfig.DEBUG) {
                Log.d(AUTH_LOG_TAG, "login token received = $received")
            }
            if (!received) {
                AppResult.Error("Сервер не вернул токен", IllegalStateException("empty token"))
            } else {
                authDataStore.saveToken(response.token)
                api.clearCachedAuthTokens()
                if (BuildConfig.DEBUG) {
                    val saved = !authDataStore.getTokenOnce().isNullOrBlank()
                    Log.d(AUTH_LOG_TAG, "token saved = $saved")
                }
                AppResult.Success(response.user.toDomain())
            }
        } catch (e: ApiException) {
            logApiException(e, "POST /api/auth/login")
            AppResult.Error(e.message ?: "Ошибка входа", e)
        } catch (e: IOException) {
            logNetworkFailure(e, "POST /api/auth/login")
            AppResult.Error(connectivityMessage(e), e)
        } catch (e: Exception) {
            logNetworkFailure(e, "POST /api/auth/login")
            AppResult.Error(connectivityMessage(e), e)
        }

    override suspend fun register(
        fullName: String,
        email: String,
        password: String,
        requestedRole: UserRole,
    ): AppResult<User> =
        try {
            val user = api.register(
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
            AppResult.Error(e.message ?: "Ошибка регистрации", e)
        } catch (e: IOException) {
            logNetworkFailure(e, "POST /api/auth/register")
            AppResult.Error(connectivityMessage(e), e)
        } catch (e: Exception) {
            logNetworkFailure(e, "POST /api/auth/register")
            AppResult.Error(connectivityMessage(e), e)
        }

    override suspend fun getCurrentUser(): AppResult<User> =
        try {
            if (BuildConfig.DEBUG) {
                val available = !authDataStore.getTokenOnce().isNullOrBlank()
                Log.d(AUTH_LOG_TAG, "token available for request = $available")
            }
            AppResult.Success(api.me().toDomain())
        } catch (e: ApiException) {
            logApiException(e, "GET /api/auth/me")
            val msg = e.message ?: "Не удалось получить данные пользователя"
            when (e.statusCode) {
                401 -> {
                    authDataStore.clearToken()
                    api.clearCachedAuthTokens()
                    AppResult.Error(
                        message = if (msg.isNotBlank()) msg else "Сессия истекла. Войдите снова.",
                        throwable = e,
                        appError = AppError.SessionExpired(msg.ifBlank { "Сессия истекла. Войдите снова." }),
                    )
                }
                403 -> {
                    if (msg.meansAccountNoLongerActiveForSession()) {
                        authDataStore.clearToken()
                        api.clearCachedAuthTokens()
                        AppResult.Error(msg, e, appError = AppError.SessionExpired(msg))
                    } else {
                        AppResult.Error(msg, e, appError = AppError.Forbidden(msg))
                    }
                }
                in 500..599 ->
                    AppResult.Error(msg, e, appError = AppError.Server(msg))
                else ->
                    AppResult.Error(msg, e, appError = AppError.Unknown(msg))
            }
        } catch (e: IOException) {
            logNetworkFailure(e, "GET /api/auth/me")
            val msg = connectivityMessage(e)
            AppResult.Error(msg, e, appError = AppError.Network(msg))
        } catch (e: Exception) {
            logNetworkFailure(e, "GET /api/auth/me")
            val msg = e.message ?: "Не удалось получить данные пользователя"
            AppResult.Error(msg, e, appError = AppError.Unknown(msg))
        }

    override suspend fun logout() {
        authDataStore.clearToken()
        api.clearCachedAuthTokens()
    }

    override fun observeToken(): Flow<String?> = authDataStore.observeToken()
}

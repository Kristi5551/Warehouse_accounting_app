package com.example.warehouse_accounting_app.data.repository

import com.example.warehouse_accounting_app.core.datastore.AuthDataStore
import com.example.warehouse_accounting_app.data.mapper.toDomain
import com.example.warehouse_accounting_app.data.remote.api.AuthApi
import com.example.warehouse_accounting_app.data.remote.dto.request.LoginRequestDto
import com.example.warehouse_accounting_app.data.remote.dto.request.RegisterRequestDto
import com.example.warehouse_accounting_app.domain.model.User
import com.example.warehouse_accounting_app.domain.model.UserRole
import com.example.warehouse_accounting_app.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val api: AuthApi,
    private val authDataStore: AuthDataStore,
) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<Pair<String, User>> = runCatching {
        val response = api.login(LoginRequestDto(email = email, password = password))
        authDataStore.saveToken(response.token)
        response.token to response.user.toDomain()
    }

    override suspend fun register(
        fullName: String,
        email: String,
        password: String,
        requestedRole: UserRole,
    ): Result<Pair<String, User>> = runCatching {
        val response = api.register(
            RegisterRequestDto(
                fullName = fullName,
                email = email,
                password = password,
                requestedRole = requestedRole.name,
            ),
        )
        authDataStore.saveToken(response.token)
        response.token to response.user.toDomain()
    }

    override suspend fun logout() {
        authDataStore.clearToken()
    }

    override suspend fun getCurrentUser(): Result<User> = runCatching {
        api.me().toDomain()
    }
}
